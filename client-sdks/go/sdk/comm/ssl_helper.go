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
	"crypto/tls"
	"crypto/x509"
	"errors"
)

func CreateVerifiedSocketFactory(pemCert string, serverHost string) (*tls.Config, error) {
	pool := x509.NewCertPool()
	ok := pool.AppendCertsFromPEM([]byte(pemCert))
	if !ok {
		return nil, errors.New("failed to parse PEM certificate")
	}

	return &tls.Config{
		RootCAs:            pool,
		InsecureSkipVerify: false,
		ServerName:         serverHost,
	}, nil
}

func CreateInsecureSocketFactory(serverHost string) *tls.Config {
	return &tls.Config{
		InsecureSkipVerify: true,
		ServerName:         serverHost,
		MinVersion:         tls.VersionTLS12,
		CipherSuites: []uint16{
			tls.TLS_RSA_WITH_AES_256_CBC_SHA,
			tls.TLS_RSA_WITH_AES_128_CBC_SHA,
		},
	}
}
