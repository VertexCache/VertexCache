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

// SetCommand handles the SET command in VertexCache.
//
// Stores a value in the cache under the specified key, optionally assigning
// secondary (idx1) and tertiary (idx2) indexes for lookup. Existing keys will
// be overwritten. Supports expiration and format validation if configured.
//
// Requires the client to have WRITE or ADMIN access.
//
// Validation:
// - Key and value are required arguments.
// - Optional arguments may include index fields and TTL metadata.
type SetCommand struct {
	CommandBase

	primaryKey   string
	value        string
	secondaryKey string
	tertiaryKey  string
}

// NewSetCommand constructs a SetCommand with required and optional index keys.
// Returns an error if validation fails.
func NewSetCommand(primaryKey, value string, secondaryKey, tertiaryKey *string) (*SetCommand, error) {
	if strings.TrimSpace(primaryKey) == "" {
		return nil, model.NewVertexCacheSdkException("Missing Primary Key")
	}
	if strings.TrimSpace(value) == "" {
		return nil, model.NewVertexCacheSdkException("Missing Value")
	}
	if secondaryKey != nil && strings.TrimSpace(*secondaryKey) == "" {
		return nil, model.NewVertexCacheSdkException("Secondary key can't be empty when used")
	}
	if secondaryKey != nil && tertiaryKey != nil && strings.TrimSpace(*tertiaryKey) == "" {
		return nil, model.NewVertexCacheSdkException("Tertiary key can't be empty when used")
	}

	return &SetCommand{
		primaryKey:   primaryKey,
		value:        value,
		secondaryKey: deref(secondaryKey),
		tertiaryKey:  deref(tertiaryKey),
	}, nil
}

// BuildCommand constructs the SET command protocol string.
func (s *SetCommand) BuildCommand() string {
	var sb strings.Builder

	sb.WriteString(string(CommandSet))
	sb.WriteString(COMMAND_SPACER)
	sb.WriteString(s.primaryKey)
	sb.WriteString(COMMAND_SPACER)
	sb.WriteString(s.value)

	if s.secondaryKey != "" {
		sb.WriteString(COMMAND_SPACER)
		sb.WriteString(string(CommandIdx1))
		sb.WriteString(COMMAND_SPACER)
		sb.WriteString(s.secondaryKey)
	}

	if s.tertiaryKey != "" {
		sb.WriteString(COMMAND_SPACER)
		sb.WriteString(string(CommandIdx2))
		sb.WriteString(COMMAND_SPACER)
		sb.WriteString(s.tertiaryKey)
	}

	return sb.String()
}

// ParseResponse processes the server response to the SET command.
func (s *SetCommand) ParseResponse(responseBody string) {
	if !strings.EqualFold(responseBody, "OK") {
		s.SetFailure("OK Not received")
	} else {
		s.SetSuccess()
	}
}

// Execute runs the command using the shared ExecuteCommand helper.
func (s *SetCommand) Execute(client *comm.ClientConnector) CommandInterface {
	return ExecuteCommand(s, client)
}

// Helper to safely dereference string pointers.
func deref(s *string) string {
	if s == nil {
		return ""
	}
	return *s
}
