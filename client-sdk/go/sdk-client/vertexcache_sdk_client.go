package main

import (
	"bufio"
	"crypto/tls"
	"fmt"
	"os"
	"strings"

	"github.com/joho/godotenv"
	utls "github.com/refraction-networking/utls"
	"github.com/vertexcache/client-sdk/go/sdk/core"
	"github.com/vertexcache/client-sdk/go/sdk/transport"
)

func main() {
	fmt.Println("📄 Loading .env from: config/.env")
	err := godotenv.Load("config/.env")
	if err != nil {
		fmt.Printf("❌ Failed to load .env: %v\n", err)
		os.Exit(1)
	}

	host := os.Getenv("server_host")
	if host == "" {
		host = "localhost"
	}
	port := os.Getenv("server_port")
	if port == "" {
		port = "50505"
	}
	address := fmt.Sprintf("%s:%s", host, port)

	useTLS := strings.ToLower(os.Getenv("transport_tls_enabled")) == "true"
	verifyTLS := strings.ToLower(os.Getenv("transport_tls_verify")) == "true"

	tlsCert := os.Getenv("tls_certificate")
	var conn core.Conn

	if useTLS {
		fmt.Printf("🔍 RAW TLS config: enabled=%q, verify=%q\n", os.Getenv("transport_tls_enabled"), os.Getenv("transport_tls_verify"))
		fmt.Printf("✅ Parsed TLS settings: enabled=%v, verify=%v\n", useTLS, verifyTLS)

		if tlsCert == "" {
			fmt.Println("❌ TLS is enabled but tls_certificate is missing")
			os.Exit(1)
		}

		tlsConfig, err := transport.LoadTLSConfigFromString(tlsCert, host, verifyTLS)
		if err != nil {
			fmt.Printf("❌ failed to load TLS config: %v\n", err)
			os.Exit(1)
		}

		utlsConfig := &utls.Config{
			ServerName:         tlsConfig.ServerName,
			MinVersion:         tls.VersionTLS12,
			InsecureSkipVerify: tlsConfig.InsecureSkipVerify,
			RootCAs:            tlsConfig.RootCAs,
		}

		conn, err = transport.NewTLSConnection(address, utlsConfig)
		if err != nil {
			fmt.Printf("❌ VertexCache server not reachable at %s (timeout 2s)\n", address)
			fmt.Println("💡 Make sure the server is running before starting the client.")
			os.Exit(1)
		}
	} else {
		conn, err = transport.NewTCPConnection(address)
		if err != nil {
			fmt.Printf("❌ VertexCache server not reachable at %s\n", address)
			fmt.Println("💡 Make sure the server is running before starting the client.")
			os.Exit(1)
		}
	}

	defer conn.Close()

	fmt.Println()
	fmt.Println("VertexCache Go Client Console:")
	fmt.Println("  Version: 1.0.0")
	fmt.Printf("  Host: %s\n", host)
	fmt.Printf("  Port: %s\n", port)
	fmt.Printf("  Message Layer Encryption Enabled: Yes\n")
	fmt.Printf("  Transport Layer Encryption Enabled: %v\n", useTLS)
	fmt.Printf("  Transport Layer Verify Certificate: %v\n", verifyTLS)
	fmt.Printf("  Config file set: Yes\n")
	fmt.Printf("  Config file loaded with no errors: Yes\n")
	fmt.Println("  Config file location: ./config/.env")
	fmt.Println("Status: OK, Console Client Started")
	fmt.Println()

	reader := bufio.NewReader(os.Stdin)
	client := core.NewClient(conn)

	for {
		fmt.Printf("VertexCache Console, %s:%s> ", host, port)
		input, _ := reader.ReadString('\n')
		cmd := strings.TrimSpace(input)

		if cmd == "exit" || cmd == "quit" {
			break
		}

		result := client.RunCommand(cmd)
		if result.Success {
			fmt.Println(result.Data)
		} else if result.Error != nil {
			fmt.Println(result.Error.Error())
		} else {
			fmt.Println("❌ Unknown error")
		}
	}
}
