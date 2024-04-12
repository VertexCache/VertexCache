package com.vertexcache;

import com.vertexcache.server.SocketServer;

import com.vertexcache.common.security.CertificateTrustManager.ServerCertificateTrustManagerNoVerification;
import com.vertexcache.common.security.CertificateTrustManager.ServerCertificateTrustManagerVerification;
import com.vertexcache.common.security.KeyPairHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import javax.crypto.*;
import javax.net.ssl.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VertexCacheServerTest {

    private static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2urk3svMX6hHDHPpASohDaxggCK3sKj9izN3l7yq1+BUWtpQxLShJyaNRkv4iSHrNmr6f9Lx4csfMSuJqPU4Nh03BfP+0bxkpaUexDm431wFl/dHghfmoGqeg11iSBnnjGh9Q0TdlEbx3fqKqKgGCERle2OK96Wx7t0rKxhk37nlzcClBjSulgrCy1wSfIbtpfU/s4tYPVKUr+whFtk07bcsXgiE5uIO+oTdfq1UBx8IiaZq+tXYMPmJj3xyz3fVkyi20CUlaTdreOUYQYTzW6lAINNqhd4XqS4rRdXRSDQXvt3HLe+dYDF3qvBnPFTA6XHkBrx4plnLBi5GB4+7vwIDAQAB";



    private SocketServer server;
    private Thread serverThread;
    private Socket clientSocket;

    private SocketServer vertexCacheServer;

   // private PublicKey publicKey;
//    private PrivateKey privateKey;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {

        /*
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        server = new VertexCacheServer();
        server.isEncrypted(keyPair.getPublic(),keyPair.getPrivate());
        serverThread = new Thread(() -> server.execute());
        serverThread.start();
*/
        // Generate keys for testing encryption/decryption
        //KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        //keyPairGenerator.initialize(2048);
        //KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //publicKey = keyPair.getPublic();
        //privateKey = keyPair.getPrivate();


        //VertexCacheServer vertexCacheServer = new VertexCacheServer();
        //vertexCacheServer.isEncrypted(null,null);
        //vertexCacheServer.execute();


    }

    @AfterEach
    public void tearDown() {
/*
        try {
            serverThread.join();
            serverThread.interrupt();


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/


    }

    //@Test
    public void testNonEncryptedDataSentIn() throws IOException, InterruptedException {


        clientSocket = new Socket("localhost", 50505);
        OutputStream outputStream = clientSocket.getOutputStream();
        InputStream inputStream = clientSocket.getInputStream();

        String messageToSend = "Hello from client";
        outputStream.write(messageToSend.getBytes());
        outputStream.flush();

        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        String receivedMessage = new String(buffer, 0, bytesRead);

        assertEquals(messageToSend, receivedMessage);

        inputStream.close();
        outputStream.close();
        clientSocket.close();
    }

   //@Test
    public void testEncryptedDataSentIn() throws Exception {
        clientSocket = new Socket("localhost", 50505);
        OutputStream outputStream = clientSocket.getOutputStream();
        InputStream inputStream = clientSocket.getInputStream();

        // Encrypt the message using the server's public key
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, KeyPairHelper.decodePublicKey(publicKey));
        String messageToSend = "Hello from client !!! XXXX";
        byte[] encryptedBytes = cipher.doFinal(messageToSend.getBytes());

        // Send encrypted data to server
        outputStream.write(encryptedBytes);
        outputStream.flush();

        // Receive response from server
       /*
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        byte[] decryptedBytes = cipher.doFinal(buffer, 0, bytesRead);
        String receivedMessage = new String(decryptedBytes);


        assertEquals(messageToSend, receivedMessage);
        */


       byte[] buffer = new byte[1024];
       int bytesRead = inputStream.read(buffer);
       if (bytesRead != -1) {
           String receivedMessage = new String(buffer, 0, bytesRead);
           System.out.println("Received from server: " + receivedMessage);
           //assertEquals(messageToSend, receivedMessage);
           assertEquals("+OK", receivedMessage);
       } else {
           System.out.println("No data received from server.");
       }

        inputStream.close();
        outputStream.close();
        clientSocket.close();
    }

    //@Test
    public void testPing() throws Exception {
        clientSocket = new Socket("localhost", 50505);
        OutputStream outputStream = clientSocket.getOutputStream();
        InputStream inputStream = clientSocket.getInputStream();

        // Encrypt the message using the server's public key
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, KeyPairHelper.decodePublicKey(publicKey));
        String messageToSend = "PING";
        byte[] encryptedBytes = cipher.doFinal(messageToSend.getBytes());

        // Send encrypted data to server
        outputStream.write(encryptedBytes);
        outputStream.flush();



        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        if (bytesRead != -1) {
            String receivedMessage = new String(buffer, 0, bytesRead);
            System.out.println("Received from server: " + receivedMessage);
            //assertEquals(messageToSend, receivedMessage);
            byte[] expected = "+PONG\r\n".getBytes(StandardCharsets.UTF_8);

            byte[] actual = new byte[bytesRead];
            System.arraycopy(buffer, 0, actual, 0, bytesRead);

            assertEquals(new String(expected), new String(actual));
        } else {
            System.out.println("No data received from server.");
        }

        inputStream.close();
        outputStream.close();
        clientSocket.close();
    }


    /**
     * Ignore checking for Cert, just accepts it
     * @throws Exception
     */
    @Test
    public void testPingWithPublicKeyAndSSLIgnoreValidCert() throws Exception {

        // Create SSL context and trust manager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { new ServerCertificateTrustManagerNoVerification() }, null);

        // Create SSL socket factory
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        // Create SSL socket
        SSLSocket clientSocket = (SSLSocket) sslSocketFactory.createSocket("localhost", 50505);

        // Perform SSL handshake
        clientSocket.startHandshake();

        OutputStream outputStream = clientSocket.getOutputStream();
        InputStream inputStream = clientSocket.getInputStream();

        // Encrypt the message using the server's public key
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, KeyPairHelper.decodePublicKey(publicKey));
        String messageToSend = "PING";
        byte[] encryptedBytes = cipher.doFinal(messageToSend.getBytes());

        // Send encrypted data to server
        outputStream.write(encryptedBytes);
        outputStream.flush();

        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        if (bytesRead != -1) {
            String receivedMessage = new String(buffer, 0, bytesRead);
            System.out.println("Received from server: " + receivedMessage);
            //assertEquals(messageToSend, receivedMessage);
            byte[] expected = "+PONG\r\n".getBytes(StandardCharsets.UTF_8);

            byte[] actual = new byte[bytesRead];
            System.arraycopy(buffer, 0, actual, 0, bytesRead);

            assertEquals(new String(expected), new String(actual));
        } else {
            System.out.println("No data received from server.");
        }

        inputStream.close();
        outputStream.close();
        clientSocket.close();
    }












   @Test
    public void testPingWithDynamicCertificateImport() throws Exception {

        // Create SSL context with dynamic certificate import
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new X509TrustManager[]{new ServerCertificateTrustManagerVerification("../vertex-cache-config/server/test_server_certificate.pem")}, null);

        // Create SSL socket factory
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        // Create SSL socket
        SSLSocket clientSocket = (SSLSocket) sslSocketFactory.createSocket("localhost", 50505);

        // Perform SSL handshake
        clientSocket.startHandshake();

        OutputStream outputStream = clientSocket.getOutputStream();
        InputStream inputStream = clientSocket.getInputStream();

        // Encrypt the message using the server's public key
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, KeyPairHelper.decodePublicKey(publicKey));
        String messageToSend = "PING";
        byte[] encryptedBytes = cipher.doFinal(messageToSend.getBytes());

        // Send encrypted data to server
        outputStream.write(encryptedBytes);
        outputStream.flush();

        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        if (bytesRead != -1) {
            String receivedMessage = new String(buffer, 0, bytesRead);
            System.out.println("Received from server: " + receivedMessage);
            //assertEquals(messageToSend, receivedMessage);
            byte[] expected = "+PONG\r\n".getBytes(StandardCharsets.UTF_8);

            byte[] actual = new byte[bytesRead];
            System.arraycopy(buffer, 0, actual, 0, bytesRead);

            assertEquals(new String(expected), new String(actual));
        } else {
            System.out.println("No data received from server.");
        }

        inputStream.close();
        outputStream.close();
        clientSocket.close();
    }



}

