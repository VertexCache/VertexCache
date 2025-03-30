using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol.Parsers
{
    public class DeletedResponseParser : IResponseParser
    {
        public bool CanParse(string response) => response == "+Deleted";

        public VCacheResult Parse(string response)
        {
            return VCacheResult.Success(response);
        }
    }
}
