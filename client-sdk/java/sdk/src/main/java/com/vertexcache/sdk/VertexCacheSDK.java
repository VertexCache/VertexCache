package com.vertexcache.sdk;

import com.vertexcache.sdk.protocol.Command;
import com.vertexcache.sdk.protocol.command.PingCommand;
import com.vertexcache.sdk.protocol.command.SetCommand;
import com.vertexcache.sdk.transport.TcpClient;

public class VertexCacheSDK {

    private VertexCacheSDKOptions vertexCacheSDKOptions;


    private TcpClient tcpClient;

    public VertexCacheSDK(VertexCacheSDKOptions vertexCacheSDKOptions) {
        this.vertexCacheSDKOptions = vertexCacheSDKOptions;

        tcpClient = new TcpClient(
                this.vertexCacheSDKOptions.getServerHost(),
                this.vertexCacheSDKOptions.getServerPort(),
                this.vertexCacheSDKOptions.isEnableTlsEncryption(),
                this.vertexCacheSDKOptions.isVerifyCertificate(),
                this.vertexCacheSDKOptions.getTlsCertificate(),
                this.vertexCacheSDKOptions.getConnectTimeout(),
                this.vertexCacheSDKOptions.getReadTimeout(),
                this.vertexCacheSDKOptions.isEnablePublicKeyEncryption(),              // encrypt messages
                this.vertexCacheSDKOptions.getPublicKey()
        );
    }

    public boolean isPingable() {
        return new PingCommand().execute(this.tcpClient).isSuccess();
    }

    public boolean setKey(String key, String value) {
        return new SetCommand(key,value).execute(this.tcpClient).isSuccess();
    }


    public String getMessage() throws Exception {

/*
        TcpClient client = new TcpClient(
                "localhost",
                50505,
                true,              // use TLS
                false,              // verify server cert
                "-----BEGIN CERTIFICATE-----\nMIIDiTCCAnGgAwIBAgIITq/F/pEVepgwDQYJKoZIhvcNAQEMBQAwcjELMAkGA1UE\nBhMCQkMxCzAJBgNVBAgTAlZDMRQwEgYDVQQHEwtWZXJ0ZXhDYWNoZTEUMBIGA1UE\nChMLVmVydGV4Q2FjaGUxFDASBgNVBAsTC1ZlcnRleENhY2hlMRQwEgYDVQQDEwtW\nZXJ0ZXhDYWNoZTAgFw0yNTAzMTYxNTIwMjVaGA8yMTI1MDIyMDE1MjAyNVowcjEL\nMAkGA1UEBhMCQkMxCzAJBgNVBAgTAlZDMRQwEgYDVQQHEwtWZXJ0ZXhDYWNoZTEU\nMBIGA1UEChMLVmVydGV4Q2FjaGUxFDASBgNVBAsTC1ZlcnRleENhY2hlMRQwEgYD\nVQQDEwtWZXJ0ZXhDYWNoZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB\nAJAkANp/l5iPScBSDhRu3iGPfvoAkmbJMDTnxP3i6hdOx3999b1C0yKdVJJClMGO\naywKboa414SUvzfRXx2/7WTgkjdOTL/m2kNNUc0FHtqUpIjR2eCkyXh9DM+2GpF+\nfUMtHjClhOA8eolImcI6xYJynpejGV3YmBd9HQxOGOWJmzrptrPpZP/tdPwjqrxt\nMa1/luTp2JIsrQpzfvdiats0WE3JhvyA1SX2+Tx0O6ri3q9hqLBsdPChgF5hiGYU\nUdIikX9MMCkzWa8gbBp2QipgwKlk+qgc+aw2A/yVJGGkYB4vLkDQ4GKRcXUNnc5c\njlR8ExYMWdAC55giRL9zdm0CAwEAAaMhMB8wHQYDVR0OBBYEFAwCU6xaQHRkZdw4\nMeoHybhSMfnnMA0GCSqGSIb3DQEBDAUAA4IBAQB7gzu5nV5c+76ZjVgdGgi/DeIP\nVAKYvPxxiIIJuZl5ySryyri2W/k0vmUm8LGbcNCNtOOXnpunKem7Nzk6EfzR1Ckl\n9CALIDg6NXmwoFaortP0O3QsAOKjmXmrPNJ6zomqdBOTbUxnGgqrEWqpMClc5gvP\nvqeUyEtJLNROUSqC4tuXO5IDSE3s1bYMfSQj69nThPzTyX+u9vXahi3IDi7aBPU/\nds1ltqVzU8tqMk9bGfgyUt5n6g0W/Q98MAEbE2yRnWUV4SncSevyzl87JCk2Zm/z\nFnVvyxc63ee/dspYo4m5e3AKrFLcPO4DR+YzIoregc6FiQCTo0W0RjPskrGZ\n-----END CERTIFICATE-----",
                3000,              // connect timeout (ms)
                3000,              // read timeout (ms)
                true,              // encrypt messages
                "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\nbw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\nUzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\nGzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\nNwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\nEwIDAQAB\n-----END PUBLIC KEY-----"          // public key PEM
        );


        String response = client.send("PING");
        System.out.println("Server said: " + response);

        String response2 = client.send("set abc 123 idx1 key1 idx2 key2");
        System.out.println("Server said: " + response2);

        String response3 = client.send("get abc");
        System.out.println("Server said: " + response3);

        String response4 = client.send("getidx1 key1");
        System.out.println("Server said: " + response4);


        String response5 = client.send("getidx2 key2");
        System.out.println("Server said: " + response5);


        client.close();

*/
        return "VertexCache SDK!";



    }

    public boolean isConnected() {
        return this.tcpClient.isConnected();
    }

    public void close() {
        this.tcpClient.close();
    }
}