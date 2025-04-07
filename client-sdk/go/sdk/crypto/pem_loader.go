package crypto

import (
	"crypto/rsa"
	"crypto/tls"
	"crypto/x509"
	"encoding/pem"
	"errors"
)

// LoadRSAPrivateKeyFromString parses an RSA private key from PEM string.
func LoadRSAPrivateKeyFromString(pemStr string) (*rsa.PrivateKey, error) {
	block, _ := pem.Decode([]byte(pemStr))
	if block == nil || block.Type != "RSA PRIVATE KEY" {
		return nil, errors.New("invalid RSA private key format")
	}
	return x509.ParsePKCS1PrivateKey(block.Bytes)
}

// LoadRSAPublicKeyFromString parses an RSA public key from PEM string.
func LoadRSAPublicKeyFromString(pemStr string) (*rsa.PublicKey, error) {
	block, _ := pem.Decode([]byte(pemStr))
	if block == nil || block.Type != "PUBLIC KEY" {
		return nil, errors.New("invalid RSA public key format")
	}
	pubInterface, err := x509.ParsePKIXPublicKey(block.Bytes)
	if err != nil {
		return nil, err
	}
	pub, ok := pubInterface.(*rsa.PublicKey)
	if !ok {
		return nil, errors.New("not an RSA public key")
	}
	return pub, nil
}

// LoadTLSCertificateFromString loads a tls.Certificate from PEM key + cert.
func LoadTLSCertificateFromString(certPEM string, keyPEM string) (tls.Certificate, error) {
	return tls.X509KeyPair([]byte(certPEM), []byte(keyPEM))
}

// LoadCertificateFromString parses a PEM-encoded TLS certificate and returns an x509.Certificate.
func LoadCertificateFromString(pemStr string) (*x509.Certificate, error) {
	block, _ := pem.Decode([]byte(pemStr))
	if block == nil || block.Type != "CERTIFICATE" {
		return nil, errors.New("invalid PEM certificate format")
	}
	return x509.ParseCertificate(block.Bytes)
}

// CreateCertPool creates an x509.CertPool containing the given cert, to be used in tls.Config.RootCAs.
func CreateCertPool(cert *x509.Certificate) *x509.CertPool {
	pool := x509.NewCertPool()
	pool.AddCert(cert)
	return pool
}
