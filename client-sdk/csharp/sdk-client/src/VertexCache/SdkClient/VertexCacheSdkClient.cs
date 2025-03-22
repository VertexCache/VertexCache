using System.Threading.Tasks;
using DotNetEnv;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.DependencyInjection;
using VertexCache.Sdk;

namespace VertexCache.SdkClient
{
    public class VertexCacheSdkClient
    {
        private readonly VertexCacheSdk _sdk;

        public VertexCacheSdkClient()
        {
            // Load config from .env
            Env.Load("config/.env");

            var options = new VertexCacheSdkOptions
            {
                ServerHost = Env.GetString("server_host", "127.0.0.1"),
                ServerPort = Env.GetInt("server_port", 50505),
                EnableEncryption = Env.GetBool("enable_encrypt_message", false),
                PublicKey = Env.GetString("public_key"),
                EnableEncryptionTransport = Env.GetBool("enable_encrypt_transport", false),
                EnableVerifyCertificate = Env.GetBool("enable_verify_certificate", true),
                CertificatePem = Env.GetString("server_certificate_pem")
            };

            var services = new ServiceCollection();
            services.AddLogging(config => config.AddConsole().SetMinimumLevel(LogLevel.Information));
            var provider = services.BuildServiceProvider();

            _sdk = new VertexCacheSdk(options, provider.GetRequiredService<ILogger<VertexCacheSdk>>());
        }

        public Task<VCacheResult> RunCommandAsync(string command, string[] args)
        {
            return _sdk.RunCommandAsync(command, args);
        }
    }
}
