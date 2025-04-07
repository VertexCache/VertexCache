package transport

import (
	"bufio"
	"net"
	"strings"
	"time"
)

type VCacheTcpConn struct {
	conn   net.Conn
	reader *bufio.Reader
	writer *bufio.Writer
}

func NewTCPConnection(address string) (VCacheConn, error) {
	conn, err := net.DialTimeout("tcp", address, 3*time.Second)
	if err != nil {
		return nil, err
	}
	return &VCacheTcpConn{
		conn:   conn,
		reader: bufio.NewReader(conn),
		writer: bufio.NewWriter(conn),
	}, nil
}

func (c *VCacheTcpConn) Send(line string) (int, error) {
	n, err := c.writer.WriteString(line + "\n")
	if err != nil {
		return n, err
	}
	err = c.writer.Flush()
	return n, err
}

func (c *VCacheTcpConn) SendRaw(data []byte) (int, error) {
	return c.conn.Write(data)
}

func (c *VCacheTcpConn) ReadLine() (string, error) {
	line, err := c.reader.ReadString('\n')
	return strings.TrimSpace(line), err
}

func (c *VCacheTcpConn) Dispose() {
	if c.conn != nil {
		c.conn.Close()
	}
}
