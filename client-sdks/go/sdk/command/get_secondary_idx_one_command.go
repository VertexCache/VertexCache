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

// GetSecondaryIdxOneCommand handles the GETIDX1 command in VertexCache.
//
// Retrieves the value for a given key via the secondary index (idx1).
// Returns an error if the key is missing or expired.
//
// Requires the client to have READ, READ_WRITE, or ADMIN access.
// This command supports idx1-based lookups only.
type GetSecondaryIdxOneCommand struct {
	CommandBase
	key   string
	value string
}

// NewGetSecondaryIdxOneCommand creates a new GetSecondaryIdxOneCommand with the given key.
// Returns an error if the key is empty or invalid.
func NewGetSecondaryIdxOneCommand(key string) (*GetSecondaryIdxOneCommand, error) {
	if strings.TrimSpace(key) == "" {
		return nil, model.NewVertexCacheSdkException("GET By Secondary Index (idx1) command requires a non-empty key")
	}
	return &GetSecondaryIdxOneCommand{key: key}, nil
}

// BuildCommand constructs the raw protocol string for the GETIDX1 command.
func (g *GetSecondaryIdxOneCommand) BuildCommand() string {
	return "GETIDX1" + COMMAND_SPACER + g.key
}

// ParseResponse handles the raw response from the VertexCache server.
func (g *GetSecondaryIdxOneCommand) ParseResponse(responseBody string) {
	if strings.EqualFold(responseBody, "(nil)") {
		g.SetSuccessWithResponse("No matching key found, +(nil)")
		return
	}

	if strings.HasPrefix(responseBody, "ERR") {
		g.SetFailure("GETIDX1 failed: " + responseBody)
	} else {
		g.value = responseBody
	}
}

// Value returns the value retrieved via idx1 lookup.
func (g *GetSecondaryIdxOneCommand) Value() string {
	return g.value
}

// Execute runs the command using the shared ExecuteCommand helper.
func (g *GetSecondaryIdxOneCommand) Execute(client *comm.ClientConnector) CommandInterface {
	return ExecuteCommand(g, client)
}
