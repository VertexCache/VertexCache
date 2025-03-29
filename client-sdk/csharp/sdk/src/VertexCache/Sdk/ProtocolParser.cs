namespace VertexCache.Sdk
{
    public static class ProtocolParser
    {
        public static VCacheResult Parse(string? response)
        {
            if (string.IsNullOrWhiteSpace(response))
                return VCacheResult.Failure(VCacheErrorCode.ProtocolError, "Empty response");

            var result = new VCacheResult { Raw = response };

            if (response.StartsWith("+"))
            {
                result.IsSuccess = true;
                result.Message = response[1..].Trim();
                result.Code = VCacheErrorCode.None;
            }
            else if (response.StartsWith("-"))
            {
                result.IsSuccess = false;
                result.Message = response[1..].Trim();
                result.Code = VCacheErrorCode.ServerError;
            }
            else
            {
                result.IsSuccess = false;
                result.Message = "Malformed response";
                result.Code = VCacheErrorCode.ProtocolError;
            }

            return result;
        }
    }
}
