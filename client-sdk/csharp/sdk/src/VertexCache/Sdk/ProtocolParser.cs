namespace VertexCache.Sdk
{
    public static class ProtocolParser
    {
        public static VCacheResult Parse(string? response)
        {
            if (string.IsNullOrWhiteSpace(response))
            {
                return VCacheResult.Failure(VCacheErrorCode.EmptyResponse, "No response received from server");
            }

            response = response.Trim();

            if (response.StartsWith("+"))
                return VCacheResult.Success(response[1..].Trim());

            if (response.StartsWith("-"))
                return VCacheResult.Failure(VCacheErrorCode.InvalidCommand, response[1..].Trim());

            // Fallback: treat raw response as success
            return VCacheResult.Success(response);
        }
    }
}
