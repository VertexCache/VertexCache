using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol.Parsers
{
    public class PongResponseParser : IResponseParser
    {
        public bool CanParse(string response) => response == "+PONG";

        public VCacheResult Parse(string response) => VCacheResult.SuccessWithRaw(response);
    }
}
