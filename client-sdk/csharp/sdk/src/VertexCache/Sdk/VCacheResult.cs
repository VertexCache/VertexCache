namespace VertexCache.Sdk
{
    public class VCacheResult
    {
        public bool Success { get; set; }
        public string Response { get; set; } = string.Empty;
        public VCacheErrorCode ErrorCode { get; set; } = VCacheErrorCode.None;
        public string ErrorMessage { get; set; } = string.Empty;

        public static VCacheResult Ok(string response) => new VCacheResult
        {
            Success = true,
            Response = response
        };

        public static VCacheResult Fail(VCacheErrorCode code, string message) => new VCacheResult
        {
            Success = false,
            ErrorCode = code,
            ErrorMessage = message
        };
    }
}
