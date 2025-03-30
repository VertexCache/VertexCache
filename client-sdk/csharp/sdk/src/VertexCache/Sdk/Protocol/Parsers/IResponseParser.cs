using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol.Parsers
{
    public interface IResponseParser
    {
        bool CanParse(string response);
        VCacheResult Parse(string response);
    }
}
