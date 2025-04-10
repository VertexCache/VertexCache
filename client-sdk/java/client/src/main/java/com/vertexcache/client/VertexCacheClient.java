package com.vertexcache.client;

import com.vertexcache.sdk.VertexCacheSDK;
import com.vertexcache.sdk.VertexCacheSDKOptions;

public class VertexCacheClient {
    public static void main(String[] args) {

        VertexCacheSDKOptions vertexCacheSDKOptions = new VertexCacheSDKOptions();

        vertexCacheSDKOptions.setServerHost("localhost");
        vertexCacheSDKOptions.setServerPort(50505);

        vertexCacheSDKOptions.setEnableTlsEncryption(true);
        vertexCacheSDKOptions.setTlsCertificate("-----BEGIN CERTIFICATE-----\nMIIDiTCCAnGgAwIBAgIITq/F/pEVepgwDQYJKoZIhvcNAQEMBQAwcjELMAkGA1UE\nBhMCQkMxCzAJBgNVBAgTAlZDMRQwEgYDVQQHEwtWZXJ0ZXhDYWNoZTEUMBIGA1UE\nChMLVmVydGV4Q2FjaGUxFDASBgNVBAsTC1ZlcnRleENhY2hlMRQwEgYDVQQDEwtW\nZXJ0ZXhDYWNoZTAgFw0yNTAzMTYxNTIwMjVaGA8yMTI1MDIyMDE1MjAyNVowcjEL\nMAkGA1UEBhMCQkMxCzAJBgNVBAgTAlZDMRQwEgYDVQQHEwtWZXJ0ZXhDYWNoZTEU\nMBIGA1UEChMLVmVydGV4Q2FjaGUxFDASBgNVBAsTC1ZlcnRleENhY2hlMRQwEgYD\nVQQDEwtWZXJ0ZXhDYWNoZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB\nAJAkANp/l5iPScBSDhRu3iGPfvoAkmbJMDTnxP3i6hdOx3999b1C0yKdVJJClMGO\naywKboa414SUvzfRXx2/7WTgkjdOTL/m2kNNUc0FHtqUpIjR2eCkyXh9DM+2GpF+\nfUMtHjClhOA8eolImcI6xYJynpejGV3YmBd9HQxOGOWJmzrptrPpZP/tdPwjqrxt\nMa1/luTp2JIsrQpzfvdiats0WE3JhvyA1SX2+Tx0O6ri3q9hqLBsdPChgF5hiGYU\nUdIikX9MMCkzWa8gbBp2QipgwKlk+qgc+aw2A/yVJGGkYB4vLkDQ4GKRcXUNnc5c\njlR8ExYMWdAC55giRL9zdm0CAwEAAaMhMB8wHQYDVR0OBBYEFAwCU6xaQHRkZdw4\nMeoHybhSMfnnMA0GCSqGSIb3DQEBDAUAA4IBAQB7gzu5nV5c+76ZjVgdGgi/DeIP\nVAKYvPxxiIIJuZl5ySryyri2W/k0vmUm8LGbcNCNtOOXnpunKem7Nzk6EfzR1Ckl\n9CALIDg6NXmwoFaortP0O3QsAOKjmXmrPNJ6zomqdBOTbUxnGgqrEWqpMClc5gvP\nvqeUyEtJLNROUSqC4tuXO5IDSE3s1bYMfSQj69nThPzTyX+u9vXahi3IDi7aBPU/\nds1ltqVzU8tqMk9bGfgyUt5n6g0W/Q98MAEbE2yRnWUV4SncSevyzl87JCk2Zm/z\nFnVvyxc63ee/dspYo4m5e3AKrFLcPO4DR+YzIoregc6FiQCTo0W0RjPskrGZ\n-----END CERTIFICATE-----");


        vertexCacheSDKOptions.setPublicKey("-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\nbw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\nUzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\nGzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\nNwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\nEwIDAQAB\n-----END PUBLIC KEY-----");

        VertexCacheSDK sdk = new VertexCacheSDK(vertexCacheSDKOptions);


    }
}
