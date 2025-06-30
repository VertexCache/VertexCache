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
// See the License for the specific language governing permissions and
// limitations under the License.
// ------------------------------------------------------------------------------

package sdk

import (
	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/comm"
	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/command"
	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/model"
)

// VertexCacheSDK serves as the main entry point for interacting with the VertexCache server.
//
// It provides methods to perform cache operations such as GET, SET, and DEL, and abstracts away
// the underlying TCP transport details.
//
// This SDK handles encryption (symmetric/asymmetric), TLS negotiation, authentication, and framing
// of commands and responses. Errors are surfaced through structured CommandResult and GetResult
// to aid client integration.
type VertexCacheSDK struct {
	client *comm.ClientConnector
}

// NewVertexCacheSDK initializes a new VertexCacheSDK instance with the given client option.
func NewVertexCacheSDK(option *model.ClientOption) *VertexCacheSDK {
	return &VertexCacheSDK{
		client: comm.NewClientConnector(option),
	}
}

// OpenConnection establishes the connection to the VertexCache server.
func (sdk *VertexCacheSDK) OpenConnection() error {
	return sdk.client.Connect()
}

// Close terminates the connection to the server.
func (sdk *VertexCacheSDK) Close() {
	sdk.client.Close()
}

// IsConnected returns true if the underlying client is connected.
func (sdk *VertexCacheSDK) IsConnected() bool {
	return sdk.client.IsConnected()
}

// Ping sends a PING command to the server.
func (sdk *VertexCacheSDK) Ping() *model.CommandResult {
	cmd := command.NewPingCommand().Execute(sdk.client).(*command.PingCommand)
	return model.NewCommandResult(cmd.IsSuccess(), cmd.StatusMessage())
}

// Set stores a key-value pair in the cache.
func (sdk *VertexCacheSDK) Set(key, value string) *model.CommandResult {
	cmd, err := command.NewSetCommand(key, value, nil, nil)
	if err != nil {
		return model.NewCommandResult(false, err.Error())
	}
	cmd.Execute(sdk.client)
	return model.NewCommandResult(cmd.IsSuccess(), cmd.StatusMessage())
}

// SetStrict stores a key-value pair in the cache, returning result and error.
func (sdk *VertexCacheSDK) SetStrict(key, value string) (*model.CommandResult, error) {
	cmd, err := command.NewSetCommand(key, value, nil, nil)
	if err != nil {
		return nil, err
	}
	cmd.Execute(sdk.client)
	return model.NewCommandResult(cmd.IsSuccess(), cmd.StatusMessage()), nil
}

// SetWithSecondary stores a key-value pair with a secondary index.
func (sdk *VertexCacheSDK) SetWithSecondary(key, value, idx1 string) *model.CommandResult {
	cmd, err := command.NewSetCommand(key, value, &idx1, nil)
	if err != nil {
		return model.NewCommandResult(false, err.Error())
	}
	cmd.Execute(sdk.client)
	return model.NewCommandResult(cmd.IsSuccess(), cmd.StatusMessage())
}

// SetWithSecondaryStrict is the strict version of SetWithSecondary.
func (sdk *VertexCacheSDK) SetWithSecondaryStrict(key, value, idx1 string) (*model.CommandResult, error) {
	cmd, err := command.NewSetCommand(key, value, &idx1, nil)
	if err != nil {
		return nil, err
	}
	cmd.Execute(sdk.client)
	return model.NewCommandResult(cmd.IsSuccess(), cmd.StatusMessage()), nil
}

// SetWithSecondaryAndTertiary stores a key-value pair with secondary and tertiary indexes.
func (sdk *VertexCacheSDK) SetWithSecondaryAndTertiary(key, value, idx1, idx2 string) *model.CommandResult {
	cmd, err := command.NewSetCommand(key, value, &idx1, &idx2)
	if err != nil {
		return model.NewCommandResult(false, err.Error())
	}
	cmd.Execute(sdk.client)
	return model.NewCommandResult(cmd.IsSuccess(), cmd.StatusMessage())
}

// SetWithSecondaryAndTertiaryStrict is the strict version of SetWithSecondaryAndTertiary.
func (sdk *VertexCacheSDK) SetWithSecondaryAndTertiaryStrict(key, value, idx1, idx2 string) (*model.CommandResult, error) {
	cmd, err := command.NewSetCommand(key, value, &idx1, &idx2)
	if err != nil {
		return nil, err
	}
	cmd.Execute(sdk.client)
	return model.NewCommandResult(cmd.IsSuccess(), cmd.StatusMessage()), nil
}

// Del deletes a key from the cache.
func (sdk *VertexCacheSDK) Del(key string) *model.CommandResult {
	cmd, err := command.NewDelCommand(key)
	if err != nil {
		return model.NewCommandResult(false, err.Error())
	}
	cmd.Execute(sdk.client)
	return model.NewCommandResult(cmd.IsSuccess(), cmd.StatusMessage())
}

// DelStrict deletes a key and returns both result and error.
func (sdk *VertexCacheSDK) DelStrict(key string) (*model.CommandResult, error) {
	cmd, err := command.NewDelCommand(key)
	if err != nil {
		return nil, err
	}
	cmd.Execute(sdk.client)
	return model.NewCommandResult(cmd.IsSuccess(), cmd.StatusMessage()), nil
}

// Get retrieves a value by primary key.
func (sdk *VertexCacheSDK) Get(key string) *model.GetResult {
	cmd, err := command.NewGetCommand(key)
	if err != nil {
		return model.NewGetResult(false, err.Error(), "")
	}
	cmd.Execute(sdk.client)
	return model.NewGetResult(cmd.IsSuccess(), cmd.StatusMessage(), cmd.Value())
}

// GetStrict retrieves a value by primary key with strict result.
func (sdk *VertexCacheSDK) GetStrict(key string) (*model.GetResult, error) {
	cmd, err := command.NewGetCommand(key)
	if err != nil {
		return nil, err
	}
	cmd.Execute(sdk.client)
	return model.NewGetResult(cmd.IsSuccess(), cmd.StatusMessage(), cmd.Value()), nil
}

// GetBySecondaryIndex retrieves a value by idx1.
func (sdk *VertexCacheSDK) GetBySecondaryIndex(idx1 string) *model.GetResult {
	cmd, err := command.NewGetSecondaryIdxOneCommand(idx1)
	if err != nil {
		return model.NewGetResult(false, err.Error(), "")
	}
	cmd.Execute(sdk.client)
	return model.NewGetResult(cmd.IsSuccess(), cmd.StatusMessage(), cmd.Value())
}

// GetBySecondaryIndexStrict retrieves a value by idx1 with strict result.
func (sdk *VertexCacheSDK) GetBySecondaryIndexStrict(idx1 string) (*model.GetResult, error) {
	cmd, err := command.NewGetSecondaryIdxOneCommand(idx1)
	if err != nil {
		return nil, err
	}
	cmd.Execute(sdk.client)
	return model.NewGetResult(cmd.IsSuccess(), cmd.StatusMessage(), cmd.Value()), nil
}

// GetByTertiaryIndex retrieves a value by idx2.
func (sdk *VertexCacheSDK) GetByTertiaryIndex(idx2 string) *model.GetResult {
	cmd, err := command.NewGetSecondaryIdxTwoCommand(idx2)
	if err != nil {
		return model.NewGetResult(false, err.Error(), "")
	}
	cmd.Execute(sdk.client)
	return model.NewGetResult(cmd.IsSuccess(), cmd.StatusMessage(), cmd.Value())
}

// GetByTertiaryIndexStrict retrieves a value by idx2 with strict result.
func (sdk *VertexCacheSDK) GetByTertiaryIndexStrict(idx2 string) (*model.GetResult, error) {
	cmd, err := command.NewGetSecondaryIdxTwoCommand(idx2)
	if err != nil {
		return nil, err
	}
	cmd.Execute(sdk.client)
	return model.NewGetResult(cmd.IsSuccess(), cmd.StatusMessage(), cmd.Value()), nil
}
