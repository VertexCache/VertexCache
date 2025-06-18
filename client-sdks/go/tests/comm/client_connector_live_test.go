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
	"strings"
	"testing"

	"github.com/vertexcache/client-sdks/go/sdk/comm"
	"github.com/vertexcache/client-sdks/go/sdk/model"
)

const (
	host          = "127.0.0.1"
	port          = 50505
	clientID      = "sdk-client-go"
	clientToken   = "6612f3e2-c7ef-4d7f-a0d6-eb665af84f0c"
	testPublicKey = `-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
EwIDAQAB
-----END PUBLIC KEY-----`

	tlsCert = `-----BEGIN CERTIFICATE-----\nMIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV\nBAYTB1...`
)

func TestClientConnector_LiveConnectAndPing(t *testing.T) {
	//if os.Getenv("VC_LIVE_TEST") != "true" {
	//	t.Skip("Skipping live test unless VC_LIVE_TEST=true")
	//}

	opt := model.NewClientOption()
	opt.ClientID = clientID
	opt.ClientToken = clientToken
	opt.ServerHost = host
	opt.ServerPort = port
	opt.EnableTLSEncryption = true
	opt.VerifyCertificate = false // Set to true if TLS cert is trusted
	opt.TLSCertificate = tlsCert
	opt.EncryptionMode = model.Asymmetric
	opt.PublicKey = testPublicKey

	client := comm.NewClientConnector(opt)
	err := client.Connect()
	if err != nil {
		t.Fatalf("Connect() failed: %v", err)
	}

	if !client.IsConnected() {
		t.Fatal("Expected client to be connected after Connect()")
	}

	reply, err := client.Send("PING")
	if err != nil {
		t.Fatalf("Send() failed: %v", err)
	}
	if !strings.HasPrefix(reply, "+PONG") {
		t.Errorf("Expected reply to start with +PONG, got: %s", reply)
	}

	client.Close()
	if client.IsConnected() {
		t.Error("Expected IsConnected() to be false after Close()")
	}
}
