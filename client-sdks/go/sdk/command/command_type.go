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

// CommandType represents the different types of commands supported by the VertexCache SDK.
//
// Each command type corresponds to a specific cache operation or internal SDK operation,
// such as GET, SET, DEL, or IDENT (used for client identification and authentication).
//
// CommandType is used throughout the SDK to identify and validate command behavior,
// facilitate routing, and enforce permission checks based on role capabilities.
type CommandType string

const (
	// CommandPing represents a PING operation.
	CommandPing CommandType = "PING"

	// CommandSet represents a SET operation.
	CommandSet CommandType = "SET"

	// CommandDel represents a DEL (delete) operation.
	CommandDel CommandType = "DEL"

	// CommandIdx1 represents a secondary index (IDX1) lookup.
	CommandIdx1 CommandType = "IDX1"

	// CommandIdx2 represents a tertiary index (IDX2) lookup.
	CommandIdx2 CommandType = "IDX2"
)

// Keyword returns the string keyword associated with the CommandType.
func (c CommandType) Keyword() string {
	return string(c)
}

// String returns the string representation of the CommandType.
func (c CommandType) String() string {
	return string(c)
}
