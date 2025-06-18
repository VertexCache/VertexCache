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
// ------------------------------------------------------------------------------

package tests

import (
	"strings"
	"testing"

	"github.com/vertexcache/client-sdks/go/sdk"
	"github.com/vertexcache/client-sdks/go/sdk/model"
)

const (
	clientID    = "sdk-client-go"
	clientToken = "6612f3e2-c7ef-4d7f-a0d6-eb665af84f0c"
	host        = "127.0.0.1"
	port        = 50505
	tlsCert     = "-----BEGIN CERTIFICATE-----\\nMIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV..."
	testPubKey  = `-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
EwIDAQAB
-----END PUBLIC KEY-----`
)

func setupSDK(t *testing.T) *sdk.VertexCacheSDK {
	opt := model.NewClientOption()
	opt.ClientID = clientID
	opt.ClientToken = clientToken
	opt.ServerHost = host
	opt.ServerPort = port
	opt.EnableTLSEncryption = true
	opt.VerifyCertificate = false
	opt.TLSCertificate = tlsCert
	opt.EncryptionMode = model.Asymmetric
	opt.PublicKey = testPubKey

	sdkClient := sdk.NewVertexCacheSDK(opt)
	if err := sdkClient.OpenConnection(); err != nil {
		t.Fatalf("OpenConnection() failed: %v", err)
	}
	return sdkClient
}

func teardownSDK(client *sdk.VertexCacheSDK) {
	client.Close()
}

func Test01_PingShouldSucceed(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	res := client.Ping()
	if !res.IsSuccess() {
		t.Fatalf("Ping failed: %v", res.Message())
	}
	if !strings.HasPrefix(res.Message(), "PONG") {
		t.Errorf("Expected PONG response, got: %s", res.Message())
	}
}

func Test02_SetAndGetShouldSucceed(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	key := "go-unit-key"
	value := "unit-test-value"

	setResult := client.Set(key, value)
	if !setResult.IsSuccess() {
		t.Fatalf("Set failed: %s", setResult.Message())
	}

	getResult := client.Get(key)
	if !getResult.IsSuccess() {
		t.Fatalf("Get failed: %s", getResult.Message())
	}
	if getResult.Value() != value {
		t.Errorf("Expected value %s, got %s", value, getResult.Value())
	}
}

/*
func Test03_DelShouldSucceed(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	key := "go-unit-del-key"
	value := "delete-this"

	setResult := client.Set(key, value)
	if !setResult.IsSuccess() {
		t.Fatalf("Set before Del failed: %s", setResult.Message())
	}

	delResult := client.Del(key)
	if !delResult.IsSuccess() {
		t.Fatalf("Del failed: %s", delResult.Message())
	}

	getResult := client.Get(key)
	if getResult.IsSuccess() {
		t.Errorf("Expected deleted key to be missing, got value: %s", getResult.Value())
	}
}
*/
