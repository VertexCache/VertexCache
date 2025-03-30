using System;
using System.Collections.Generic;
using VertexCache.Sdk.Protocol.Parsers;
using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Protocol
{
    public static class ProtocolParser
    {
        private static readonly List<IResponseParser> _parsers = new()
        {
            new PongResponseParser(),
            new OkResponseParser(),
            new ErrorResponseParser(),
            new NilResponseParser(),
            new DeletedResponseParser(),
            new ValueResponseParser()
        };

        public static VCacheResult Parse(string? response)
        {
            if (string.IsNullOrWhiteSpace(response))
                return VCacheResult.Failure(VCacheErrorCode.ProtocolError, "Empty response.");

            foreach (var parser in _parsers)
            {
                if (parser.CanParse(response))
                    return parser.Parse(response);
            }

            // ✅ Fallback: treat any unhandled +<value> response as success
            if (response.StartsWith("+"))
            {
                return VCacheResult.Success(response);
            }

            return VCacheResult.Failure(VCacheErrorCode.ProtocolError, $"Unknown response: {response}");
        }
    }
}
