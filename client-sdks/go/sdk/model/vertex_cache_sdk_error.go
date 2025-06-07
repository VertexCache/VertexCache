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
// See the License for the specific language governing permissions and
// limitations under the License.
// ------------------------------------------------------------------------------

package model

import "fmt"

// VertexCacheSdkError represents a consistent SDK error across VertexCache client implementations.
type VertexCacheSdkError struct {
	Message string
	Cause   error
}

// Error implements the error interface.
func (e *VertexCacheSdkError) Error() string {
	if e.Cause != nil {
		return fmt.Sprintf("%s: %v", e.Message, e.Cause)
	}
	return e.Message
}

// NewVertexCacheSdkError creates a new VertexCacheSdkError with message only.
func NewVertexCacheSdkError(msg string) *VertexCacheSdkError {
	return &VertexCacheSdkError{Message: msg}
}

// NewVertexCacheSdkErrorWithCause creates a new VertexCacheSdkError with message and underlying cause.
func NewVertexCacheSdkErrorWithCause(msg string, cause error) *VertexCacheSdkError {
	return &VertexCacheSdkError{
		Message: msg,
		Cause:   cause,
	}
}
