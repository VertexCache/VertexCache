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

import org.junit.jupiter.api.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpClientTest {

    private static final int PORT = 19191;
    private static Thread serverThread;
    private static volatile boolean running = true;

    @BeforeAll
    static void startMockServer() {
        serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                while (running) {
                    try (Socket socket = serverSocket.accept();
                         DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                         DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

                        // Read IDENT frame
                        byte[] identFrame = MessageCodec.readFramedMessage(in);
                        if (identFrame == null) continue;
                        System.out.println("[MockServer] Received IDENT: " + new String(identFrame));

                        // Respond to IDENT
                        MessageCodec.writeFramedMessage(out, "OK IDENT ACK".getBytes());
                        out.flush();

                        // Echo loop
                        while (true) {
                            byte[] msg = MessageCodec.readFramedMessage(in);
                            if (msg == null) break;
                            String response = "echo:" + new String(msg);
                            MessageCodec.writeFramedMessage(out, response.getBytes());
                            out.flush();
                        }

                    } catch (IOException ignoreOneConnection) {}
                }
            } catch (IOException serverFailure) {
                serverFailure.printStackTrace();
            }
        });
        serverThread.start();
    }

    @AfterAll
    static void stopMockServer() {
        running = false;
        try (Socket s = new Socket("localhost", PORT)) {
            s.close(); // unblocks accept()
        } catch (IOException ignored) {}
    }

    /*
    @Test
    public void testPlainSendAndReceive() {
        TcpClient client = new TcpClient(
                "localhost",
                PORT,
                false,     // useTls
                false,     // verifyCert
                null,      // cert
                3000,
                3000,
                EncryptionMode.NONE,
                null,
                null,
                "junit",
                "token"
        );

        String response = client.send("ping");
        assertEquals("echo:ping", response);
        client.close();
    }

     */
}


