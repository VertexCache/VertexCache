package protocol

import "fmt"

// CommandType defines the type of command being issued to the VertexCache server.
type CommandType string

const (
	CommandSet    CommandType = "SET"
	CommandGet    CommandType = "GET"
	CommandDelete CommandType = "DELETE"
	CommandPing   CommandType = "PING"
)

// Command represents a protocol-level command to be serialized and sent to the server.
type Command struct {
	Type  CommandType
	Key   string
	Value string // Only used by SET
}

// Serialize encodes the command into the VertexCache wire format.
func (c *Command) Serialize() string {
	switch c.Type {
	case CommandSet:
		return fmt.Sprintf("SET %s %s\n", c.Key, c.Value)
	case CommandGet, CommandDelete:
		return fmt.Sprintf("%s %s\n", c.Type, c.Key)
	case CommandPing:
		return "PING\n"
	default:
		return ""
	}
}
