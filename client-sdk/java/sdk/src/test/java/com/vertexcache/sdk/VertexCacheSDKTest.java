package com.vertexcache.sdk;

import com.vertexcache.sdk.result.CommandResult;
import com.vertexcache.sdk.transport.crypto.EncryptionMode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VertexCacheSDKTest {

    // Yes some hard code certs, these are test certs that supplied in the .env-example nothing that is prod anyways
    // will remove, this is just wip
    @Test
    void test() throws Exception {

        VertexCacheSDKOptions vertexCacheSDKOptions = new VertexCacheSDKOptions();

        vertexCacheSDKOptions.setClientId("sdk-client-java");
        vertexCacheSDKOptions.setClientToken("ea143c4a-1426-4d43-b5be-f0ecffe4a6c7");

        vertexCacheSDKOptions.setServerHost("localhost");
        vertexCacheSDKOptions.setServerPort(50505);

        vertexCacheSDKOptions.setEnableTlsEncryption(true);
        vertexCacheSDKOptions.setTlsCertificate("-----BEGIN CERTIFICATE-----\\nMIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV\\nBAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x\\nEDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv\\nY2FsaG9zdDAeFw0yNTA1MTgwMzU2NDdaFw0zNTA1MTYwMzU2NDdaMG4xEDAOBgNV\\nBAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x\\nEDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv\\nY2FsaG9zdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMHVT5HdQkUj\\nIa3sYmLQUeOja7tKtAXi1cuhCLlrvgS2DKJa9cpkgi1dsKOjJmsTqo580e+jrpdQ\\nJ+mTybdKoG6CZWEqfMizut48aTQoBteiLFSZ9J2/6nCXhxugA+aQ94lhkj3lJIHf\\nlIZeIYHaPNXH9/K4oCODJ8P6MfeQjY1ZWbrcQ9PxHQhWV/60AfTuJRJ4T/HQmOqM\\n6IcYz2t7iviIYvQq37A+wr1ClgxlfuT6JScEA8J34GivskB2p/MEn8E8y/durORz\\naaF5RBpnsc+fzVwQuvkth993rnDemdrcvTF1bdF5t88Zt5FiPD4qDF+pKloHNMRQ\\nDXBYb9Wf/t8CAwEAAaMhMB8wHQYDVR0OBBYEFOYQaTvkoqgLjRhCYBMrwLqrVfJo\\nMA0GCSqGSIb3DQEBDAUAA4IBAQCVBHT1uqtm72g085JuWdjBoBDa6bJD3Wj3L+GH\\nJaKOF26wQmXtLV0KraH3t3SUxWOM865OcbOkIiSUjMIgqmmFh1quoF4NMBa0wye8\\nJguLk6Qpffd+YXfzddxi33jdCUWgyqcTKq7bfB5DbMP4U5yVxnlXwKB0dxkaEFSx\\niAUrhcZ1+iYjelrERk8MPj9FQIzQ8FwwF4oB8ShNDhDNWCOVbSdLXwMOLH84u/ul\\nv/I4U/5/mqGGTtwNyyzFS0GYgrYua4H7Aqer2g4wv8PUYwkaAfQ49CWm9kFQxgD4\\nqwwA44GZv7zAa89WHNpbIMAA8keexZkPzJBIQNSKy2d9dhcP\\n-----END CERTIFICATE-----");

        vertexCacheSDKOptions.setEncryptionMode(EncryptionMode.ASYMMETRIC);

        vertexCacheSDKOptions.setPublicKey("-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\nbw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\nUzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\nGzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\nNwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\nEwIDAQAB\n-----END PUBLIC KEY-----");


        vertexCacheSDKOptions.setSharedEncryptionKey("neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc=");


        VertexCacheSDK sdk = new VertexCacheSDK(vertexCacheSDKOptions);

        assertTrue(sdk.isConnected());

        assertTrue(sdk.ping().isSuccess());

        CommandResult commandResult = sdk.set("my-key","my-value");


        assertTrue(commandResult.isSuccess());

        sdk.get("my-key");

        /*
        assertTrue(sdk.setValueByKey("my-key","my-value"));

        assertTrue(sdk.setValueByKey("my-key-1","my-value-1"));

        assertTrue(sdk.setValueByKey("my-key-2","my-value-2"));

        assertTrue(sdk.setValueByKey("my-key-3","my-value-3"));

        assertTrue(sdk.deleteValueByKey("my-key-1"));

         */

    }
}
