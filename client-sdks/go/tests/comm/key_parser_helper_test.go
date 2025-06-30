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
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/comm"
)

const validPEM = `
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
EwIDAQAB
-----END PUBLIC KEY-----
`

const invalidPEM = "-----BEGIN PUBLIC KEY-----INVALID-----END PUBLIC KEY-----"
const validSharedKey = "YWJjZGVmZ2hpamtsbW5vcA==" // "abcdefghijklmnop"
const invalidSharedKey = "%%%INVALID%%%"

func TestConfigPublicKeyIfEnabled_Valid(t *testing.T) {
	_, err := comm.ConfigPublicKeyIfEnabled(validPEM)
	assert.NoError(t, err)
}

func TestConfigPublicKeyIfEnabled_Invalid(t *testing.T) {
	_, err := comm.ConfigPublicKeyIfEnabled(invalidPEM)
	assert.Error(t, err)
	assert.Equal(t, "Invalid public key", err.Error())
}

func TestConfigSharedKeyIfEnabled_Valid(t *testing.T) {
	key, err := comm.ConfigSharedKeyIfEnabled(validSharedKey)
	assert.NoError(t, err)
	assert.Equal(t, 16, len(key))
	assert.Equal(t, "abcdefghijklmnop", string(key))
}

func TestConfigSharedKeyIfEnabled_Invalid(t *testing.T) {
	_, err := comm.ConfigSharedKeyIfEnabled(invalidSharedKey)
	assert.Error(t, err)
	assert.Equal(t, "Invalid shared key", err.Error())
}
