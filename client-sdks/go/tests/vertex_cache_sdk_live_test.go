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
	"github.com/vertexcache/client-sdks/go/sdk/comm"
	"os"
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

func TestMain(m *testing.M) {
	if os.Getenv("VC_LIVE_TLS_ASYMMETRIC_TEST") != "true" {
		println("Skipping all live tests. Set VC_LIVE_TLS_ASYMMETRIC_TEST=true to run them.")
		os.Exit(0)
	}
	os.Exit(m.Run())
}

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
	if !getResult.IsSuccess() {
		t.Errorf("Expected GET to succeed for deleted key, got error: %s", getResult.Message())
	}
	if getResult.Value() != "" {
		t.Errorf("Expected deleted key to return empty value, got: %s", getResult.Value())
	}
}

func Test04_GetShouldReturnPreviouslySetValue(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	key := "go-unit-key"
	value := "value-123"

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

func Test05_GetOnMissingKeyShouldSucceedButReturnNil(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	key := "go-nonexistent-key"

	getResult := client.Get(key)
	if !getResult.IsSuccess() {
		t.Fatalf("Expected GET to succeed even on nonexistent key: %s", getResult.Message())
	}
	if getResult.Value() != "" {
		t.Errorf("Expected nil/empty value, got: %s", getResult.Value())
	}
}

func Test06_SetWithSecondaryIndexShouldSucceed(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	key := "go-secondary-key"
	value := "value-xyz"
	idx1 := "go-secondary-idx"

	result := client.SetWithSecondary(key, value, idx1)
	if !result.IsSuccess() {
		t.Fatalf("SetWithSecondary failed: %s", result.Message())
	}
	if result.Message() != "OK" {
		t.Errorf("Expected OK response, got: %s", result.Message())
	}
}

func Test07_SetWithSecondaryAndTertiaryIndexShouldSucceed(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	key := "go-key-with-both-indexes"
	value := "value-abc"
	idx1 := "go-idx1"
	idx2 := "go-idx2"

	result := client.SetWithSecondaryAndTertiary(key, value, idx1, idx2)
	if !result.IsSuccess() {
		t.Fatalf("SetWithSecondaryAndTertiary failed: %s", result.Message())
	}
	if result.Message() != "OK" {
		t.Errorf("Expected OK response, got: %s", result.Message())
	}
}

func Test08_GetBySecondaryIndexShouldReturnPreviouslySetValue(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	key := "go-secondary-index-key"
	value := "value-456"
	idx1 := "go-idx1-lookup"

	setResult := client.SetWithSecondary(key, value, idx1)
	if !setResult.IsSuccess() {
		t.Fatalf("SetWithSecondary failed: %s", setResult.Message())
	}

	getResult := client.GetBySecondaryIndex(idx1)
	if !getResult.IsSuccess() {
		t.Fatalf("GetBySecondaryIndex failed: %s", getResult.Message())
	}
	if getResult.Value() != value {
		t.Errorf("Expected value %s, got: %s", value, getResult.Value())
	}
}

func Test09_GetByTertiaryIndexShouldReturnPreviouslySetValue(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	key := "go-tertiary-index-key"
	value := "value-789"
	idx1 := "go-idx1-tertiary"
	idx2 := "go-idx2-lookup"

	setResult := client.SetWithSecondaryAndTertiary(key, value, idx1, idx2)
	if !setResult.IsSuccess() {
		t.Fatalf("SetWithSecondaryAndTertiary failed: %s", setResult.Message())
	}

	getResult := client.GetByTertiaryIndex(idx2)
	if !getResult.IsSuccess() {
		t.Fatalf("GetByTertiaryIndex failed: %s", getResult.Message())
	}
	if getResult.Value() != value {
		t.Errorf("Expected value %s, got: %s", value, getResult.Value())
	}
}

func Test10_MultibyteKeyAndValueShouldSucceed(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	key := "键🔑値🌟"
	value := "测试🧪データ💾"

	setResult := client.Set(key, value)
	if !setResult.IsSuccess() {
		t.Fatalf("Set with multibyte key/value failed: %s", setResult.Message())
	}

	getResult := client.Get(key)
	if !getResult.IsSuccess() {
		t.Fatalf("Get with multibyte key failed: %s", getResult.Message())
	}
	if getResult.Value() != value {
		t.Errorf("Expected multibyte value %s, got: %s", value, getResult.Value())
	}
}

func Test11_FailedHostShouldError(t *testing.T) {
	badOption := model.NewClientOption()
	badOption.ClientID = clientID
	badOption.ClientToken = clientToken
	badOption.ServerHost = "bad-host" // Invalid host
	badOption.ServerPort = port
	badOption.EnableTLSEncryption = true
	badOption.VerifyCertificate = false
	badOption.TLSCertificate = tlsCert
	badOption.EncryptionMode = model.Asymmetric
	badOption.PublicKey = testPubKey

	client := comm.NewClientConnector(badOption)
	err := client.Connect()
	if err == nil {
		t.Fatal("Expected connection error due to bad host, but got none")
	}
}

func Test13_FailedSecureTLSShouldError(t *testing.T) {
	opt := model.NewClientOption()
	opt.ClientID = clientID
	opt.ClientToken = clientToken
	opt.ServerHost = host // or serverHost — make sure this matches your test constants
	opt.ServerPort = port
	opt.EnableTLSEncryption = true
	opt.VerifyCertificate = true // Intentionally expecting to fail verification
	opt.TLSCertificate = tlsCert
	opt.EncryptionMode = model.Asymmetric
	opt.PublicKey = testPubKey

	client := sdk.NewVertexCacheSDK(opt)
	err := client.OpenConnection()
	if err == nil {
		defer client.Close()
		t.Fatal("Expected TLS verification failure but got nil error")
	}
	if !strings.Contains(strings.ToLower(err.Error()), "failed to parse pem certificate") &&
		!strings.Contains(strings.ToLower(err.Error()), "secure socket") {
		t.Errorf("Unexpected TLS error message: %s", err.Error())
	}
}

func Test14_NonSecureTLSShouldSucceed(t *testing.T) {
	opt := model.NewClientOption()
	opt.ClientID = clientID
	opt.ClientToken = clientToken
	opt.ServerHost = host
	opt.ServerPort = port
	opt.EnableTLSEncryption = true
	opt.VerifyCertificate = false // skip verification
	opt.TLSCertificate = ""       // intentionally blank
	opt.EncryptionMode = model.Asymmetric
	opt.PublicKey = testPubKey

	client := sdk.NewVertexCacheSDK(opt)
	err := client.OpenConnection()
	if err != nil {
		t.Fatalf("Expected connection to succeed without certificate verification, got: %v", err)
	}

	if !client.IsConnected() {
		t.Error("Expected client to be connected")
	}

	client.Close()
}

func Test15_InvalidPublicKeyShouldError(t *testing.T) {
	opt := model.NewClientOption()
	opt.ClientID = clientID
	opt.ClientToken = clientToken
	opt.ServerHost = host
	opt.ServerPort = port
	opt.EnableTLSEncryption = true
	opt.VerifyCertificate = false
	opt.TLSCertificate = tlsCert
	opt.EncryptionMode = model.Asymmetric
	opt.PublicKey = testPubKey + "_BAD" // Corrupt PEM block

	client := sdk.NewVertexCacheSDK(opt)
	err := client.OpenConnection()
	if err == nil {
		t.Fatal("Expected error due to invalid public key but got nil")
	}
	if !strings.Contains(err.Error(), "Invalid PEM format for public key") &&
		!strings.Contains(err.Error(), "Failed to encrypt IDENT") {
		t.Errorf("Unexpected error: %s", err.Error())
	}
}

func Test16_InvalidSharedKeyShouldError(t *testing.T) {
	opt := model.NewClientOption()
	opt.ClientID = clientID
	opt.ClientToken = clientToken
	opt.ServerHost = host
	opt.ServerPort = port
	opt.EnableTLSEncryption = true
	opt.VerifyCertificate = false
	opt.TLSCertificate = tlsCert
	opt.EncryptionMode = model.Symmetric
	opt.SharedEncryptionKey = "_BAD_SHARED_KEY" // Not base64

	client := sdk.NewVertexCacheSDK(opt)
	err := client.OpenConnection()
	if err == nil {
		t.Fatal("Expected error due to invalid shared key but got nil")
	}
	if !strings.Contains(err.Error(), "Invalid shared key") &&
		!strings.Contains(err.Error(), "Failed to decode shared key") {
		t.Errorf("Unexpected error: %s", err.Error())
	}
}

func Test17_SetWithEmptyKeyShouldFail(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	res := client.Set("", "some-value")
	if res.IsSuccess() {
		t.Fatal("Expected failure when setting with empty key")
	}
	if !strings.Contains(res.Message(), "Missing Primary Key") {
		t.Errorf("Unexpected error message: %s", res.Message())
	}
}

func Test18_SetWithEmptyValueShouldFail(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	res := client.Set("non-empty-key", "")
	if res.IsSuccess() {
		t.Fatal("Expected failure when setting with empty value")
	}
	if !strings.Contains(res.Message(), "Missing Value") {
		t.Errorf("Unexpected error message: %s", res.Message())
	}
}

func Test19_SetWithNilKeyShouldFail(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	var key string = ""
	res := client.Set(key, "some-value")
	if res.IsSuccess() {
		t.Fatal("Expected failure when setting with nil/empty key")
	}
	if !strings.Contains(res.Message(), "Missing Primary Key") {
		t.Errorf("Unexpected error message: %s", res.Message())
	}
}

func Test20_SetWithNilValueShouldFail(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	var value string = ""
	res := client.Set("nil-value-key", value)
	if res.IsSuccess() {
		t.Fatal("Expected failure when setting with nil/empty value")
	}
	if !strings.Contains(res.Message(), "Missing Value") {
		t.Errorf("Unexpected error message: %s", res.Message())
	}
}

func Test21_SetWithEmptySecondaryIndexShouldFail(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	idx1 := ""
	res := client.SetWithSecondary("key", "value", idx1)
	if res.IsSuccess() {
		t.Fatal("Expected failure when setting with empty secondary index")
	}
	if !strings.Contains(res.Message(), "Secondary key can't be empty") {
		t.Errorf("Unexpected error message: %s", res.Message())
	}
}

func Test22_SetWithEmptyTertiaryIndexShouldFail(t *testing.T) {
	client := setupSDK(t)
	defer teardownSDK(client)

	idx1 := "valid-secondary"
	idx2 := ""

	res := client.SetWithSecondaryAndTertiary("key", "value", idx1, idx2)
	if res.IsSuccess() {
		t.Fatal("Expected failure when setting with empty tertiary index")
	}
	if !strings.Contains(res.Message(), "Tertiary key can't be empty") {
		t.Errorf("Unexpected error message: %s", res.Message())
	}
}
