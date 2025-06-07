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

package model

import (
	"fmt"
)

// ClientOption is a configuration container for initializing the VertexCache SDK client.
//
// This struct holds all user-specified options required to establish a connection
// to a VertexCache server, including host, port, TLS settings, authentication tokens,
// encryption modes (asymmetric or symmetric), and related keys or certificates.
//
// It provides a flexible way to customize client behavior, including security preferences.
type ClientOption struct {
	ClientID    string
	ClientToken string

	ServerHost string
	ServerPort int

	EnableTLSEncryption bool
	TLSCertificate      string
	VerifyCertificate   bool

	EncryptionMode       EncryptionMode
	EncryptWithPublicKey bool
	EncryptWithSharedKey bool

	PublicKey           string
	SharedEncryptionKey string

	ReadTimeout    int
	ConnectTimeout int
}

// NewClientOption returns a new ClientOption with default values set.
func NewClientOption() *ClientOption {
	return &ClientOption{
		ClientID:       "sdk-client",
		ServerHost:     "127.0.0.1",
		ServerPort:     50505,
		ReadTimeout:    3000,
		ConnectTimeout: 3000,
		EncryptionMode: None,
	}
}

// BuildIdentCommand constructs the IDENT command used during client handshake.
func (opt *ClientOption) BuildIdentCommand() string {
	clientID := opt.ClientID
	if clientID == "" {
		clientID = ""
	}
	token := opt.ClientToken
	if token == "" {
		token = ""
	}
	return fmt.Sprintf("IDENT {\"client_id\":\"%s\", \"token\":\"%s\"}", clientID, token)
}
