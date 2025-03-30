using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol.Parsers
{
    public class NilResponseParser : IResponseParser
    {
        public bool CanParse(string response) => response == "+(nil)";

        public VCacheResult Parse(string response)
        {
            return VCacheResult.Success(response);
        }
    }
}
