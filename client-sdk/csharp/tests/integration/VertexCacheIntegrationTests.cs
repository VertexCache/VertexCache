using System;
using System.Net.Sockets;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.DependencyInjection;
using VertexCache.Sdk;
using Xunit;

namespace VertexCache.Tests.Integration
{
    public class VertexCacheIntegrationTests
    {
        private readonly VertexCacheSdk _sdk;

        public VertexCacheIntegrationTests()
        {
            var options = new VertexCacheSdkOptions
            {
                ServerHost = "127.0.0.1",
                ServerPort = 50505,
                TimeoutMs = 2000,
                EnableEncryption = false,
                EnableEncryptionTransport = false,
                EnableVerifyCertificate = false
            };

            var services = new ServiceCollection();
            services.AddLogging(config => config.AddConsole().SetMinimumLevel(LogLevel.Warning));
            var provider = services.BuildServiceProvider();
            var logger = provider.GetRequiredService<ILogger<VertexCacheSdk>>();

            _sdk = new VertexCacheSdk(options, logger);
        }

        private bool IsServerAvailable()
        {
            try
            {
                using var client = new TcpClient();
                var task = client.ConnectAsync("127.0.0.1", 50505);
                return task.Wait(1000);
            }
            catch
            {
                return false;
            }
        }

        private void SkipIfServerUnavailable()
        {
            if (!IsServerAvailable())
            {
                Console.WriteLine("⚠️  Skipping test: VertexCache server is not running on localhost:50505");
                throw new SkipTestException();
            }
        }

        [Fact]
        public async Task PingCommand_ReturnsSuccess()
        {
            try { SkipIfServerUnavailable(); }
            catch (SkipTestException) { return; }

            var result = await _sdk.RunCommandAsync("ping", Array.Empty<string>());

            Assert.True(result.Success);
            Assert.Equal("pong", result.Response, ignoreCase: true);
        }

        [Fact]
        public async Task SetAndGetCommand_WorkCorrectly()
        {
            try { SkipIfServerUnavailable(); }
            catch (SkipTestException) { return; }

            string key = "test-key";
            string value = "hello-world";

            var setResult = await _sdk.RunCommandAsync("set", new[] { key, value });
            Assert.True(setResult.Success);

            var getResult = await _sdk.RunCommandAsync("get", new[] { key });
            Assert.True(getResult.Success);
            Assert.Equal(value, getResult.Response);
        }

        [Fact]
        public async Task DelCommand_RemovesKey()
        {
            try { SkipIfServerUnavailable(); }
            catch (SkipTestException) { return; }

            string key = "delete-key";
            string value = "to-be-deleted";

            await _sdk.RunCommandAsync("set", new[] { key, value });

            var delResult = await _sdk.RunCommandAsync("del", new[] { key });
            Assert.True(delResult.Success);

            var getAfterDel = await _sdk.RunCommandAsync("get", new[] { key });
            Assert.False(getAfterDel.Success);
        }

        private class SkipTestException : Exception { }
    }
}
