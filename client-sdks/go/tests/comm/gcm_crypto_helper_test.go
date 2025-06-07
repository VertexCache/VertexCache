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
	"crypto/aes"
	"crypto/cipher"
	"encoding/hex"
	"testing"

	"github.com/vertexcache/client-sdks/go/sdk/comm"
)

var key = make([]byte, 32)
var message = []byte("VertexCache secure payload")

func TestEncryptDecryptRoundTrip(t *testing.T) {
	encrypted, err := comm.Encrypt(message, key)
	if err != nil {
		t.Fatal(err)
	}

	decrypted, err := comm.Decrypt(encrypted, key)
	if err != nil {
		t.Fatal(err)
	}

	if !bytes.Equal(message, decrypted) {
		t.Errorf("Expected decrypted to equal original")
	}
}

func TestDecryptFailsOnTamperedCiphertext(t *testing.T) {
	encrypted, err := comm.Encrypt(message, key)
	if err != nil {
		t.Fatal(err)
	}
	encrypted[len(encrypted)-1] ^= 0x01

	_, err = comm.Decrypt(encrypted, key)
	if err == nil {
		t.Errorf("Expected decryption to fail on tampered ciphertext")
	}
}

func TestDecryptFailsIfTooShort(t *testing.T) {
	_, err := comm.Decrypt([]byte{1, 2, 3}, key)
	if err == nil {
		t.Errorf("Expected error on short input")
	}
}

func TestBase64EncodeDecodeRoundTrip(t *testing.T) {
	b64 := comm.EncodeBase64Key(key)
	decoded, err := comm.DecodeBase64Key(b64)
	if err != nil {
		t.Fatal(err)
	}
	if !bytes.Equal(decoded, key) {
		t.Errorf("Base64 decode mismatch")
	}
}

func TestGenerateBase64Key(t *testing.T) {
	encoded, err := comm.GenerateBase64Key()
	if err != nil {
		t.Fatal(err)
	}
	decoded, err := comm.DecodeBase64Key(encoded)
	if err != nil {
		t.Fatal(err)
	}
	if len(decoded) != 32 {
		t.Errorf("Expected 32-byte key")
	}
}

func TestReconciliationEncryptWithFixedIV(t *testing.T) {
	key := make([]byte, 16) // 128-bit all-zero key
	iv := make([]byte, 12)  // 96-bit all-zero IV
	message := []byte("VertexCacheGCMTest")

	block, err := aes.NewCipher(key)
	if err != nil {
		t.Fatal(err)
	}
	aesgcm, err := cipher.NewGCM(block)
	if err != nil {
		t.Fatal(err)
	}
	ciphertext := aesgcm.Seal(nil, iv, message, nil)
	combined := append(iv, ciphertext...)

	plaintext, err := aesgcm.Open(nil, iv, ciphertext, nil)
	if err != nil {
		t.Fatal(err)
	}
	if !bytes.Equal(plaintext, message) {
		t.Errorf("Decrypted does not match original")
	}

	t.Logf("[RECON] Plaintext: %s", string(message))
	t.Logf("[RECON] Key (hex): %s", hex.EncodeToString(key))
	t.Logf("[RECON] IV (hex): %s", hex.EncodeToString(iv))
	t.Logf("[RECON] Encrypted (hex): %s", hex.EncodeToString(combined))
}
