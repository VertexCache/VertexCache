// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// ------------------------------------------------------------------------------

package command

import (
	"github.com/vertexcache/client-sdks/go/sdk/comm"
	"strings"
)

// RESPONSE_OK is the default success indicator from the VertexCache server.
const RESPONSE_OK = "OK"

// COMMAND_SPACER defines the default delimiter between parts of a command string.
const COMMAND_SPACER = " "

// CommandBase defines the foundational structure for all client-issued commands in the VertexCache SDK.
//
// It encapsulates common metadata and behaviors shared by all command types, including:
// - Command type identification (e.g., GET, SET, DEL)
// - Internal tracking for retries and timestamps
// - Role-based authorization levels
//
// Concrete commands should embed CommandBase and override BuildCommand() and optionally ParseResponse().
type CommandBase struct {
	success  bool
	response string
	err      string
}

// Execute sends the constructed command to the VertexCache server and handles the response protocol.
// Returns itself for optional chaining.
func (c *CommandBase) Execute(client *comm.ClientConnector) CommandInterface {
	raw, sendErr := client.Send(c.BuildCommand())
	if sendErr != nil {
		c.success = false
		c.err = sendErr.Error()
		return c
	}

	raw = strings.TrimSpace(raw)

	if strings.HasPrefix(raw, "+") {
		c.response = raw[1:]
		c.ParseResponse(c.response)
		if c.err == "" {
			c.success = true
		}
	} else if strings.HasPrefix(raw, "-") {
		c.success = false
		c.err = raw[1:]
	} else {
		c.success = false
		c.err = "Unexpected response: " + raw
	}

	return c
}

// BuildCommand must be implemented by concrete command types to construct the full protocol command string.
func (c *CommandBase) BuildCommand() string {
	panic("BuildCommand() must be implemented by concrete command")
}

// ParseResponse processes a successful response string.
// Default implementation does nothing. May be overridden.
func (c *CommandBase) ParseResponse(responseBody string) {
	// Optional override
}

// SetFailure marks the command as failed with a specific error message.
func (c *CommandBase) SetFailure(response string) {
	c.success = false
	c.err = response
}

// SetSuccess marks the command as successful with default OK response.
func (c *CommandBase) SetSuccess() {
	c.success = true
	c.response = RESPONSE_OK
	c.err = ""
}

// SetSuccessWithResponse marks the command as successful with a custom response.
func (c *CommandBase) SetSuccessWithResponse(response string) {
	c.success = true
	c.response = response
	c.err = ""
}

// StatusMessage returns the response if successful, or error otherwise.
func (c *CommandBase) StatusMessage() string {
	if c.success {
		return c.response
	}
	return c.err
}

// IsSuccess reports whether the command succeeded.
func (c *CommandBase) IsSuccess() bool {
	return c.success
}

// Response returns the raw server response.
func (c *CommandBase) Response() string {
	return c.response
}

// Error returns the error message from a failed command.
func (c *CommandBase) Error() string {
	return c.err
}
