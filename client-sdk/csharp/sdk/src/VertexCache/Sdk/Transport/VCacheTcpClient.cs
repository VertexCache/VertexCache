using System.Net.Security;
using System.Net.Sockets;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System;
using System.IO;
using System.Threading.Tasks;


namespace VertexCache.Sdk.Transport
{
    public class VCacheTcpClient : IDisposable
    {
        private readonly TcpClient _tcpClient;
        private Stream _stream;
        private readonly int _timeoutMs;

        public VCacheTcpClient(string host, int port, bool useTls, bool verifyCert, string? certPem, int timeoutMs)
        {
            _tcpClient = new TcpClient();
            _timeoutMs = timeoutMs;
            _tcpClient.ConnectAsync(host, port).Wait(_timeoutMs);

            _stream = _tcpClient.GetStream();

            if (useTls)
            {
                var ssl = new SslStream(_stream, false, (sender, certificate, chain, sslPolicyErrors) =>
                {
                    if (!verifyCert) return true;
                    if (certificate is null || string.IsNullOrWhiteSpace(certPem)) return false;

                    var expected = X509Certificate2.CreateFromPem(certPem);
                    return certificate.GetCertHashString() == expected.GetCertHashString();
                });

                ssl.AuthenticateAsClient(host);
                _stream = ssl;
            }
        }

        public async Task<string> SendAsync(string message)
        {
            byte[] data = Encoding.UTF8.GetBytes(message + "\n");
            await _stream.WriteAsync(data, 0, data.Length);
            await _stream.FlushAsync();

            var buffer = new byte[4096];
            int bytesRead = await _stream.ReadAsync(buffer, 0, buffer.Length);
            return Encoding.UTF8.GetString(buffer, 0, bytesRead).Trim();
        }

        public void Dispose()
        {
            _stream.Dispose();
            _tcpClient.Close();
        }
    }
}
