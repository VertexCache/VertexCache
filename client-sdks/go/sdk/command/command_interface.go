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
	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/comm"
)

// CommandInterface represents a generic interface for all command types
// that can be executed by the VertexCache SDK.
//
// Implementations of this interface define how a command is executed,
// report success status, and expose response/error/status details.
//
// This allows polymorphic handling of commands like GET, SET, and DEL
// within the transport layer.
type CommandInterface interface {
	// Execute runs the command using the provided ClientConnector.
	// Returns itself for optional chaining.
	Execute(client *comm.ClientConnector) CommandInterface

	// IsSuccess reports whether the command execution succeeded.
	IsSuccess() bool

	// Response returns the full raw server response string.
	Response() string

	// Error returns any error message from the command execution.
	Error() string

	// StatusMessage returns a high-level summary message for the operation.
	StatusMessage() string

	// Required for ExecuteCommand helper
	BuildCommand() string
	ParseResponse(responseBody string)
	SetFailure(error string)
	SetSuccessWithResponse(response string)
}
