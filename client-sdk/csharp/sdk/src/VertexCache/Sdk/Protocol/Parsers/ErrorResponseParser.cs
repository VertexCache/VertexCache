using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol.Parsers
{
    public class ErrorResponseParser : IResponseParser
    {
        public bool CanParse(string response) => response.StartsWith("-");

        public VCacheResult Parse(string response) =>
            VCacheResult.FailureWithRaw(VCacheErrorCode.ServerError, response, response);
    }
}
