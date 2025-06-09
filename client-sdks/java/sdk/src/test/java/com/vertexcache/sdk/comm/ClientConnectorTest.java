/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vertexcache.sdk.comm;

import com.vertexcache.sdk.model.ClientOption;
import com.vertexcache.sdk.model.EncryptionMode;
import com.vertexcache.sdk.model.VertexCacheSdkException;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class ClientConnectorTest {

    private static final int PORT = 19191;
    private static Thread serverThread;
    private static volatile boolean running = true;

    private static final String TEST_SHARED_KEY = "neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc=";
    private static final String TEST_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\n" +
            "bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\n" +
            "UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\n" +
            "GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\n" +
            "NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n" +
            "6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\n" +
            "EwIDAQAB\n" +
            "-----END PUBLIC KEY-----";

    @BeforeAll
    static void startEchoServer() {
        serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                while (running) {
                    try (Socket socket = serverSocket.accept();
                         DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                         DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

                        byte[] ident = MessageCodec.readFramedMessage(in);
                        if (ident == null) continue;
                        MessageCodec.writeFramedMessage(out, "+OK IDENT ACK".getBytes(StandardCharsets.UTF_8));
                        out.flush();

                        while (true) {
                            byte[] msg = MessageCodec.readFramedMessage(in);
                            if (msg == null) break;
                            String response = "echo:" + new String(msg);
                            MessageCodec.writeFramedMessage(out, response.getBytes());
                            out.flush();
                        }

                    } catch (IOException ignore) {}
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        serverThread.start();
    }

    @AfterAll
    static void stopEchoServer() {
        running = false;
        try (Socket s = new Socket("127.0.0.1", PORT)) {
            s.close(); // unblock accept()
        } catch (IOException ignored) {}
    }

    @Test
    void testSymmetricEncryption_shouldSucceed() {
        ClientOption opt = new ClientOption();
        opt.setClientId("test-sym");
        opt.setServerHost("127.0.0.1");
        opt.setServerPort(PORT);
        opt.setEncryptionMode(EncryptionMode.SYMMETRIC);
        opt.setSharedEncryptionKey(TEST_SHARED_KEY);

        ClientConnector client = new ClientConnector(opt);
        client.connect();
        assertTrue(client.isConnected());

        String reply = client.send("secure-msg");

        // The server just echoes back the encrypted bytes — we can't match the plain text.
        // Just confirm we got something non-null and non-empty.
        assertNotNull(reply);
        assertFalse(reply.isEmpty());

        client.close();
        assertFalse(client.isConnected());
    }

    @Test
    void testInvalidSymmetricKey_shouldThrow() {
        ClientOption opt = new ClientOption();
        opt.setClientId("bad-sym");
        opt.setServerHost("127.0.0.1");
        opt.setServerPort(PORT);
        opt.setEncryptionMode(EncryptionMode.SYMMETRIC);

        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, () -> {
            opt.setSharedEncryptionKey("short");
        });

        assertTrue(ex.getMessage().contains("Invalid shared key"));
    }

    @Test
    void testInvalidAsymmetricKey_shouldThrow() {
        ClientOption opt = new ClientOption();
        opt.setClientId("bad-asym");
        opt.setServerHost("127.0.0.1");
        opt.setServerPort(PORT);
        opt.setEncryptionMode(EncryptionMode.ASYMMETRIC);

        VertexCacheSdkException ex = assertThrows(
                VertexCacheSdkException.class,
                () -> opt.setPublicKey(TEST_PUBLIC_KEY + "_BAD")
        );

        assertTrue(ex.getMessage().contains("Invalid public key"));
    }

    @Test
    void testConnectToWrongPort_shouldFailGracefully() {
        ClientOption opt = new ClientOption();
        opt.setClientId("wrong-port");
        opt.setServerHost("127.0.0.1");
        opt.setServerPort(65530);
        opt.setEncryptionMode(EncryptionMode.NONE);

        ClientConnector client = new ClientConnector(opt);
        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, client::connect);
        assertNotNull(ex.getMessage());
    }

    @Test
    void testIdentHandshakeFailure_shouldThrow() throws Exception {
        final int TEMP_PORT = 19292;
        Thread identFailServer = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(TEMP_PORT);
                 Socket socket = ss.accept();
                 DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                MessageCodec.readFramedMessage(in); // read IDENT
                MessageCodec.writeFramedMessage(out, "-ERR Not authorized".getBytes());
                out.flush();
            } catch (IOException ignored) {}
        });
        identFailServer.start();

        Thread.sleep(100); // allow server to start

        ClientOption opt = new ClientOption();
        opt.setClientId("fail-ident");
        opt.setServerHost("127.0.0.1");
        opt.setServerPort(TEMP_PORT);
        opt.setEncryptionMode(EncryptionMode.NONE);

        ClientConnector client = new ClientConnector(opt);
        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, client::connect);
        assertTrue(ex.getMessage().contains("Authorization failed"));
    }
}
