// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
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
	"bufio"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/pem"
	"errors"
	"net"
	"strings"
	"sync"

	"github.com/vertexcache/client-sdks/go/sdk/model"
)

type ClientConnector struct {
	writer    *bufio.Writer
	reader    *bufio.Reader
	conn      net.Conn
	options   *model.ClientOption
	connected bool
	lock      sync.Mutex
}

func NewClientConnector(opt *model.ClientOption) *ClientConnector {
	return &ClientConnector{
		options: opt,
	}
}

func (cc *ClientConnector) Connect() error {
	var err error
	if cc.options.EnableTLSEncryption {
		cc.conn, err = CreateSecureSocket(*cc.options)
	} else {
		cc.conn, err = CreateSocketNonTLS(*cc.options)
	}
	if err != nil {
		return model.NewVertexCacheSdkException("Connection failed: " + err.Error())
	}

	cc.writer = bufio.NewWriter(cc.conn)
	cc.reader = bufio.NewReader(cc.conn)

	identCmd := cc.options.BuildIdentCommand()
	encrypted, err := cc.encryptIfEnabled([]byte(identCmd))
	if err != nil {
		return model.NewVertexCacheSdkException("Failed to encrypt IDENT: " + err.Error())
	}

	err = WriteFramedMessage(cc.writer, encrypted)
	if err != nil {
		return model.NewVertexCacheSdkException("Failed to send IDENT: " + err.Error())
	}
	cc.writer.Flush()

	resp, err := ReadFramedMessage(cc.reader)
	if err != nil {
		return model.NewVertexCacheSdkException("Failed to read IDENT response: " + err.Error())
	}

	respStr := ""
	if resp != nil {
		respStr = strings.TrimSpace(string(resp))
	}

	if !strings.HasPrefix(respStr, "+OK") {
		return model.NewVertexCacheSdkException("Authorization failed: " + respStr)
	}

	cc.connected = true
	return nil
}

func (cc *ClientConnector) Send(message string) (string, error) {
	cc.lock.Lock()
	defer cc.lock.Unlock()

	if !cc.connected {
		return "", model.NewVertexCacheSdkException("Client not connected")
	}

	encrypted, err := cc.encryptIfEnabled([]byte(message))
	if err != nil {
		return "", model.NewVertexCacheSdkException("Encryption failed: " + err.Error())
	}

	err = WriteFramedMessage(cc.writer, encrypted)
	if err != nil {
		return "", model.NewVertexCacheSdkException("Failed to send message: " + err.Error())
	}
	cc.writer.Flush()

	resp, err := ReadFramedMessage(cc.reader)
	if err != nil || resp == nil {
		return "", model.NewVertexCacheSdkException("Failed to read response")
	}

	return string(resp), nil
}

func (cc *ClientConnector) encryptIfEnabled(plain []byte) ([]byte, error) {
	switch cc.options.EncryptionMode {
	case model.Asymmetric:
		SwitchToAsymmetric()
		block, _ := pem.Decode([]byte(cc.options.PublicKey))
		if block == nil {
			return nil, errors.New("Invalid PEM format for public key")
		}
		pub, err := x509.ParsePKIXPublicKey(block.Bytes)
		if err != nil {
			return nil, err
		}
		key, ok := pub.(*rsa.PublicKey)
		if !ok {
			return nil, errors.New("Not a valid RSA public key")
		}
		return rsa.EncryptPKCS1v15(rand.Reader, key, plain)

	case model.Symmetric:
		SwitchToSymmetric()
		keyBytes, err := ConfigSharedKeyIfEnabled(cc.options.SharedEncryptionKey)
		if err != nil {
			return nil, err
		}
		return Encrypt(plain, keyBytes)

	case model.None:
		fallthrough
	default:
		return plain, nil
	}
}

func (cc *ClientConnector) IsConnected() bool {
	return cc.connected && cc.conn != nil
}

func (cc *ClientConnector) Close() {
	if cc.conn != nil {
		cc.conn.Close()
	}
	cc.connected = false
}
