package console_app

import (
	"strings"
)

// CommandParser replicates the functionality of the C# CommandParser class.
type CommandParser struct{}

// Parse parses the input string into a command and its arguments.
// It trims the input, splits it by spaces, and lowercases the command.
func (CommandParser) Parse(input string) (string, []string) {
	parts := strings.Fields(strings.TrimSpace(input))
	if len(parts) == 0 {
		return "", []string{}
	}
	command := strings.ToLower(parts[0])
	args := []string{}
	if len(parts) > 1 {
		args = parts[1:]
	}
	return command, args
}
