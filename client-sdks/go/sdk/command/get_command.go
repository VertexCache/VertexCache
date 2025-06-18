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
	"strings"

	"github.com/vertexcache/client-sdks/go/sdk/comm"
	"github.com/vertexcache/client-sdks/go/sdk/model"
)

// GetCommand handles the GET command in VertexCache.
//
// Retrieves the value for a given key from the cache.
// Returns an error if the key is missing or expired.
//
// Requires the client to have READ, READ_WRITE, or ADMIN access.
// This command supports primary key lookups only.
type GetCommand struct {
	CommandBase
	key   string
	value string
}

// NewGetCommand creates a new GetCommand with the provided key.
// Returns an error if the key is empty or invalid.
func NewGetCommand(key string) (*GetCommand, error) {
	if strings.TrimSpace(key) == "" {
		return nil, model.NewVertexCacheSdkException("GET command requires a non-empty key")
	}
	return &GetCommand{key: key}, nil
}

// BuildCommand constructs the full protocol string for the GET command.
func (g *GetCommand) BuildCommand() string {
	return "GET" + COMMAND_SPACER + g.key
}

// ParseResponse handles the raw response returned by the VertexCache server.
func (g *GetCommand) ParseResponse(responseBody string) {
	if strings.EqualFold(responseBody, "(nil)") {
		g.SetSuccessWithResponse("No matching key found, +(nil)")
		return
	}

	if strings.HasPrefix(responseBody, "ERR") {
		g.SetFailure("GET failed: " + responseBody)
	} else {
		g.value = responseBody
	}
}

// Value returns the value retrieved from the GET command, or empty string if not found.
func (g *GetCommand) Value() string {
	return g.value
}

// Execute runs the GetCommand using the shared ExecuteCommand helper.
func (g *GetCommand) Execute(client *comm.ClientConnector) CommandInterface {
	return ExecuteCommand(g, client)
}
