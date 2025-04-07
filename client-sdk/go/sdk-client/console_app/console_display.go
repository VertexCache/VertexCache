package console_app

import (
	"fmt"
	"sdk/core"
)

func PrintWelcomeBanner(options *core.VertexCacheSdkOptions) {
	clearConsole()
	fmt.Println("VertexCache Go Client Console:")
	fmt.Printf("  Host: %s\n", options.ServerHost)
	fmt.Printf("  Port: %d\n", options.ServerPort)
	fmt.Printf("  Message Layer Encryption Enabled: %s\n", boolToYesNo(options.EnableEncryption))
	fmt.Printf("  Transport Layer Encryption Enabled: %s\n", boolToYesNo(options.EnableEncryptionTransport))
	fmt.Printf("  Transport Layer Verify Certificate: %s\n", boolToYesNo(options.EnableVerifyCertificate))
	fmt.Println()
}

func PrintPrompt(options *core.VertexCacheSdkOptions) {
	fmt.Printf("VertexCache Console, %s:%d> ", options.ServerHost, options.ServerPort)
}

func boolToYesNo(value bool) string {
	if value {
		return "Yes"
	}
	return "No"
}

func clearConsole() {
	// Only works reliably on Unix/macOS terminals
	fmt.Print("\033[H\033[2J")
}
