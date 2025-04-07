package main

import (
	"log"

	"sdk/config"
	"sdk/core"
	"sdk/crypto"
	"sdk/transport"
)

func main() {
	rawPublicKey := config.GetEnvString("public_key", "")
	if rawPublicKey == "" {
		log.Fatal("Missing env var: public_key")
	}

	rawCert := config.GetEnvString("tls_certificate", "")
	if rawCert == "" {
		log.Fatal("Missing env var: tls_certificate")
	}

	options := &core.VertexCacheSdkOptions{
		ServerHost:                config.GetEnvString("server_host", "127.0.0.1"),
		ServerPort:                config.GetEnvInt("server_port", 50505),
		EnableEncryptionTransport: config.GetEnvBool("enable_encrypt_transport", false),
		EnableVerifyCertificate:   config.GetEnvBool("enable_verify_certificate", true),
		EnableEncryption:          config.GetEnvBool("enable_encrypt_message", false),
		TimeoutMs:                 config.GetEnvInt("timeout_ms", 3000),
		MaxRetries:                0,
		PublicKey:                 crypto.LoadFromFileOrRaw(rawPublicKey),
		CertificatePem:            crypto.LoadFromFileOrRaw(rawCert),
	}

	client := transport.NewVCachePersistentClient(options)
	defer client.Dispose()

	runner := consoleapp.NewCliRunner(client, options)
	runner.RunInteractiveAsync()
}
