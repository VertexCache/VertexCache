package main

import (
	"fmt"
	"os"
	"sdk/config"
	"sdk/core"
	"sdk/crypto"
	"sdk/sdk-client/console_app"
	"sdk/transport"
	"strings"
)

func main() {
	envPath := "config/.env"
	fmt.Printf("📄 Loading .env from: %s\n", envPath)

	err := config.LoadEnv(envPath)
	if err != nil {
		fmt.Printf("❌ Failed to load config: %v\n", err)
		os.Exit(1)
	}

	rawPublicKey := config.Get("public_key")
	rawCert := config.Get("tls_certificate")

	options := &core.VertexCacheSdkOptions{
		ServerHost:                config.Get("server_host", "127.0.0.1"),
		ServerPort:                config.GetInt("server_port", 50505),
		EnableEncryption:          config.GetBool("enable_encrypt_message", false),
		EnableEncryptionTransport: config.GetBool("enable_encrypt_transport", false),
		EnableVerifyCertificate:   config.GetBool("enable_verify_certificate", true),
		TimeoutMs:                 config.GetInt("timeout_ms", 2000),
		MaxRetries:                config.GetInt("max_retries", 0),
		PublicKey:                 crypto.MustLoadPublicKey(rawPublicKey),
		CertificatePem:            transport.MustLoadCertificate(rawCert),
	}

	console_app.PrintWelcomeBanner(options)

	client := core.NewVertexCacheSdk(options)

	for {
		console_app.PrintPrompt(options)
		var input string
		_, err := fmt.Scanln(&input)
		if err != nil {
			if err.Error() == "unexpected newline" {
				continue
			}
			fmt.Printf("❌ Failed to read input: %v\n", err)
			continue
		}

		parts := strings.Fields(input)
		if len(parts) == 0 {
			continue
		}

		command := parts[0]
		args := parts[1:]

		result := client.RunCommand(command, args)

		if result.Ok {
			if result.Raw != "" {
				fmt.Println(result.Raw)
			} else {
				fmt.Println(result.Message)
			}
		} else {
			fmt.Printf("[%s] %s\n", result.Error.Code.String(), result.Error.Message)
		}
	}
}
