using System;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;

namespace VertexCache.Sdk
{
    public class VertexCacheSdk : IVCacheClient
    {
        private readonly VertexCacheSdkOptions _options;
        private readonly ILogger<VertexCacheSdk> _logger;

        public VertexCacheSdk(VertexCacheSdkOptions options, ILogger<VertexCacheSdk> logger)
        {
            _options = options;
            _logger = logger;
        }

        public async Task<VCacheResult> RunCommandAsync(string command, string[] args)
        {
            if (string.IsNullOrWhiteSpace(command))
                return VCacheResult.Fail(VCacheErrorCode.InvalidCommand, "Empty command");

            string rawCommand = FormatCommand(command, args);

            if (_options.EnableEncryption && !string.IsNullOrWhiteSpace(_options.PublicKey))
            {
                try
                {
                    rawCommand = EncryptionHelper.Encrypt(rawCommand, _options.PublicKey);
                }
                catch (Exception ex)
                {
                    return VCacheResult.Fail(VCacheErrorCode.EncryptionError, $"Failed to encrypt command: {ex.Message}");
                }
            }

            try
            {
                using var client = new VCacheTcpClient(
                    _options.ServerHost,
                    _options.ServerPort,
                    _options.EnableEncryptionTransport,
                    _options.EnableVerifyCertificate,
                    _options.CertificatePem,
                    _options.TimeoutMs
                );

                var response = await client.SendAsync(rawCommand);
                return VCacheResult.Ok(response);
            }
            catch (TimeoutException)
            {
                return VCacheResult.Fail(VCacheErrorCode.Timeout, "Connection timed out");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error during command execution");
                return VCacheResult.Fail(VCacheErrorCode.NetworkFailure, ex.Message);
            }
        }

        private string FormatCommand(string command, string[] args)
        {
            return args.Length > 0 ? $"{command} {string.Join(' ', args)}" : command;
        }
    }
}
