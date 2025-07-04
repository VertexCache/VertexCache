// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
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
	"fmt"
	"net"
	"time"

	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/model"
)

func CreateSecureSocket(opt model.ClientOption) (net.Conn, error) {
	var tlsConfig *tls.Config
	var err error

	if opt.VerifyCertificate {
		fmt.Println("TLS mode: VERIFY")
		if opt.TLSCertificate == "" {
			fmt.Println("Missing TLSCertificate with Verify=true")
			return nil, model.NewVertexCacheSdkException("TLS verification enabled but certificate is missing")
		}
		tlsConfig, err = CreateVerifiedSocketFactory(opt.TLSCertificate, opt.ServerHost)
		if err != nil {
			fmt.Printf("CreateVerifiedSocketFactory failed: %v\n", err)
			return nil, model.NewVertexCacheSdkException("Failed to create Secure Socket")
		}
	} else {
		fmt.Println("TLS mode: INSECURE (skip verify)")
		tlsConfig = CreateInsecureSocketFactory(opt.ServerHost)
	}

	addr := fmt.Sprintf("%s:%d", opt.ServerHost, opt.ServerPort)
	fmt.Printf("Dialing TLS socket to %s\n", addr)
	conn, err := tls.DialWithDialer(&net.Dialer{
		Timeout: time.Duration(opt.ConnectTimeout) * time.Millisecond,
	}, "tcp", addr, tlsConfig)

	if err != nil {
		fmt.Printf("tls.DialWithDialer failed: %v\n", err)
		return nil, model.NewVertexCacheSdkException("Failed to create Secure Socket")
	}

	fmt.Println("TLS connection succeeded")
	return conn, nil
}

func CreateSocketNonTLS(opt model.ClientOption) (net.Conn, error) {
	addr := fmt.Sprintf("%s:%d", opt.ServerHost, opt.ServerPort)
	conn, err := net.DialTimeout("tcp", addr, time.Duration(opt.ConnectTimeout)*time.Millisecond)
	if err != nil {
		return nil, model.NewVertexCacheSdkException("Failed to create Non Secure Socket")
	}

	// Set read timeout (like socket.setSoTimeout in Java)
	err = conn.SetReadDeadline(time.Now().Add(time.Duration(opt.ReadTimeout) * time.Millisecond))
	if err != nil {
		conn.Close()
		return nil, model.NewVertexCacheSdkException("Failed to create Non Secure Socket")
	}

	return conn, nil
}
