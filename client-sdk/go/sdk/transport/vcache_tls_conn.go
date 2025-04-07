package transport

import (
	"bufio"
	"crypto/tls"
	"strings"
)

type VCacheTlsConn struct {
	conn   *tls.Conn
	reader *bufio.Reader
	writer *bufio.Writer
}

func NewTLSConnection(address string, config *tls.Config) (VCacheConn, error) {
	conn, err := tls.Dial("tcp", address, config)
	if err != nil {
		return nil, err
	}
	return &VCacheTlsConn{
		conn:   conn,
		reader: bufio.NewReader(conn),
		writer: bufio.NewWriter(conn),
	}, nil
}

func (c *VCacheTlsConn) Send(line string) (int, error) {
	n, err := c.writer.WriteString(line + "\n")
	if err != nil {
		return n, err
	}
	err = c.writer.Flush()
	return n, err
}

func (c *VCacheTlsConn) SendRaw(data []byte) (int, error) {
	return c.conn.Write(data)
}

func (c *VCacheTlsConn) ReadLine() (string, error) {
	line, err := c.reader.ReadString('\n')
	return strings.TrimSpace(line), err
}

func (c *VCacheTlsConn) Dispose() {
	if c.conn != nil {
		c.conn.Close()
	}
}
