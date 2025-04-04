package core

import (
	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/protocol"
	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/results"
	"github.com/vertexcache/vertexcache/client-sdk/go/sdk/transport"
)

// Client provides a high-level API to interact with a VertexCache server over TLS.
type Client struct {
	conn *transport.Connection
}

// NewClient creates a new VertexCache SDK client using an established transport connection.
func NewClient(conn *transport.Connection) *Client {
	return &Client{conn: conn}
}

// sendCommand serializes a command, sends it, and reads the response line.
func (c *Client) sendCommand(cmd *protocol.Command) *results.Result {
	err := c.conn.Send(cmd.Serialize())
	if err != nil {
		return results.NewFailure(results.New(results.ErrConnection, "failed to send command", err))
	}

	line, err := c.conn.ReceiveLine()
	if err != nil {
		return results.NewFailure(results.New(results.ErrConnection, "failed to receive response", err))
	}

	return results.NewSuccess(line)
}

// Set stores a key-value pair in VertexCache.
func (c *Client) Set(key, value string) *results.Result {
	cmd := &protocol.Command{
		Type:  protocol.CommandSet,
		Key:   key,
		Value: value,
	}
	return c.sendCommand(cmd)
}

// Get retrieves the value associated with a key from VertexCache.
func (c *Client) Get(key string) *results.Result {
	cmd := &protocol.Command{
		Type: protocol.CommandGet,
		Key:  key,
	}
	return c.sendCommand(cmd)
}

// Delete removes a key and its associated value from VertexCache.
func (c *Client) Delete(key string) *results.Result {
	cmd := &protocol.Command{
		Type: protocol.CommandDelete,
		Key:  key,
	}
	return c.sendCommand(cmd)
}

// Ping verifies the connection is alive by sending a ping.
func (c *Client) Ping() *results.Result {
	cmd := &protocol.Command{
		Type: protocol.CommandPing,
	}
	return c.sendCommand(cmd)
}

// Close gracefully closes the connection to the server.
func (c *Client) Close() error {
	if c.conn != nil {
		return c.conn.Close()
	}
	return nil
}
