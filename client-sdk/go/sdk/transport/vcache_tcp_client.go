package transport

import (
	"bufio"
	"crypto/tls"
	"crypto/x509"
	"errors"
	"fmt"
	"net"
	"strings"
	"time"
)

type VCacheTcpClient struct {
	conn      net.Conn
	reader    *bufio.Reader
	writer    *bufio.Writer
	timeoutMs int
}

func NewVCacheTcpClient(host string, port int, useTLS bool, verifyCert bool, certPem string, timeoutMs int) (*VCacheTcpClient, error) {
	address := fmt.Sprintf("%s:%d", host, port)
	dialer := &net.Dialer{Timeout: time.Duration(timeoutMs) * time.Millisecond}

	conn, err := dialer.Dial("tcp", address)
	if err != nil {
		return nil, fmt.Errorf("TCP dial failed: %w", err)
	}

	var finalConn net.Conn = conn

	if useTLS {
		tlsConfig := &tls.Config{
			InsecureSkipVerify: !verifyCert,
			ServerName:         host,
		}

		if verifyCert && strings.TrimSpace(certPem) != "" {
			certPool := x509.NewCertPool()
			if !certPool.AppendCertsFromPEM([]byte(certPem)) {
				return nil, errors.New("failed to parse PEM certificate")
			}
			tlsConfig.RootCAs = certPool
		}

		tlsConn := tls.Client(conn, tlsConfig)
		if err := tlsConn.Handshake(); err != nil {
			return nil, fmt.Errorf("TLS handshake failed: %w", err)
		}
		finalConn = tlsConn
	}

	client := &VCacheTcpClient{
		conn:      finalConn,
		reader:    bufio.NewReader(finalConn),
		writer:    bufio.NewWriter(finalConn),
		timeoutMs: timeoutMs,
	}

	return client, nil
}

func (c *VCacheTcpClient) SendAsync(message string) (string, error) {
	message = strings.TrimSpace(message) + "\n"

	if _, err := c.writer.WriteString(message); err != nil {
		return "", fmt.Errorf("write failed: %w", err)
	}
	if err := c.writer.Flush(); err != nil {
		return "", fmt.Errorf("flush failed: %w", err)
	}

	response, err := c.reader.ReadString('\n')
	if err != nil {
		return "", fmt.Errorf("read failed: %w", err)
	}

	return strings.TrimSpace(response), nil
}

func (c *VCacheTcpClient) Dispose() {
	if c.conn != nil {
		c.conn.Close()
	}
}
