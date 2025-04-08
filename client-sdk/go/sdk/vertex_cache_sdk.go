package sdk

import (
	"context"
	"crypto/tls"
	"encoding/base64"
	"fmt"
	"strings"
	"vertexcache/sdk/core"
	"vertexcache/sdk/crypto"
	"vertexcache/sdk/protocol"
	"vertexcache/sdk/results"
	"vertexcache/sdk/transport"
)

type VertexCacheSdk struct {
	Options *core.VertexCacheSdkOptions
}

func NewVertexCacheSdk(options *core.VertexCacheSdkOptions) *VertexCacheSdk {
	return &VertexCacheSdk{Options: options}
}

//
// ✅ Simple public helper methods
//

func (sdk *VertexCacheSdk) Ping(ctx context.Context) *results.VCacheResult {
	return sdk.runCommand(ctx, "PING", nil)
}

func (sdk *VertexCacheSdk) Set(ctx context.Context, key, value string) *results.VCacheResult {
	return sdk.runCommand(ctx, "SET", []string{key, value})
}

func (sdk *VertexCacheSdk) Get(ctx context.Context, key string) *results.VCacheResult {
	return sdk.runCommand(ctx, "GET", []string{key})
}

func (sdk *VertexCacheSdk) Del(ctx context.Context, key string) *results.VCacheResult {
	return sdk.runCommand(ctx, "DEL", []string{key})
}

//
// 🔒 Internal engine (was RunCommand)
//

func (sdk *VertexCacheSdk) runCommand(ctx context.Context, command string, args []string) *results.VCacheResult {
	if strings.TrimSpace(command) == "" {
		return results.Failure(results.InvalidCommand, "Command cannot be empty.")
	}

	rawCommand := protocol.FormatCommand(command, args)

	if sdk.Options.EnableEncryption {
		if sdk.Options.PublicKey == "" {
			return results.Failure(results.EncryptionError, "Missing public key for encryption.")
		}

		byteLen := len([]byte(rawCommand))
		if byteLen > 245 {
			return results.Failure(results.EncryptionError, fmt.Sprintf("Message too long for RSA: %d bytes", byteLen))
		}

		normalizedKey := crypto.NormalizePublicKey(sdk.Options.PublicKey)
		encrypted, err := crypto.Encrypt(rawCommand, normalizedKey)
		if err != nil {
			return results.Failure(results.EncryptionError, "Failed to encrypt command: "+err.Error())
		}
		rawCommand = encrypted
	}

	var lastErr error

	for attempt := 0; attempt <= sdk.Options.MaxRetries; attempt++ {
		address := fmt.Sprintf("%s:%d", sdk.Options.ServerHost, sdk.Options.ServerPort)
		var conn transport.VCacheConn
		var err error

		if sdk.Options.EnableEncryptionTransport {
			tlsConfig := &tls.Config{
				ServerName:         sdk.Options.ServerHost,
				InsecureSkipVerify: !sdk.Options.EnableVerifyCertificate,
			}
			if sdk.Options.EnableVerifyCertificate && sdk.Options.CertificatePem != "" {
				cert, err := crypto.LoadCertificateFromString(sdk.Options.CertificatePem)
				if err == nil {
					tlsConfig.RootCAs = crypto.CreateCertPool(cert)
				}
			}
			conn, err = transport.NewTLSConnection(address, tlsConfig)
		} else {
			conn, err = transport.NewTCPConnection(address)
		}

		if err != nil {
			lastErr = err
			continue
		}

		defer conn.Dispose()

		if sdk.Options.EnableEncryption {
			decoded, err := base64.StdEncoding.DecodeString(rawCommand)
			if err != nil {
				return results.Failure(results.EncryptionError, "Failed to decode encrypted command: "+err.Error())
			}
			_, err = conn.SendRaw(decoded)
			if err != nil {
				lastErr = err
				continue
			}
		} else {
			_, err = conn.Send(rawCommand)
			if err != nil {
				lastErr = err
				continue
			}
		}

		line, err := conn.ReadLine()
		if err != nil {
			lastErr = err
			continue
		}

		return protocol.Parse(line)
	}

	if lastErr != nil {
		return results.Failure(results.NetworkFailure, lastErr.Error())
	}

	return results.Failure(results.Unknown, "Unexpected error")
}
