using System.Collections.Generic;
using VertexCache.Sdk.Protocol.Parsers;
using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol
{
    public class ResponseParserRegistry
    {
        private readonly List<IResponseParser> _parsers = new()
        {
            new PongResponseParser(),
            new OkResponseParser(),
            new ErrorResponseParser(),
            new NilResponseParser(),
            new DeletedResponseParser(),
            new ValueResponseParser()
        };

        public VCacheResult Parse(string? response)
        {
            if (string.IsNullOrWhiteSpace(response))
                return VCacheResult.Failure(VCacheErrorCode.ProtocolError, "Empty response from server.");

            foreach (var parser in _parsers)
            {
                if (parser.CanParse(response))
                {
                    return parser.Parse(response);
                }
            }

            // Fallback: treat unknown + response as generic success
            if (response.StartsWith('+'))
                return VCacheResult.Success(response);

            return VCacheResult.Failure(VCacheErrorCode.Unknown, $"Unknown response: {response}");
        }
    }
}
