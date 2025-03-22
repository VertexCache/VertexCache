using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using Moq;
using VertexCache.Sdk;
using Xunit;

namespace VertexCache.Tests.Unit
{
    public class VertexCacheSdkTests
    {
        private readonly VertexCacheSdk _sdk;

        public VertexCacheSdkTests()
        {
            var options = new VertexCacheSdkOptions
            {
                ServerHost = "localhost",
                ServerPort = 50505,
                EnableEncryption = false,
                EnableEncryptionTransport = false,
                TimeoutMs = 2000
            };

            var mockLogger = new Mock<ILogger<VertexCacheSdk>>();
            _sdk = new VertexCacheSdk(options, mockLogger.Object);
        }

        [Fact]
        public async Task RunCommandAsync_WithEmptyCommand_ReturnsInvalidCommandError()
        {
            var result = await _sdk.RunCommandAsync("", new string[] { });

            Assert.False(result.Success);
            Assert.Equal(VCacheErrorCode.InvalidCommand, result.ErrorCode);
        }

        [Fact]
        public async Task RunCommandAsync_WithPingCommand_FailsWithoutServer()
        {
            var result = await _sdk.RunCommandAsync("ping", new string[] { });

            Assert.False(result.Success);
            Assert.NotEqual(VCacheErrorCode.None, result.ErrorCode);
            Assert.NotEmpty(result.ErrorMessage);
        }

        // Add more logic-based tests here (e.g., formatting, encryption-only scenarios, etc.)
    }
}
