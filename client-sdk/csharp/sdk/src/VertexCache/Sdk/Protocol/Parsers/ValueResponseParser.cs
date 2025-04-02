using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol.Parsers
{
    public class ValueResponseParser : IResponseParser
    {
        public bool CanParse(string response) =>
            response.StartsWith("+") && response != "+OK" && response != "+PONG" && response != "+(nil)" && response != "+Deleted";

        public VCacheResult Parse(string response) => VCacheResult.SuccessWithRaw(response);
    }
}
