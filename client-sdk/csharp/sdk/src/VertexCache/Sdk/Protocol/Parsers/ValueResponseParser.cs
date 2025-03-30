using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol.Parsers
{
    public class ValueResponseParser : IResponseParser
    {
        public bool CanParse(string response) => response.StartsWith("+") && response != "+PONG" && response != "+OK" && response != "+Deleted" && response != "+(nil)";

        public VCacheResult Parse(string response)
        {
            return VCacheResult.Success(response);
        }
    }
}
