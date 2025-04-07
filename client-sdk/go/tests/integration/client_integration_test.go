package integration_test

import (
	"os"
	"path/filepath"
	"runtime"
	"testing"

	"github.com/joho/godotenv"
	"sdk/core"
	"sdk/transport"
)

func connectToServer(t *testing.T) *transport.Connection {
	t.Helper()

	_, filename, _, _ := runtime.Caller(0)
	base := filepath.Dir(filename)
	envPath := filepath.Join(base, "..", "..", "config", ".env")

	if err := godotenv.Load(envPath); err != nil {
		t.Skipf("Skipping integration test, missing .env: %v", err)
	}

	address := os.Getenv("server_host") + ":" + os.Getenv("server_port")
	useTLS := os.Getenv("transport_tls_enabled") == "true"

	var conn *transport.Connection
	var err error

	if useTLS {
		tlsCert := os.Getenv("tls_certificate")
		tlsConfig, err := transport.LoadTLSConfigFromString(tlsCert, host)
		if err != nil {
			t.Fatalf("Failed to load TLS config: %v", err)
		}
		conn, err = transport.NewTLSConnection(address, tlsConfig)
		if err != nil {
			t.Fatalf("Failed to connect (TLS): %v", err)
		}
	} else {
		conn, err = transport.NewTCPConnection(address)
		if err != nil {
			t.Fatalf("Failed to connect (TCP): %v", err)
		}
	}

	return conn
}

func TestRunCommandSequence(t *testing.T) {
	conn := connectToServer(t)
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
	expected := "+hello\r\n"
	if res.Data != expected {
		t.Errorf("Expected value %q, got %q", expected, res.Data)
	}
}
