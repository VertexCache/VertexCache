namespace VertexCache.Sdk.Results
{
    public class VCacheResult
    {
        public bool IsSuccess { get; set; }
        public string Message { get; set; } = string.Empty;
        public VCacheErrorCode Code { get; set; } = VCacheErrorCode.None;
        public string? Raw { get; set; }

        public VCacheResult() { }

        public static VCacheResult SuccessWithRaw(string raw)
        {
            return new VCacheResult
            {
                IsSuccess = true,
                Message = raw,
                Code = VCacheErrorCode.None,
                Raw = raw
            };
        }

        public static VCacheResult FailureWithRaw(VCacheErrorCode code, string message, string raw)
        {
            return new VCacheResult
            {
                IsSuccess = false,
                Message = message,
                Code = code,
                Raw = raw
            };
        }

        public static VCacheResult Success(string message)
        {
            return new VCacheResult
            {
                IsSuccess = true,
                Message = message,
                Code = VCacheErrorCode.None
            };
        }

        public static VCacheResult Failure(VCacheErrorCode code, string message)
        {
            return new VCacheResult
            {
                IsSuccess = false,
                Message = message,
                Code = code
            };
        }

        public override string ToString()
        {
            return IsSuccess ? $"+{Message}" : $"-{Message}";
        }
    }
}
