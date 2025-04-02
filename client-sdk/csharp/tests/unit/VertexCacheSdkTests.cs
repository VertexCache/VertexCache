using System;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using VertexCache.Sdk;
using VertexCache.Sdk.Results;
using VertexCache.Sdk.Core;
using Xunit;

namespace VertexCache.Tests.Unit
{
    public class VertexCacheSdkTests
    {
        [Fact]
        public async Task EmptyCommand_ShouldReturnFailure()
        {
            var options = new VertexCacheSdkOptions { ServerHost = "localhost", ServerPort = 50505 };
            var logger = LoggerFactory.Create(b => b.AddConsole()).CreateLogger<VertexCacheSdk>();
            var sdk = new VertexCacheSdk(options, logger);

            var result = await sdk.RunCommandAsync("", Array.Empty<string>());

            Assert.False(result.IsSuccess);
            Assert.Equal(VCacheErrorCode.InvalidCommand, result.Code);
        }
    }
}