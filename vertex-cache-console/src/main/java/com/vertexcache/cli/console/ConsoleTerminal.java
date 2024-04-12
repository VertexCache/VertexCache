package com.vertexcache.cli.console;

import com.vertexcache.cli.domain.config.Config;
import com.vertexcache.common.security.CertificateTrustManager.ServerCertificateTrustManagerNoVerification;
import com.vertexcache.common.security.KeyPairHelper;

import javax.crypto.Cipher;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ConsoleTerminal {

    private static final String SERVER_IP = "127.0.0.1"; // Change this to your desired IP address
    private static final int SERVER_PORT = 50505; // Change this to your desired port number

    // Public key string obtained from the server

    private static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2urk3svMX6hHDHPpASohDaxggCK3sKj9izN3l7yq1+BUWtpQxLShJyaNRkv4iSHrNmr6f9Lx4csfMSuJqPU4Nh03BfP+0bxkpaUexDm431wFl/dHghfmoGqeg11iSBnnjGh9Q0TdlEbx3fqKqKgGCERle2OK96Wx7t0rKxhk37nlzcClBjSulgrCy1wSfIbtpfU/s4tYPVKUr+whFtk07bcsXgiE5uIO+oTdfq1UBx8IiaZq+tXYMPmJj3xyz3fVkyi20CUlaTdreOUYQYTzW6lAINNqhd4XqS4rRdXRSDQXvt3HLe+dYDF3qvBnPFTA6XHkBrx4plnLBi5GB4+7vwIDAQAB";

    public void execute() {
        // Create a Scanner object to read user input
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to " + Config.APP_NAME + " Console Terminal!");

        boolean useEncryption = true;
        boolean useSSL = true;

        try {
            OutputStream outputStream = null;
            InputStream inputStream = null;

            if(useSSL) {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[] { new ServerCertificateTrustManagerNoVerification() }, null);

                // Create SSL socket factory
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                // Create SSL socket
                SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket("localhost", 50505);

                // Perform SSL handshake
                socket.startHandshake();

                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();

            } else {

                Socket socket = new Socket(SERVER_IP, SERVER_PORT);


                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();

            }

            // Start the main loop
            while (true) {
                // Display a prompt to the user
                System.out.print(Config.APP_NAME + " " + SERVER_IP + ":" + SERVER_PORT + "> ");

                // Read the user's input
                String userInput = scanner.nextLine();

                // Check for exit command
                if (userInput.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting...");
                    break; // Exit the loop and terminate the program
                }

                // Optionally encrypt the user's input
                byte[] bytesToSend;
                if (useEncryption) {
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, KeyPairHelper.decodePublicKey(publicKey));
                    bytesToSend = cipher.doFinal(userInput.getBytes());
                } else {
                    bytesToSend = userInput.getBytes();
                }

                // Send data to the server
                outputStream.write(bytesToSend);
                outputStream.flush();

                // Read the response from the server and display it
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                if (bytesRead != -1) {
                    String receivedMessage = new String(buffer, 0, bytesRead);
                    System.out.println("Server response: " + receivedMessage);
                } else {
                    System.out.println("No data received from server.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // Close the scanner
            scanner.close();
        }
    }
}

