using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol
{
    public static class ProtocolParser
    {
        private static readonly ResponseParserRegistry _registry = new();

        public static VCacheResult Parse(string? response)
        {
            return _registry.Parse(response);
        }
    }
}
