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

	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/comm"
	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/model"
)

// GetSecondaryIdxTwoCommand handles the GETIDX2 command in VertexCache.
//
// Retrieves the value for a given key via the tertiary index (idx2).
// Returns an error if the key is missing or expired.
//
// Requires the client to have READ, READ_WRITE, or ADMIN access.
// This command supports idx2-based lookups only.
type GetSecondaryIdxTwoCommand struct {
	CommandBase
	key   string
	value string
}

// NewGetSecondaryIdxTwoCommand creates a new GetSecondaryIdxTwoCommand with the given key.
// Returns an error if the key is empty or invalid.
func NewGetSecondaryIdxTwoCommand(key string) (*GetSecondaryIdxTwoCommand, error) {
	if strings.TrimSpace(key) == "" {
		return nil, model.NewVertexCacheSdkException("GET By Secondary Index (idx2) command requires a non-empty key")
	}
	return &GetSecondaryIdxTwoCommand{key: key}, nil
}

// BuildCommand constructs the raw protocol string for the GETIDX2 command.
func (g *GetSecondaryIdxTwoCommand) BuildCommand() string {
	return "GETIDX2" + COMMAND_SPACER + g.key
}

// ParseResponse handles the raw response from the VertexCache server.
func (g *GetSecondaryIdxTwoCommand) ParseResponse(responseBody string) {
	if strings.EqualFold(responseBody, "(nil)") {
		g.SetSuccessWithResponse("No matching key found, +(nil)")
		return
	}

	if strings.HasPrefix(responseBody, "ERR") {
		g.SetFailure("GETIDX2 failed: " + responseBody)
	} else {
		g.value = responseBody
	}
}

// Value returns the value retrieved via idx2 lookup.
func (g *GetSecondaryIdxTwoCommand) Value() string {
	return g.value
}

// Execute runs the command using the shared ExecuteCommand helper.
func (g *GetSecondaryIdxTwoCommand) Execute(client *comm.ClientConnector) CommandInterface {
	return ExecuteCommand(g, client)
}
