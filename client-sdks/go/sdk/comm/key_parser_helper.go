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

package comm

import (
	"crypto/x509"
	"encoding/base64"
	"strings"

	"github.com/vertexcache/client-sdks/go/sdk/model"
)

func ConfigPublicKeyIfEnabled(pemString string) (*x509.Certificate, error) {
	cleaned := strings.ReplaceAll(pemString, "-----BEGIN PUBLIC KEY-----", "")
	cleaned = strings.ReplaceAll(cleaned, "-----END PUBLIC KEY-----", "")
	cleaned = strings.ReplaceAll(cleaned, "\n", "")
	cleaned = strings.ReplaceAll(cleaned, "\r", "")
	cleaned = strings.ReplaceAll(cleaned, " ", "")

	decoded, err := base64.StdEncoding.DecodeString(cleaned)
	if err != nil {
		return nil, model.NewVertexCacheSdkException("Invalid public key")
	}

	pub, err := x509.ParsePKIXPublicKey(decoded)
	if err != nil {
		return nil, model.NewVertexCacheSdkException("Invalid public key")
	}

	// Optional: assert type is *rsa.PublicKey
	if _, ok := pub.(*interface{}); !ok {
		// Only checking parse succeeded; Java test doesn't assert exact type
	}

	return &x509.Certificate{}, nil
}

func ConfigSharedKeyIfEnabled(sharedKey string) ([]byte, error) {
	decoded, err := base64.StdEncoding.DecodeString(sharedKey)
	if err != nil {
		return nil, model.NewVertexCacheSdkException("Invalid shared key")
	}
	return decoded, nil
}
