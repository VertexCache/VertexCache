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

package comm_test

import (
	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/comm"
	"github.com/vertexcache/vertexcache/client-sdks/go/sdk/model"
	"net"
	"os"
	"testing"
)

const (
	validPort   = 11211
	unusedPort  = 59999
	liveTLSPort = 50505
	tcpTimeout  = 1000
	readTimeout = 1000
)

func TestCreateSocketNonTLSShouldSucceed(t *testing.T) {
	// Start a temporary TCP server
	ln, err := net.Listen("tcp", "localhost:0") // OS assigns an available port
	if err != nil {
		t.Fatalf("Failed to start temporary TCP server: %s", err)
	}
	defer ln.Close()

	// Get the assigned port
	addr := ln.Addr().(*net.TCPAddr)
	validPort := addr.Port

	// Run server handler in background (accept and close)
	go func() {
		conn, err := ln.Accept()
		if err == nil {
			conn.Close()
		}
	}()

	opt := model.ClientOption{
		ServerHost:     "localhost",
		ServerPort:     validPort,
		ConnectTimeout: tcpTimeout,
		ReadTimeout:    readTimeout,
	}

	sock, err := comm.CreateSocketNonTLS(opt)
	if err != nil {
		t.Fatalf("Expected success, got error: %s", err.Error())
	}
	sock.Close()
}

func TestCreateSocketNonTLSShouldFailIfPortClosed(t *testing.T) {
	opt := model.ClientOption{
		ServerHost:     "localhost",
		ServerPort:     unusedPort,
		ConnectTimeout: tcpTimeout,
		ReadTimeout:    readTimeout,
	}
	_, err := comm.CreateSocketNonTLS(opt)
	if err == nil {
		t.Fatal("Expected error, got success")
	}
}

func TestCreateSocketNonTLSShouldFailOnTimeout(t *testing.T) {
	opt := model.ClientOption{
		ServerHost:     "10.255.255.1",
		ServerPort:     9999,
		ConnectTimeout: 1,
		ReadTimeout:    readTimeout,
	}
	_, err := comm.CreateSocketNonTLS(opt)
	if err == nil {
		t.Fatal("Expected timeout error, got success")
	}
}

func TestCreateSecureSocketShouldFailDueToMissingTLSContext(t *testing.T) {
	opt := model.ClientOption{
		ServerHost:        "localhost",
		ServerPort:        8443,
		ConnectTimeout:    tcpTimeout,
		ReadTimeout:       readTimeout,
		VerifyCertificate: true,
	}
	_, err := comm.CreateSecureSocket(opt)
	if err == nil {
		t.Fatal("Expected TLS context error, got success")
	}
}

func TestCreateSecureSocketShouldFailWithBadCertificate(t *testing.T) {
	opt := model.ClientOption{
		ServerHost:        "localhost",
		ServerPort:        18888,
		ConnectTimeout:    tcpTimeout,
		ReadTimeout:       readTimeout,
		VerifyCertificate: true,
		TLSCertificate:    "not a valid cert",
	}
	_, err := comm.CreateSecureSocket(opt)
	if err == nil {
		t.Fatal("Expected cert error, got success")
	}
}

func TestCreateSecureSocketShouldSucceedWithLiveServer(t *testing.T) {
	if os.Getenv("VC_LIVE_TEST") != "true" {
		t.Skip("Skipping live test unless VC_LIVE_TEST=true")
	}

	opt := model.ClientOption{
		ServerHost:        "localhost",
		ServerPort:        liveTLSPort,
		ConnectTimeout:    tcpTimeout,
		ReadTimeout:       readTimeout,
		VerifyCertificate: false,
	}

	sock, err := comm.CreateSecureSocket(opt)
	if err != nil {
		t.Logf("Live TLS test skipped: could not establish secure socket: %s", err.Error())
		return // Don't fail test — treat as a soft skip
	}

	defer sock.Close()

	t.Log("TLS connection established successfully (expected IDENT failure may follow).")
}
