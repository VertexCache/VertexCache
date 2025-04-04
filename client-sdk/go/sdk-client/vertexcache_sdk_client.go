package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"strings"

	"github.com/joho/godotenv"
	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/core"
	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/protocol"
	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/results"
	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/transport"
)

const (
	version        = "1.0.0"
	defaultEnvPath = "config/.env"
)

func main() {
	envPath := filepath.Join(defaultEnvPath)
	envLoaded := false
	envErr := godotenv.Load(envPath)
	if envErr == nil {
		envLoaded = true
	}

	host := os.Getenv("server_host")
	port := os.Getenv("server_port")
	address := fmt.Sprintf("%s:%s", host, port)
	tlsCert := os.Getenv("tls_certificate")

	tlsEnabled := tlsCert != ""
	certVerify := "Yes" // Currently no toggle
	configSet := envPath != ""
	configLoadOk := envLoaded && tlsEnabled

	// Display startup banner
	fmt.Println("VertexCache Go Client Console:")
	fmt.Printf("  Version: %s\n", version)
	fmt.Printf("  Host: %s\n", host)
	fmt.Printf("  Port: %s\n", port)
	fmt.Printf("  Message Layer Encryption Enabled: %s\n", "No") // Not implemented yet
	fmt.Printf("  Transport Layer Encryption Enabled: %s\n", boolToYesNo(tlsEnabled))
	fmt.Printf("  Transport Layer Verify Certificate: %s\n", certVerify)
	fmt.Printf("  Config file set: %s\n", boolToYesNo(configSet))
	fmt.Printf("  Config file loaded with no errors: %s\n", boolToYesNo(configLoadOk))
	fmt.Printf("  Config file location: %s\n", envPath)

	if configLoadOk {
		fmt.Println("Status: OK, Console Client Started\n")
	} else {
		fmt.Println("Status: WARN, Config incomplete or missing\n")
	}

	if !tlsEnabled {
		log.Fatal("TLS certificate missing in .env")
	}

	tlsConfig, err := transport.LoadTLSConfigFromString(tlsCert)
	if err != nil {
		log.Fatalf("TLS config error: %v", err)
	}

	conn, err := transport.NewTLSConnection(address, tlsConfig)
	if err != nil {
		log.Fatalf("Connection failed: %v", err)
	}
	defer conn.Close()

	client := core.NewClient(conn)

	scanner := bufio.NewScanner(os.Stdin)
	for {
		fmt.Printf("VertexCache Console, %s:%s> ", host, port)
		if !scanner.Scan() {
			fmt.Println("\n👋 Goodbye.")
			break
		}
		line := strings.TrimSpace(scanner.Text())
		if line == "" {
			continue
		}
		if line == "exit" || line == "quit" {
			fmt.Println("👋 Exiting.")
			break
		}

		cmd, err := protocol.ParseCommand(line)
		if err != nil {
			fmt.Printf("-ERR %v\n", err)
			continue
		}

		res := sendCommand(client, cmd)
		printFormattedResult(res)
	}
}

func sendCommand(client *core.Client, cmd *protocol.Command) *results.Result {
	switch cmd.Type {
	case protocol.CommandSet:
		return client.Set(cmd.Key, cmd.Value)
	case protocol.CommandGet:
		return client.Get(cmd.Key)
	case protocol.CommandDelete:
		return client.Delete(cmd.Key)
	case protocol.CommandPing:
		return client.Ping()
	default:
		return results.NewFailure(results.New(results.ErrInvalidCommand, "Unsupported command type", nil))
	}
}

func printFormattedResult(res *results.Result) {
	if res.Success {
		if res.Data == "PONG" {
			fmt.Println("+PONG")
		} else if res.Data != "" {
			fmt.Printf("$%d\n%s\n", len(res.Data), res.Data)
		} else {
			fmt.Println("+OK")
		}
	} else if res.Error != nil {
		fmt.Printf("-ERR [%s] %s\n", res.Error.Code, res.Error.Message)
	} else {
		fmt.Println("-ERR unknown error")
	}
}

func boolToYesNo(b bool) string {
	if b {
		return "Yes"
	}
	return "No"
}
