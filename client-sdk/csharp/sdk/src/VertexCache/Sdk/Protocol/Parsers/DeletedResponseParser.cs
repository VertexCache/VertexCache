using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol.Parsers
{
    public class DeletedResponseParser : IResponseParser
    {
        public bool CanParse(string response) => response == "+Deleted";

        public VCacheResult Parse(string response) => VCacheResult.SuccessWithRaw(response);
    }
}
