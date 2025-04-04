package integration_test

import (
	"os"
	"path/filepath"
	"testing"

	"github.com/joho/godotenv"
	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/core"
	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/transport"
)

func loadTLSConfig(t *testing.T) *transport.Connection {
	t.Helper()

	envPath := filepath.Join("config", ".env")
	err := godotenv.Load(envPath)
	if err != nil {
		t.Fatalf("Failed to load .env: %v", err)
	}

	tlsCert := os.Getenv("tls_certificate")
	if tlsCert == "" {
		t.Fatal("tls_certificate not set in .env")
	}
	tlsConfig, err := transport.LoadTLSConfigFromString(tlsCert)
	if err != nil {
		t.Fatalf("Failed to load TLS config: %v", err)
	}

	host := os.Getenv("server_host")
	port := os.Getenv("server_port")
	address := host + ":" + port

	conn, err := transport.NewTLSConnection(address, tlsConfig)
	if err != nil {
		t.Fatalf("Failed to connect: %v", err)
	}
	return conn
}

func TestRunCommandSequence(t *testing.T) {
	conn := loadTLSConfig(t)
	defer conn.Close()

	client := core.NewClient(conn)

	if res := client.Ping(); !res.Success {
		t.Fatalf("Ping failed: %v", res.Error)
	}
	if res := client.Set("testkey", "hello"); !res.Success {
		t.Fatalf("Set failed: %v", res.Error)
	}
	res := client.Get("testkey")
	if !res.Success {
		t.Fatalf("Get failed: %v", res.Error)
	}
	if res.Data != "hello" {
		t.Errorf("Expected value 'hello', got '%s'", res.Data)
	}
}
