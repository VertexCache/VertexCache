package com.vertexcache.sdk.comm;

import com.vertexcache.sdk.model.VertexCacheSdkException;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSocketFactory;

import static org.junit.jupiter.api.Assertions.*;

class SSLHelperTest {

    // Test PEM Do NOT use for Real Use
    private static final String VALID_PEM_CERT = """
        -----BEGIN CERTIFICATE-----
        MIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV
        BAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x
        EDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv
        Y2FsaG9zdDAeFw0yNTA1MTgwMzU2NDdaFw0zNTA1MTYwMzU2NDdaMG4xEDAOBgNV
        BAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x
        EDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv
        Y2FsaG9zdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMHVT5HdQkUj
        Ia3sYmLQUeOja7tKtAXi1cuhCLlrvgS2DKJa9cpkgi1dsKOjJmsTqo580e+jrpdQ
        J+mTybdKoG6CZWEqfMizut48aTQoBteiLFSZ9J2/6nCXhxugA+aQ94lhkj3lJIHf
        lIZeIYHaPNXH9/K4oCODJ8P6MfeQjY1ZWbrcQ9PxHQhWV/60AfTuJRJ4T/HQmOqM
        6IcYz2t7iviIYvQq37A+wr1ClgxlfuT6JScEA8J34GivskB2p/MEn8E8y/durORz
        aaF5RBpnsc+fzVwQuvkth993rnDemdrcvTF1bdF5t88Zt5FiPD4qDF+pKloHNMRQ
        DXBYb9Wf/t8CAwEAAaMhMB8wHQYDVR0OBBYEFOYQaTvkoqgLjRhCYBMrwLqrVfJo
        MA0GCSqGSIb3DQEBDAUAA4IBAQCVBHT1uqtm72g085JuWdjBoBDa6bJD3Wj3L+GH
        JaKOF26wQmXtLV0KraH3t3SUxWOM865OcbOkIiSUjMIgqmmFh1quoF4NMBa0wye8
        JguLk6Qpffd+YXfzddxi33jdCUWgyqcTKq7bfB5DbMP4U5yVxnlXwKB0dxkaEFSx
        iAUrhcZ1+iYjelrERk8MPj9FQIzQ8FwwF4oB8ShNDhDNWCOVbSdLXwMOLH84u/ul
        v/I4U/5/mqGGTtwNyyzFS0GYgrYua4H7Aqer2g4wv8PUYwkaAfQ49CWm9kFQxgD4
        qwwA44GZv7zAa89WHNpbIMAA8keexZkPzJBIQNSKy2d9dhcP
        -----END CERTIFICATE-----
        """;

    private static final String INVALID_PEM_CERT = "-----BEGIN CERTIFICATE-----\\nINVALID DATA\\n-----END CERTIFICATE-----";

    @Test
    void createVerifiedSocketFactory_shouldSucceedWithValidCert() {
        assertDoesNotThrow(() -> {
            SSLSocketFactory factory = SSLHelper.createVerifiedSocketFactory(VALID_PEM_CERT);
            assertNotNull(factory);
        });
    }

    @Test
    void createVerifiedSocketFactory_shouldFailWithInvalidCert() {
        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, () -> {
            SSLHelper.createVerifiedSocketFactory(INVALID_PEM_CERT);
        });
        assertEquals("Failed to create secure socket connection", ex.getMessage());
    }

    @Test
    void createInsecureSocketFactory_shouldSucceed() {
        assertDoesNotThrow(() -> {
            SSLSocketFactory factory = SSLHelper.createInsecureSocketFactory();
            assertNotNull(factory);
        });
    }
}
