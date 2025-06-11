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

package comm_test

import (
	"strings"
	"testing"

	"github.com/vertexcache/client-sdks/go/sdk/comm"
	"github.com/vertexcache/client-sdks/go/sdk/model"
)

func TestClientConnector_IsConnected_Default(t *testing.T) {
	opt := model.NewClientOption()
	connector := comm.NewClientConnector(opt)
	if connector.IsConnected() {
		t.Errorf("expected IsConnected to return false initially")
	}
}

func TestClientConnector_Close_NoPanic(t *testing.T) {
	opt := model.NewClientOption()
	connector := comm.NewClientConnector(opt)
	connector.Close() // should not panic
	if connector.IsConnected() {
		t.Errorf("expected IsConnected to return false after Close")
	}
}

func TestClientConnector_BuildIdentCommand_Format(t *testing.T) {
	opt := model.NewClientOption()
	opt.ClientID = "abc"
	opt.ClientToken = "xyz"
	cmd := opt.BuildIdentCommand()
	if !strings.HasPrefix(cmd, "IDENT {") {
		t.Errorf("expected IDENT command to start with 'IDENT {'")
	}
	if !strings.Contains(cmd, "abc") || !strings.Contains(cmd, "xyz") {
		t.Errorf("expected client ID and token to be present in IDENT command")
	}
}
