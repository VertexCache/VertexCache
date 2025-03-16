using Xunit;
using VertexCache.Sdk;

namespace VertexCache.Tests
{
    public class VertexCacheSdkTests
    {
        [Fact]
        public void GetMessage_ShouldReturnExpectedString()
        {
            // Arrange
            var sdk = new VertexCacheSdk();

            // Act
            var result = sdk.GetMessage();

            // Assert
            Assert.Equal("VertexCache SDK!", result);
        }
    }
}
