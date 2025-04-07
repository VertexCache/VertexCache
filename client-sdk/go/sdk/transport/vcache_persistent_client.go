package transport

import (
	"bufio"
	"fmt"
	"net"
	"strings"

	"vertexcache/sdk/core"
)

type VCachePersistentClient struct {
	options   *core.VertexCacheSdkOptions
	conn      net.Conn
	writer    *bufio.Writer
	reader    *bufio.Reader
	connected bool
}

func NewVCachePersistentClient(options *core.VertexCacheSdkOptions) *VCachePersistentClient {
	return &VCachePersistentClient{
		options: options,
	}
}

func (c *VCachePersistentClient) Connect() error {
	address := fmt.Sprintf("%s:%d", c.options.ServerHost, c.options.ServerPort)
	conn, err := net.Dial("tcp", address)
	if err != nil {
		return err
	}

	c.conn = conn
	c.writer = bufio.NewWriter(conn)
	c.reader = bufio.NewReader(conn)
	c.connected = true
	return nil
}

func (c *VCachePersistentClient) Send(line string) (int, error) {
	if !c.connected {
		if err := c.Connect(); err != nil {
			return 0, err
		}
	}

	n, err := c.writer.WriteString(line + "\n")
	if err != nil {
		return n, err
	}
	err = c.writer.Flush()
	return n, err
}

func (c *VCachePersistentClient) ReadLine() (string, error) {
	if !c.connected {
		if err := c.Connect(); err != nil {
			return "", err
		}
	}
	line, err := c.reader.ReadString('\n')
	return strings.TrimSpace(line), err
}

func (c *VCachePersistentClient) Dispose() {
	if c.conn != nil {
		c.conn.Close()
	}
	c.connected = false
}
