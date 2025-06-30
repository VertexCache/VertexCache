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
	"bytes"
	"encoding/hex"
	"strings"
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/comm"
)

func TestWriteThenReadFramedMessage(t *testing.T) {
	original := "Hello VertexCache"
	var buf bytes.Buffer
	err := comm.WriteFramedMessage(&buf, []byte(original))
	assert.NoError(t, err)

	payload, err := comm.ReadFramedMessage(&buf)
	assert.NoError(t, err)
	assert.Equal(t, []byte(original), payload)
}

func TestInvalidVersionByte(t *testing.T) {
	frame := []byte{0, 0, 0, 3, 0x02, 'a', 'b', 'c'}
	buf := bytes.NewReader(frame)

	_, err := comm.ReadFramedMessage(buf)
	assert.ErrorContains(t, err, "unsupported protocol version")
}

func TestTooShortHeaderReturnsNil(t *testing.T) {
	buf := bytes.NewReader([]byte{0x01, 0x02})
	payload, err := comm.ReadFramedMessage(buf)
	assert.NoError(t, err)
	assert.Nil(t, payload)
}

func TestTooLargePayloadRejected(t *testing.T) {
	tooBig := make([]byte, comm.MaxMessageSize+1)
	var buf bytes.Buffer
	err := comm.WriteFramedMessage(&buf, tooBig)
	assert.ErrorContains(t, err, "message too large")
}

func TestWriteEmptyPayloadThenReadShouldFail(t *testing.T) {
	var buf bytes.Buffer
	err := comm.WriteFramedMessage(&buf, []byte{})
	assert.NoError(t, err)

	_, err = comm.ReadFramedMessage(&buf)
	assert.ErrorContains(t, err, "invalid message length")
}

func TestUtf8MultibytePayload(t *testing.T) {
	original := "你好, VertexCache 🚀"
	var buf bytes.Buffer
	err := comm.WriteFramedMessage(&buf, []byte(original))
	assert.NoError(t, err)

	payload, err := comm.ReadFramedMessage(&buf)
	assert.NoError(t, err)
	assert.Equal(t, []byte(original), payload)
}

func TestHexDumpForInterSdkComparison(t *testing.T) {
	var buf bytes.Buffer
	err := comm.WriteFramedMessage(&buf, []byte("ping"))
	assert.NoError(t, err)

	hexDump := strings.ToUpper(hex.EncodeToString(buf.Bytes()))
	t.Logf("Framed hex: %s", hexDump)
}
