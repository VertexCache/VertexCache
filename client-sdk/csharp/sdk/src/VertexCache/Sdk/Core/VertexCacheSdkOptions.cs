namespace VertexCache.Sdk.Core
{
    public class VertexCacheSdkOptions
    {
        public string ServerHost { get; set; } = "127.0.0.1";
        public int ServerPort { get; set; } = 50505;
        public int TimeoutMs { get; set; } = 3000;
        public int MaxRetries { get; set; } = 0;

        public bool EnableEncryption { get; set; } = false;
        public string? PublicKey { get; set; }

        public bool EnableEncryptionTransport { get; set; } = false;
        public bool EnableVerifyCertificate { get; set; } = true;
        public string? CertificatePem { get; set; }
    }
}
