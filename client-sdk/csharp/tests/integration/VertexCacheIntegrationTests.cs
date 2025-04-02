using System;
using System.IO;
using System.Threading.Tasks;
using VertexCache.Sdk;
using VertexCache.Sdk.Core;
using VertexCache.Sdk.Results;
using Xunit;

namespace VertexCache.Tests.Integration
{
    public class VertexCacheIntegrationTests
    {
        private static VertexCacheSdkOptions LoadTestOptions()
        {
            var baseDir = AppContext.BaseDirectory;
            var configDir = Path.GetFullPath(Path.Combine(baseDir, "..", "..", "..", "..", "..", "config"));

            return new VertexCacheSdkOptions
            {
                ServerHost = "localhost",
                ServerPort = 50505,
                EnableEncryptionTransport = true,
                EnableVerifyCertificate = false,
                EnableEncryption = true,
                CertificatePem = File.ReadAllText(Path.Combine(configDir, "test_tls_certificate.pem")),
                PublicKey = File.ReadAllText(Path.Combine(configDir, "test_public_key.pem")),
                TimeoutMs = 3000,
                MaxRetries = 0
            };
        }


        [Fact]
        public async Task RunCommandSequence_WhenServerIsRunning_ShouldReturnExpectedResults()
        {
            var sdk = new VertexCacheSdk(LoadTestOptions());

            var pingResult = await sdk.RunCommandAsync("ping", Array.Empty<string>());
            var setResult = await sdk.RunCommandAsync("set", new[] { "testkey", "hello" });
            var getResult = await sdk.RunCommandAsync("get", new[] { "testkey" });

            Assert.True(pingResult.IsSuccess, $"Ping failed: {pingResult.Raw}");
            Assert.True(setResult.IsSuccess, $"Set failed: {setResult.Raw}");
            Assert.True(getResult.IsSuccess, $"Get failed: {getResult.Raw}");
            Assert.Equal("+hello", getResult.Raw);
        }
    }
}
