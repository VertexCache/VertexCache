package crypto

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"errors"
	"strings"
)

// NormalizePublicKey ensures the PEM format has headers and proper line breaks.
func NormalizePublicKey(key string) string {
	if strings.TrimSpace(key) == "" {
		return key
	}

	key = strings.TrimSpace(key)

	if strings.HasPrefix(key, "-----BEGIN PUBLIC KEY-----") {
		return key
	}

	// Remove rogue headers and line breaks
	key = strings.ReplaceAll(key, "-----BEGIN PUBLIC KEY-----", "")
	key = strings.ReplaceAll(key, "-----END PUBLIC KEY-----", "")
	key = strings.ReplaceAll(key, "\n", "")
	key = strings.ReplaceAll(key, "\r", "")

	// Re-wrap to 64-char lines
	var sb strings.Builder
	sb.WriteString("-----BEGIN PUBLIC KEY-----\n")
	for i := 0; i < len(key); i += 64 {
		end := i + 64
		if end > len(key) {
			end = len(key)
		}
		sb.WriteString(key[i:end] + "\n")
	}
	sb.WriteString("-----END PUBLIC KEY-----")
	return sb.String()
}

// Encrypt encrypts the string with the provided PEM public key using RSA PKCS#1 v1.5.
// Returns Base64-encoded ciphertext.
func Encrypt(data string, publicKeyPem string) (string, error) {
	if strings.TrimSpace(data) == "" {
		return "", errors.New("cannot encrypt null or empty data")
	}
	if strings.TrimSpace(publicKeyPem) == "" {
		return "", errors.New("public key is missing")
	}

	dataBytes := []byte(data)
	if len(dataBytes) > 245 {
		return "", errors.New("data too long to encrypt with 2048-bit RSA (max 245 bytes)")
	}

	block, _ := pem.Decode([]byte(publicKeyPem))
	if block == nil || block.Type != "PUBLIC KEY" {
		return "", errors.New("invalid PEM public key")
	}

	pubKey, err := x509.ParsePKIXPublicKey(block.Bytes)
	if err != nil {
		return "", errors.New("failed to parse public key: " + err.Error())
	}

	rsaPubKey, ok := pubKey.(*rsa.PublicKey)
	if !ok {
		return "", errors.New("parsed key is not an RSA public key")
	}

	encryptedBytes, err := rsa.EncryptPKCS1v15(rand.Reader, rsaPubKey, dataBytes)
	if err != nil {
		return "", err
	}

	return base64.StdEncoding.EncodeToString(encryptedBytes), nil
}
