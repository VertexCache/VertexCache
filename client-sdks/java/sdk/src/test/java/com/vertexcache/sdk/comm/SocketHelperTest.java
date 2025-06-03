package com.vertexcache.sdk.comm;

import com.vertexcache.sdk.model.ClientOption;
import com.vertexcache.sdk.model.VertexCacheSdkException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class SocketHelperTest {

    private static final int MOCK_PORT = 18888;
    private static Thread mockServerThread;
    private static volatile boolean serverRunning;

    @BeforeAll
    static void startMockServer() {
        serverRunning = true;
        mockServerThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(MOCK_PORT)) {
                while (serverRunning) {
                    try {
                        Socket socket = serverSocket.accept();
                        socket.close(); // immediately close after accept
                    } catch (IOException ignored) {
                    }
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
            // Nudge to unblock serverSocket.accept()
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
        option.setServerPort(65534); // assuming nothing is listening here
        option.setConnectTimeout(500);
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
        option.setVerifyCertificate(false);
        option.setTlsCertificate(null); // required if verify=true

        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, () -> {
            SocketHelper.createSecureSocket(option);
        });
        assertEquals("Failed to create Secure Socket", ex.getMessage());
    }
}
