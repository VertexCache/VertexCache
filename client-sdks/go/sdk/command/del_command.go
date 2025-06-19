// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
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
	"fmt"
	"strings"

	"github.com/vertexcache/client-sdks/go/sdk/comm"
	"github.com/vertexcache/client-sdks/go/sdk/model"
)

// DelCommand handles the DEL command in VertexCache.
//
// Deletes a key and its associated value from the cache.
// If the system is configured to allow idempotent deletes,
// then deleting a non-existent key still returns a success response ("OK DEL (noop)").
//
// Requires the client to have WRITE or ADMIN access.
//
// Configuration:
// - del_command_idempotent: when true, deletion of missing keys does not result in an error.
type DelCommand struct {
	CommandBase
	key string
}

// NewDelCommand creates a new DelCommand instance with the given key.
// Returns an error if the key is empty or invalid.
func NewDelCommand(key string) (*DelCommand, error) {
	if strings.TrimSpace(key) == "" {
		return nil, model.NewVertexCacheSdkException(fmt.Sprintf("%s command requires a non-empty key", CommandDel))
	}
	return &DelCommand{key: key}, nil
}

// BuildCommand assembles the raw protocol string for the DEL command.
func (d *DelCommand) BuildCommand() string {
	return string(CommandDel) + COMMAND_SPACER + d.key
}

// ParseResponse interprets the response from the VertexCache server after a DEL command.
func (d *DelCommand) ParseResponse(responseBody string) {
	if strings.HasPrefix(responseBody, "ERR") {
		d.SetFailure("DEL failed: " + responseBody)
	} else {
		d.SetSuccessWithResponse(responseBody)
	}
}

// Execute runs the DelCommand using the shared ExecuteCommand helper.
func (d *DelCommand) Execute(client *comm.ClientConnector) CommandInterface {
	return ExecuteCommand(d, client)
}
