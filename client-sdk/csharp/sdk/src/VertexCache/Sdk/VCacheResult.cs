namespace VertexCache.Sdk
{
    public class VCacheResult
    {
        public bool IsSuccess { get; private set; }
        public string? Message { get; private set; }
        public VCacheErrorCode? ErrorCode { get; private set; }

        private VCacheResult(bool success, string? message, VCacheErrorCode? errorCode = null)
        {
            IsSuccess = success;
            Message = message;
            ErrorCode = errorCode;
        }

        public static VCacheResult Success(string? message) => new VCacheResult(true, message);
        public static VCacheResult Failure(VCacheErrorCode code, string message) => new VCacheResult(false, message, code);

        public override string ToString()
        {
            return IsSuccess ? $"✅ {Message}" : $"❌ [{ErrorCode}] {Message}";
        }
    }
}
