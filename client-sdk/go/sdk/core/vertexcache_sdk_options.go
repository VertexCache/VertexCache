package core

type VertexCacheSdkOptions struct {
	ServerHost                string
	ServerPort                int
	TimeoutMs                 int
	MaxRetries                int
	EnableEncryption          bool
	PublicKey                 string
	EnableEncryptionTransport bool
	EnableVerifyCertificate   bool
	CertificatePem            string
}
