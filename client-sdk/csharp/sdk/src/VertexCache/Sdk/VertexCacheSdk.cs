using System;
using System.IO;
using System.Net.Security;
using System.Net.Sockets;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;

namespace VertexCache.Sdk
{
    public class VertexCacheSdk
    {
        private readonly VertexCacheSdkOptions _options;
        private readonly ILogger<VertexCacheSdk>? _logger;

        public VertexCacheSdk(VertexCacheSdkOptions options, ILogger<VertexCacheSdk>? logger = null)
        {
            _options = options;
            _logger = logger;
        }

        public async Task<VCacheResult> RunCommandAsync(string command, string[] args)
        {
            _logger?.LogInformation("DEBUG: TLS enabled? {TLS}, CertVerify? {Verify}, Cert length: {CertLen}",
                _options.EnableEncryptionTransport,
                _options.EnableVerifyCertificate,
                _options.CertificatePem?.Length ?? 0);

            if (string.IsNullOrWhiteSpace(command))
                return VCacheResult.Failure(VCacheErrorCode.InvalidCommand, "Command cannot be empty.");

            string rawCommand = CommandFormatter.Format(command, args);

            if (_options.EnableEncryption && !string.IsNullOrWhiteSpace(_options.PublicKey))
            {
                try
                {
                 _logger?.LogInformation("🔐 Encrypting command: {Raw}", rawCommand);
                 _logger?.LogInformation("🔢 Byte length: {Len}", Encoding.UTF8.GetByteCount(rawCommand));

                 string normalizedKey = EncryptionHelper.NormalizePublicKey(_options.PublicKey);
                 rawCommand = EncryptionHelper.Encrypt(rawCommand, normalizedKey);

                }
                catch (Exception ex)
                {
                    _logger?.LogError(ex, "Failed to encrypt command");
                    return VCacheResult.Failure(VCacheErrorCode.EncryptionError, $"Failed to encrypt command: {ex.Message}");
                }
            }

            for (int attempt = 0; attempt <= _options.MaxRetries; attempt++)
            {
                try
                {
                    using var client = new TcpClient();
                    var connectTask = client.ConnectAsync(_options.ServerHost, _options.ServerPort);
                    if (!connectTask.Wait(_options.TimeoutMs))
                        throw new TimeoutException("Connection timed out");

                    using var stream = client.GetStream();
                    Stream ioStream = stream;

                    if (_options.EnableEncryptionTransport)
                    {
                        var ssl = new SslStream(stream, false, (sender, certificate, chain, sslPolicyErrors) =>
                        {
                            if (!_options.EnableVerifyCertificate) return true;
                            if (certificate is null || string.IsNullOrWhiteSpace(_options.CertificatePem)) return false;

                            var expected = new X509Certificate2(Encoding.UTF8.GetBytes(_options.CertificatePem));
                            return certificate.GetCertHashString() == expected.GetCertHashString();
                        });

                        await ssl.AuthenticateAsClientAsync(_options.ServerHost);
                        ioStream = ssl;
                    }

                    // Write without BOM
                    using var writer = new StreamWriter(ioStream, new UTF8Encoding(encoderShouldEmitUTF8Identifier: false)) { AutoFlush = true };
                    using var reader = new StreamReader(ioStream, Encoding.UTF8);

                    byte[] rawBytes = Convert.FromBase64String(rawCommand);
                    await ioStream.WriteAsync(rawBytes, 0, rawBytes.Length);
                    await ioStream.FlushAsync();


                    string? response = await reader.ReadLineAsync();

                    return ProtocolParser.Parse(response);
                }
                catch (Exception ex)
                {
                    _logger?.LogWarning(ex, "Attempt {Attempt} failed", attempt + 1);

                    if (attempt == _options.MaxRetries)
                        return VCacheResult.Failure(VCacheErrorCode.NetworkFailure, $"Failed after {_options.MaxRetries + 1} attempts: {ex.Message}");
                }
            }

            return VCacheResult.Failure(VCacheErrorCode.Unknown, "Unexpected error");
        }
    }
}
