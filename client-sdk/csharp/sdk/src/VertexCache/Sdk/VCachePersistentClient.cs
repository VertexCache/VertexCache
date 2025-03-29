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
    public class VCachePersistentClient : IDisposable, IVCacheClient
    {
        private readonly VertexCacheSdkOptions _options;
        private readonly ILogger? _logger;
        private TcpClient? _client;
        private Stream? _stream;
        private StreamWriter? _writer;
        private StreamReader? _reader;
        private bool _connected = false;

        public VCachePersistentClient(VertexCacheSdkOptions options, ILogger? logger = null)
        {
            _options = options;
            _logger = logger;
        }

        public async Task<VCacheResult> ConnectAsync()
        {
            if (_connected) return VCacheResult.Success("Already connected.");

            try
            {
                _client = new TcpClient();
                var connectTask = _client.ConnectAsync(_options.ServerHost, _options.ServerPort);
                if (!connectTask.Wait(_options.TimeoutMs))
                    throw new TimeoutException("Connection timed out");

                var stream = _client.GetStream();
                _stream = stream;

                if (_options.EnableEncryptionTransport)
                {
                    var ssl = new SslStream(stream, false, (sender, certificate, chain, sslPolicyErrors) =>
                    {
                        if (!_options.EnableVerifyCertificate) return true;
                        if (certificate is null || string.IsNullOrWhiteSpace(_options.CertificatePem)) return false;

                        var expected = X509Certificate2.CreateFromPem(_options.CertificatePem);
                        return certificate.GetCertHashString() == expected.GetCertHashString();
                    });

                    await Task.Delay(20); // Mitigate possible TLS timing issues
                    await ssl.AuthenticateAsClientAsync(_options.ServerHost);
                    _stream = ssl;
                }

                _writer = new StreamWriter(_stream, new UTF8Encoding(false)) { AutoFlush = true };
                _reader = new StreamReader(_stream, Encoding.UTF8);
                _connected = true;

                return VCacheResult.Success("Connected successfully.");
            }
            catch (Exception ex)
            {
                _logger?.LogError(ex, "❌ Failed to connect");
                return VCacheResult.Failure(VCacheErrorCode.NetworkFailure, ex.Message);
            }
        }

        public async Task<VCacheResult> RunCommandAsync(string command, string[] args)
        {
            if (!_connected)
            {
                var result = await ConnectAsync();
                if (!result.IsSuccess)
                    return result;
            }

            if (string.IsNullOrWhiteSpace(command))
                return VCacheResult.Failure(VCacheErrorCode.InvalidCommand, "Command cannot be empty.");

            string rawCommand = CommandFormatter.Format(command, args);

            if (_options.EnableEncryption && !string.IsNullOrWhiteSpace(_options.PublicKey))
            {
                try
                {
                    string normalizedKey = EncryptionHelper.NormalizePublicKey(_options.PublicKey);
                    rawCommand = EncryptionHelper.Encrypt(rawCommand, normalizedKey);
                }
                catch (Exception ex)
                {
                    _logger?.LogError(ex, "❌ Encryption failed");
                    return VCacheResult.Failure(VCacheErrorCode.EncryptionError, ex.Message);
                }
            }

            try
            {
                if (_options.EnableEncryption)
                {
                    byte[] encryptedBytes = Convert.FromBase64String(rawCommand);
                    await _stream!.WriteAsync(encryptedBytes, 0, encryptedBytes.Length);
                    await _stream.FlushAsync();
                }
                else
                {
                    await _writer!.WriteLineAsync(rawCommand);
                }

                string? response = await _reader!.ReadLineAsync();
                return ProtocolParser.Parse(response);
            }
            catch (Exception ex)
            {
                _logger?.LogError(ex, "❌ Failed to send command");
                return VCacheResult.Failure(VCacheErrorCode.NetworkFailure, ex.Message);
            }
        }

        public void Dispose()
        {
            _reader?.Dispose();
            _writer?.Dispose();
            _stream?.Dispose();
            _client?.Close();
            _connected = false;
        }
    }
}
