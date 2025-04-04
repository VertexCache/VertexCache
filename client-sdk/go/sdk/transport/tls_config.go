package transport

import (
	"crypto/tls"
	"crypto/x509"
	"fmt"
)

// LoadTLSConfig loads a TLS config using a cert and private key from files.
func LoadTLSConfig(certPath, keyPath string) (*tls.Config, error) {
	cert, err := tls.LoadX509KeyPair(certPath, keyPath)
	if err != nil {
		return nil, fmt.Errorf("failed to load cert/key: %w", err)
	}
	return &tls.Config{
		Certificates:       []tls.Certificate{cert},
		InsecureSkipVerify: false,
		MinVersion:         tls.VersionTLS12,
	}, nil
}

// LoadTLSConfigFromString loads a TLS config from embedded PEM content (cert+key).
func LoadTLSConfigFromString(certPEM string) (*tls.Config, error) {
	cert, err := tls.X509KeyPair([]byte(certPEM), []byte(certPEM))
	if err != nil {
		return nil, fmt.Errorf("failed to load TLS cert from string: %w", err)
	}
	return &tls.Config{
		Certificates:       []tls.Certificate{cert},
		InsecureSkipVerify: false,
		MinVersion:         tls.VersionTLS12,
		RootCAs:            x509.NewCertPool(),
	}, nil
}
