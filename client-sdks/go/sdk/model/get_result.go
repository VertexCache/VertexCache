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

// GetResult represents the result of a GET command in the VertexCache SDK.
//
// It embeds CommandResult and includes an additional field `value`,
// which contains the cached value associated with the requested key, if present.
type GetResult struct {
	CommandResult
	value string
}

// NewGetResult creates and returns a new GetResult instance.
func NewGetResult(success bool, message, value string) *GetResult {
	return &GetResult{
		CommandResult: *NewCommandResult(success, message),
		value:         value,
	}
}

// Value returns the cached value retrieved by the GET command.
func (r *GetResult) Value() string {
	return r.value
}
