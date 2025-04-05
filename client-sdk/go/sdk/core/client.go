package core

import (
	"strings"

	"github.com/vertexcache/client-sdk/go/sdk/protocol"
	"github.com/vertexcache/client-sdk/go/sdk/results"
)

type Conn interface {
	Send([]byte) error
	Receive([]byte) (int, error)
	Close() error
}

type Client struct {
	conn Conn
}

func NewClient(conn Conn) *Client {
	return &Client{conn: conn}
}

func (c *Client) RunCommand(command string) *results.Result {
	if strings.TrimSpace(command) == "" {
		err := results.New(results.ErrInvalidCommand, "key cannot be empty", nil)
		return results.NewFailure(err)
	}

	cmd := &protocol.Command{
		Type: protocol.CommandType(strings.ToUpper(strings.Fields(command)[0])),
	}
	raw := cmd.Serialize()

	if err := c.conn.Send([]byte(raw)); err != nil {
		sdkErr := results.New(results.ErrConnection, "failed to send command", err)
		return results.NewFailure(sdkErr)
	}

	buffer := make([]byte, 4096)
	n, err := c.conn.Receive(buffer)
	if err != nil {
		sdkErr := results.New(results.ErrConnection, "failed to receive response", err)
		return results.NewFailure(sdkErr)
	}

	return results.NewSuccess(string(buffer[:n]))
}
