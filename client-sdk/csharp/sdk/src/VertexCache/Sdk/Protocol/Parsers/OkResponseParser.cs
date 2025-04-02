using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol.Parsers
{
    public class OkResponseParser : IResponseParser
    {
        public bool CanParse(string response) => response == "+OK";

        public VCacheResult Parse(string response) => VCacheResult.SuccessWithRaw(response);
    }
}
