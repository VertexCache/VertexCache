using System;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using VertexCache.Sdk;
using VertexCache.Sdk.Helpers;
using DotNetEnv;

namespace VertexCache.SdkClient
{
    public class VertexCacheSdkClient
    {
        private readonly VertexCacheSdk _sdk;

        public VertexCacheSdkClient(ILogger<VertexCacheSdk>? logger = null)
        {
            Env.Load("./config/.env");

            var rawPublicKeyValue = Env.GetString("public_key");
            var tlsCertValue = Env.GetString("tls_certificate");

            var options = new VertexCacheSdkOptions
            {
                ServerHost = Env.GetString("server_host", "127.0.0.1"),
                ServerPort = Env.GetInt("server_port", 50505),
                EnableEncryption = Env.GetBool("enable_encrypt_message", false),
                PublicKey = EncryptionHelper.NormalizePublicKey(
                    PemLoader.LoadFromFileOrRaw(rawPublicKeyValue) ?? ""
                ),
                EnableEncryptionTransport = Env.GetBool("enable_encrypt_transport", false),
                EnableVerifyCertificate = Env.GetBool("enable_verify_certificate", true),
                CertificatePem = PemLoader.LoadFromFileOrRaw(Env.GetString("tls_certificate")),
            };

            Console.WriteLine("\uD83D\uDD10 Encryption Transport Enabled: " + options.EnableEncryptionTransport);
            Console.WriteLine("\uD83D\uDD12 Verify Certificate: " + options.EnableVerifyCertificate);
            Console.WriteLine("\uD83D\uDD0F Message Encryption: " + options.EnableEncryption);
            Console.WriteLine("\uD83D\uDCDC Public Key Length: " + (options.PublicKey?.Length ?? 0));
            Console.WriteLine("\uD83D\uDCC4 TLS Cert Length: " + (options.CertificatePem?.Length ?? 0));

            _sdk = new VertexCacheSdk(options, logger);
        }

        public async Task<VCacheResult> RunCommandAsync(string command, string[] args)
        {
            return await _sdk.RunCommandAsync(command, args);
        }
    }
}
