package transport

import (
	"fmt"
	"net"
	"time"

	utls "github.com/refraction-networking/utls"
)

type Connection struct {
	conn net.Conn
}

func (c *Connection) Send(data []byte) error {
	_, err := c.conn.Write(data)
	return err
}

func (c *Connection) Receive(buffer []byte) (int, error) {
	return c.conn.Read(buffer)
}

func (c *Connection) Close() error {
	return c.conn.Close()
}

func NewTCPConnection(address string) (*Connection, error) {
	conn, err := net.DialTimeout("tcp", address, 2*time.Second)
	if err != nil {
		return nil, err
	}
	return &Connection{conn: conn}, nil
}

func NewTLSConnection(address string, config *utls.Config) (*Connection, error) {
	fmt.Printf("🔌 Dialing TLS via uTLS: %s\n", address)

	rawConn, err := net.DialTimeout("tcp", address, 2*time.Second)
	if err != nil {
		return nil, fmt.Errorf("TCP dial failed: %v", err)
	}

	// uTLS client with Firefox-compatible ClientHello
	uconn := utls.UClient(rawConn, config, utls.HelloFirefox_Auto)

	if err := uconn.Handshake(); err != nil {
		return nil, fmt.Errorf("TLS handshake failed: %v", err)
	}

	state := uconn.ConnectionState()
	fmt.Println("✅ uTLS handshake succeeded")
	fmt.Printf("   Cipher Suite: 0x%x\n", state.CipherSuite)
	fmt.Printf("   TLS Version: 0x%x\n", state.Version)

	return &Connection{conn: uconn}, nil
}
