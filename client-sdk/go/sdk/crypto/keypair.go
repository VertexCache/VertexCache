package crypto

import (
	"crypto/rsa"
	"crypto/x509"
	"encoding/pem"
	"errors"
	"fmt"
	"io/ioutil"
	"os"
	"strings"
)

// LoadRSAPrivateKeyFromFile loads a private key from a PEM file.
func LoadRSAPrivateKeyFromFile(path string) (*rsa.PrivateKey, error) {
	data, err := ioutil.ReadFile(path)
	if err != nil {
		return nil, err
	}
	return parseRSAPrivateKey(data)
}

// LoadRSAPublicKeyFromFile loads a public key from a PEM file.
func LoadRSAPublicKeyFromFile(path string) (*rsa.PublicKey, error) {
	data, err := ioutil.ReadFile(path)
	if err != nil {
		return nil, err
	}
	return parseRSAPublicKey(data)
}

// LoadRSAPrivateKeyFromEnv loads a private key from env (file path, single-line PEM, or multiline).
func LoadRSAPrivateKeyFromEnv(value string) (*rsa.PrivateKey, error) {
	if isFilePath(value) {
		return LoadRSAPrivateKeyFromFile(value)
	}
	return parseRSAPrivateKey(normalizeMultiline(value))
}

// LoadRSAPublicKeyFrom
