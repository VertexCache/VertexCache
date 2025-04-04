package transport

import (
	"bufio"
	"crypto/tls"
	"net"
)

// Connection wraps a secure network connection.
type Connection struct {
	conn   net.Conn
	reader *bufio.Reader
}

// NewTLSConnection establishes a TLS connection to the given address.
func NewTLSConnection(address string, tlsConfig *tls.Config) (*Connection, error) {
	conn, err := tls.Dial("tcp", address, tlsConfig)
	if err != nil {
		return nil, err
	}
	return &Connection{
		conn:   conn,
		reader: bufio.NewReader(conn),
	}, nil
}

// Send writes a raw line of data to the connection.
func (c *Connection) Send(data string) error {
	_, err := c.conn.Write([]byte(data))
	return err
}

// ReceiveLine reads a full line from the connection.
func (c *Connection) ReceiveLine() (string, error) {
	line, err := c.reader.ReadString('\n')
	if err != nil {
		return "", err
	}
	return line[:len(line)-1], nil // trim newline
}

// Close gracefully closes the connection.
func (c *Connection) Close() error {
	return c.conn.Close()
}
