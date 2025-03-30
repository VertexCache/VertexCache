using System;
using System.IO;
using System.Net.Sockets;
using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using VertexCache.Sdk.Core;
using VertexCache.Sdk.Transport;
using VertexCache.Sdk.Crypto;
using VertexCache.Sdk.Protocol;
using VertexCache.Sdk.Results;

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
            _logger?.LogInformation("🏁 RunCommandAsync invoked: '{Command}' with {ArgCount} args", command, args.Length);

            if (string.IsNullOrWhiteSpace(command))
            {
                _logger?.LogWarning("❌ Empty command rejected before socket creation");
                return VCacheResult.Failure(VCacheErrorCode.InvalidCommand, "Command cannot be empty.");
            }

            string rawCommand = CommandFormatter.Format(command, args);

            if (_options.EnableEncryption)
            {
                try
                {
                    string? rawKey = _options.PublicKey;
                    if (string.IsNullOrWhiteSpace(rawKey))
                        return VCacheResult.Failure(VCacheErrorCode.EncryptionError, "Missing public key for encryption.");

                    _logger?.LogInformation("🔐 Encrypting command: {Raw}", rawCommand);
                    int byteLen = Encoding.UTF8.GetByteCount(rawCommand);
                    _logger?.LogInformation("🔢 Byte length before encryption: {Len}", byteLen);

                    if (byteLen > 245)
                        return VCacheResult.Failure(VCacheErrorCode.EncryptionError, $"Message too long for RSA: {byteLen} bytes");

                    string normalizedKey = EncryptionHelper.NormalizePublicKey(rawKey);
                    rawCommand = EncryptionHelper.Encrypt(rawCommand, normalizedKey);
                }
                catch (Exception ex)
                {
                    _logger?.LogError(ex, "❌ Encryption failed");
                    return VCacheResult.Failure(VCacheErrorCode.EncryptionError, $"Failed to encrypt command: {ex.Message}");
                }
            }

            for (int attempt = 0; attempt <= _options.MaxRetries; attempt++)
            {
                try
                {
                    _logger?.LogInformation("🌐 Connecting to {Host}:{Port} (Attempt {Attempt})", _options.ServerHost, _options.ServerPort, attempt + 1);

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

                            var expected = X509Certificate2.CreateFromPem(_options.CertificatePem);
                            return certificate.GetCertHashString() == expected.GetCertHashString();
                        });

                        await ssl.AuthenticateAsClientAsync(_options.ServerHost);
                        ioStream = ssl;
                    }

                    using var writer = new StreamWriter(ioStream, new UTF8Encoding(encoderShouldEmitUTF8Identifier: false)) { AutoFlush = true };
                    using var reader = new StreamReader(ioStream, Encoding.UTF8);

                    if (_options.EnableEncryption)
                    {
                        byte[] encryptedBytes = Convert.FromBase64String(rawCommand);
                        _logger?.LogInformation("📤 Sending {Len} encrypted bytes (raw)", encryptedBytes.Length);
                        await ioStream.WriteAsync(encryptedBytes, 0, encryptedBytes.Length);
                        await ioStream.FlushAsync();
                    }
                    else
                    {
                        _logger?.LogInformation("📤 Sending command (plaintext): {Raw}", rawCommand);
                        await writer.WriteLineAsync(rawCommand);
                    }

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
