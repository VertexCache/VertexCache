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

package model

// CommandResult represents the result of executing a cache command in the VertexCache SDK.
//
// It encapsulates the response status, message, and optional payload returned
// from the server after executing commands such as GET, SET, or DEL.
// SDK consumers use this to determine success and retrieve associated values or errors.
type CommandResult struct {
	success bool
	message string
}

// NewCommandResult creates and returns a new CommandResult instance.
func NewCommandResult(success bool, message string) *CommandResult {
	return &CommandResult{
		success: success,
		message: message,
	}
}

// IsSuccess reports whether the command was executed successfully.
func (r *CommandResult) IsSuccess() bool {
	return r.success
}

// Message returns the response or error message associated with the command.
func (r *CommandResult) Message() string {
	return r.message
}
