package protocol

import (
	"fmt"
	"strings"
)

// ParseCommand takes a line of text and attempts to interpret it as a Command.
// Returns a Command pointer and error if the format is invalid.
func ParseCommand(line string) (*Command, error) {
	parts := strings.Fields(line)
	if len(parts) == 0 {
		return nil, fmt.Errorf("empty command line")
	}

	switch parts[0] {
	case "SET":
		if len(parts) < 3 {
			return nil, fmt.Errorf("invalid SET command format")
		}
		return &Command{
			Type:  CommandSet,
			Key:   parts[1],
			Value: strings.Join(parts[2:], " "),
		}, nil

	case "GET":
		if len(parts) != 2 {
			return nil, fmt.Errorf("invalid GET command format")
		}
		return &Command{
			Type: CommandGet,
			Key:  parts[1],
		}, nil

	case "DELETE":
		if len(parts) != 2 {
			return nil, fmt.Errorf("invalid DELETE command format")
		}
		return &Command{
			Type: CommandDelete,
			Key:  parts[1],
		}, nil

	case "PING":
		return &Command{
			Type: CommandPing,
		}, nil

	default:
		return nil, fmt.Errorf("unknown command type: %s", parts[0])
	}
}
