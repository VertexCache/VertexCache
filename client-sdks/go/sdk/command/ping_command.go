package command

import (
	"github.com/vertexcache/client-sdks/go/sdk/comm"
	"strings"
)

// PingCommand handles the PING command in VertexCache.
//
// This command is used to check server availability and latency.
// It returns a basic "PONG" response and can be used by clients to verify liveness.
//
// PING is always allowed regardless of authentication state or client role.
// It does not require access validation or key arguments.
type PingCommand struct {
	CommandBase
}

// NewPingCommand creates a new instance of PingCommand.
func NewPingCommand() *PingCommand {
	return &PingCommand{}
}

// BuildCommand constructs the PING protocol command.
func (p *PingCommand) BuildCommand() string {
	return "PING"
}

// ParseResponse validates that a PONG was returned by the server.
func (p *PingCommand) ParseResponse(responseBody string) {
	if strings.TrimSpace(responseBody) == "" || !strings.EqualFold(responseBody, "PONG") {
		p.SetFailure("PONG not received")
	}
}

// Execute runs the PingCommand using the shared ExecuteCommand helper.
func (p *PingCommand) Execute(client *comm.ClientConnector) CommandInterface {
	return ExecuteCommand(p, client)
}
