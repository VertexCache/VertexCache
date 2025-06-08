package com.vertexcache.sdk.comm;

import com.vertexcache.sdk.model.ClientOption;
import com.vertexcache.sdk.model.VertexCacheSdkException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class SocketHelperTest {

    private static final boolean ENABLE_LIVE_TLS_TESTS = true;

    private static final int MOCK_PORT = 18888;
    private static final int UNUSED_PORT = 65534; // adjust if in use
    private static Thread mockServerThread;
    private static volatile boolean serverRunning;

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

    @BeforeAll
    static void startMockServer() {
        serverRunning = true;
        mockServerThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(MOCK_PORT)) {
                while (serverRunning) {
                    try {
                        Socket socket = serverSocket.accept();
                        socket.close(); // immediately close after accept
                    } catch (IOException ignored) {}
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        mockServerThread.setDaemon(true);
        mockServerThread.start();
    }

    @AfterAll
    static void stopMockServer() {
        serverRunning = false;
        try (Socket socket = new Socket("localhost", MOCK_PORT)) {
            // nudge to unblock accept()
        } catch (IOException ignored) {}
        try {
            mockServerThread.join();
        } catch (InterruptedException ignored) {}
    }

    @Test
    void createNonSecureSocket_shouldSucceed() {
        ClientOption option = new ClientOption();
        option.setServerHost("localhost");
        option.setServerPort(MOCK_PORT);
        option.setConnectTimeout(1000);
        option.setReadTimeout(1000);

        assertDoesNotThrow(() -> {
            Socket socket = SocketHelper.createSocketNonTLS(option);
            assertNotNull(socket);
            assertTrue(socket.isConnected());
            socket.close();
        });
    }

    @Test
    void createNonSecureSocket_shouldFailIfPortClosed() {
        ClientOption option = new ClientOption();
        option.setServerHost("localhost");
        option.setServerPort(UNUSED_PORT);
        option.setConnectTimeout(500);
        option.setReadTimeout(500);

        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, () -> {
            SocketHelper.createSocketNonTLS(option);
        });
        assertEquals("Failed to create Non Secure Socket", ex.getMessage());
    }

    @Test
    void createNonSecureSocket_shouldFailOnTimeout() {
        ClientOption option = new ClientOption();
        option.setServerHost("10.255.255.1"); // blackhole IP for timeout testing
        option.setServerPort(12345);
        option.setConnectTimeout(300); // short timeout
        option.setReadTimeout(500);

        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, () -> {
            SocketHelper.createSocketNonTLS(option);
        });
        assertEquals("Failed to create Non Secure Socket", ex.getMessage());
    }

    @Test
    void createSecureSocket_shouldFailDueToMissingTLSContext() {
        ClientOption option = new ClientOption();
        option.setServerHost("localhost");
        option.setServerPort(MOCK_PORT);
        option.setConnectTimeout(1000);
        option.setReadTimeout(1000);
        option.setVerifyCertificate(true);
        option.setTlsCertificate(null); // required if verify=true

        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, () -> {
            SocketHelper.createSecureSocket(option);
        });
        assertEquals("Failed to create Secure Socket", ex.getMessage());
    }

    @Test
    void createSecureSocket_shouldFailWithBadCertificate() {
        ClientOption option = new ClientOption();
        option.setServerHost("localhost");
        option.setServerPort(MOCK_PORT);
        option.setConnectTimeout(1000);
        option.setReadTimeout(1000);
        option.setVerifyCertificate(true);
        option.setTlsCertificate("not a cert");

        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, () -> {
            SocketHelper.createSecureSocket(option);
        });
        assertEquals("Failed to create Secure Socket", ex.getMessage());
    }

    @Test
    void createSecureSocket_shouldSucceedWithLiveServer() {
        Assumptions.assumeTrue(ENABLE_LIVE_TLS_TESTS, "Live TLS test skipped");

        ClientOption option = new ClientOption();
        option.setServerHost("localhost");
        option.setServerPort(50505);
        option.setConnectTimeout(1000);
        option.setReadTimeout(1000);
        option.setVerifyCertificate(true);
        option.setTlsCertificate(VALID_PEM_CERT);

        assertDoesNotThrow(() -> {
            Socket socket = SocketHelper.createSecureSocket(option);
            assertNotNull(socket);
            assertTrue(socket.isConnected());
            socket.close();
        });
    }
}
