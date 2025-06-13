// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache
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

package comm

import (
	"bytes"
	"encoding/binary"
	"errors"
	"fmt"
	"io"
)

const (
	MaxMessageSize  = 10 * 1024 * 1024 // 10MB
	ProtocolVersion = 0x00000101
)

// WriteFramedMessage writes a framed message:
// [4 bytes length][4 bytes protocol version][payload]
func WriteFramedMessage(w io.Writer, payload []byte) error {
	if len(payload) > MaxMessageSize {
		return fmt.Errorf("message too large: %d", len(payload))
	}

	var header bytes.Buffer

	// Write length (int32, big-endian)
	if err := binary.Write(&header, binary.BigEndian, int32(len(payload))); err != nil {
		return err
	}

	// Write protocol version (uint32, big-endian)
	if err := binary.Write(&header, binary.BigEndian, uint32(ProtocolVersion)); err != nil {
		return err
	}

	// Write header + payload
	if _, err := w.Write(header.Bytes()); err != nil {
		return err
	}
	if _, err := w.Write(payload); err != nil {
		return err
	}
	return nil
}

// ReadFramedMessage reads a framed message:
// [4 bytes length][4 bytes protocol version][payload]
func ReadFramedMessage(r io.Reader) ([]byte, error) {
	header := make([]byte, 8)
	n, err := io.ReadFull(r, header)
	if err != nil {
		if err == io.ErrUnexpectedEOF || err == io.EOF || n < 8 {
			return nil, nil // treat short header as non-fatal
		}
		return nil, err
	}

	length := int32(binary.BigEndian.Uint32(header[0:4]))
	version := binary.BigEndian.Uint32(header[4:8])

	if version != ProtocolVersion {
		return nil, errors.New("unsupported protocol version")
	}
	if length <= 0 || length > MaxMessageSize {
		return nil, fmt.Errorf("invalid message length: %d", length)
	}

	payload := make([]byte, length)
	if _, err := io.ReadFull(r, payload); err != nil {
		return nil, err
	}
	return payload, nil
}
