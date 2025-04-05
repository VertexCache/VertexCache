package transport

import (
	"crypto/tls"
	"crypto/x509"
	"fmt"
)

// LoadTLSConfigFromString trusts the server's cert and constructs a compatible TLS config.
func LoadTLSConfigFromString(serverCertPEM string, serverName string, verify bool) (*tls.Config, error) {
	rootCAs := x509.NewCertPool()
	ok := rootCAs.AppendCertsFromPEM([]byte(serverCertPEM))
	if !ok {
		return nil, fmt.Errorf("failed to parse trusted server certificate")
	}

	fmt.Println("🛡️ TLS config constructed:")
	fmt.Printf("   ServerName: %s\n", serverName)
	fmt.Printf("   InsecureSkipVerify: %v\n", !verify)

	return &tls.Config{
		ServerName:             serverName,
		MinVersion:             tls.VersionTLS12,
		InsecureSkipVerify:     !verify,
		RootCAs:                rootCAs,
		SessionTicketsDisabled: true,
		NextProtos:             []string{},
	}, nil
}
