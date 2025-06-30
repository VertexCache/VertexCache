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

package model_test

import (
	"strings"
	"testing"

	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/model"
)

func TestDefaults(t *testing.T) {
	opt := model.NewClientOption()

	if opt.ClientID != "sdk-client" {
		t.Errorf("Expected default ClientID to be 'sdk-client', got '%s'", opt.ClientID)
	}
	if opt.ClientToken != "" {
		t.Errorf("Expected default ClientToken to be empty, got '%s'", opt.ClientToken)
	}
	if opt.ServerHost != "127.0.0.1" {
		t.Errorf("Expected default ServerHost to be '127.0.0.1', got '%s'", opt.ServerHost)
	}
	if opt.ServerPort != 50505 {
		t.Errorf("Expected default ServerPort to be 50505, got %d", opt.ServerPort)
	}
	if opt.EnableTLSEncryption {
		t.Errorf("Expected EnableTLSEncryption to be false")
	}
	if opt.VerifyCertificate {
		t.Errorf("Expected VerifyCertificate to be false")
	}
	if opt.ReadTimeout != 3000 {
		t.Errorf("Expected ReadTimeout to be 3000, got %d", opt.ReadTimeout)
	}
	if opt.ConnectTimeout != 3000 {
		t.Errorf("Expected ConnectTimeout to be 3000, got %d", opt.ConnectTimeout)
	}
	if opt.EncryptionMode != model.None {
		t.Errorf("Expected EncryptionMode to be NONE")
	}
	if ident := opt.BuildIdentCommand(); ident == "" {
		t.Errorf("Expected non-empty IDENT command")
	}
}

func TestSetValues(t *testing.T) {
	opt := model.NewClientOption()

	opt.ClientID = "test-client"
	opt.ClientToken = "token123"
	opt.ServerHost = "192.168.1.100"
	opt.ServerPort = 9999
	opt.EnableTLSEncryption = true
	opt.VerifyCertificate = true
	opt.TLSCertificate = "cert"
	opt.ConnectTimeout = 1234
	opt.ReadTimeout = 5678
	opt.EncryptionMode = model.Symmetric

	if opt.ClientID != "test-client" {
		t.Errorf("ClientID mismatch")
	}
	if opt.ClientToken != "token123" {
		t.Errorf("ClientToken mismatch")
	}
	if opt.ServerHost != "192.168.1.100" {
		t.Errorf("ServerHost mismatch")
	}
	if opt.ServerPort != 9999 {
		t.Errorf("ServerPort mismatch")
	}
	if !opt.EnableTLSEncryption {
		t.Errorf("Expected EnableTLSEncryption to be true")
	}
	if !opt.VerifyCertificate {
		t.Errorf("Expected VerifyCertificate to be true")
	}
	if opt.TLSCertificate != "cert" {
		t.Errorf("TLSCertificate mismatch")
	}
	if opt.ConnectTimeout != 1234 {
		t.Errorf("ConnectTimeout mismatch")
	}
	if opt.ReadTimeout != 5678 {
		t.Errorf("ReadTimeout mismatch")
	}
	if opt.EncryptionMode != model.Symmetric {
		t.Errorf("EncryptionMode mismatch")
	}
}

func TestIdentCommandGeneration(t *testing.T) {
	opt := model.NewClientOption()
	opt.ClientID = "my-id"
	opt.ClientToken = "my-token"
	expected := `IDENT {"client_id":"my-id", "token":"my-token"}`
	actual := opt.BuildIdentCommand()

	if actual != expected {
		t.Errorf("Expected IDENT command to be '%s', got '%s'", expected, actual)
	}
}

func TestNullTokenAndIdFallback(t *testing.T) {
	opt := model.NewClientOption()
	opt.ClientID = ""
	opt.ClientToken = ""
	ident := opt.BuildIdentCommand()

	if !strings.Contains(ident, `"client_id":""`) {
		t.Errorf("Expected empty client_id fallback in IDENT")
	}
	if !strings.Contains(ident, `"token":""`) {
		t.Errorf("Expected empty token fallback in IDENT")
	}
}
