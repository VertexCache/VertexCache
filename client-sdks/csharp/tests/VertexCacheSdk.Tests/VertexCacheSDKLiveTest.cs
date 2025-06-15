// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ------------------------------------------------------------------------------

using System;
using VertexCacheSdk;
using VertexCacheSdk.Model;
using Xunit;

namespace VertexCacheSdk.Tests
{
    public class VertexCacheSDKLiveTest : IDisposable
    {
        private const string CLIENT_ID = "sdk-client-csharp";
        private const string CLIENT_TOKEN = "78116067-0200-4838-8718-31ab574bfa5c";
        private const string SERVER_HOST = "localhost";
        private const int SERVER_PORT = 50505;
        private const bool ENABLE_TLS = true;

        private const string TEST_TLS_CERT = "-----BEGIN PUBLIC KEY-----\n" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\n" +
                    "bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\n" +
                    "UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\n" +
                    "GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\n" +
                    "NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n" +
                    "6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\n" +
                    "EwIDAQAB\n" +
                    "-----END PUBLIC KEY-----";

        private const string TEST_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\nbw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\nUzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\nGzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\nNwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\nEwIDAQAB\n-----END PUBLIC KEY-----";



        private VertexCacheSDK sdk;

        public VertexCacheSDKLiveTest()
        {
           // if (Environment.GetEnvironmentVariable("VC_LIVE_TEST") != "true")
             //   throw new InvalidOperationException("VC_LIVE_TEST not enabled");


            var option = new ClientOption
            {
                ServerHost = SERVER_HOST,
                ServerPort = SERVER_PORT,
                EnableTlsEncryption = ENABLE_TLS,
                VerifyCertificate = false,
                TlsCertificate = TEST_TLS_CERT,
                EncryptionMode = EncryptionMode.Asymmetric,
                PublicKey = TEST_PUBLIC_KEY
            };
            option.SetClientId(CLIENT_ID);
            option.SetClientToken(CLIENT_TOKEN);

            sdk = new VertexCacheSDK(option);
            sdk.OpenConnection();
        }

        public void Dispose()
        {
            sdk?.Close();
        }

        [Fact]
        public void PingShouldSucceed()
        {
            var result = sdk.Ping();
            Assert.True(result.IsSuccess());
            Assert.StartsWith("PONG", result.GetMessage());
        }


        [Fact]
        public void SetShouldSucceed()
        {
            var result = sdk.Set("test-key", "value-123");
            Assert.True(result.IsSuccess());
            Assert.Equal("OK", result.GetMessage());
        }

        [Fact]
        public void GetShouldReturnPreviouslySetValue()
        {
            sdk.Set("test-key", "value-123");
            var result = sdk.Get("test-key");
            Assert.True(result.IsSuccess());
            Assert.Equal("value-123", result.GetValue());
        }

        [Fact]
        public void DelShouldSucceedAndRemoveKey()
        {
            sdk.Set("delete-key", "to-be-deleted");
            var del = sdk.Del("delete-key");
            Assert.True(del.IsSuccess());

            var get = sdk.Get("delete-key");
            Assert.True(get.IsSuccess());
            Assert.Null(get.GetValue());
        }

        [Fact]
        public void GetOnMissingKeyShouldSucceedWithNull()
        {
            var result = sdk.Get("nonexistent-key");
            Assert.True(result.IsSuccess());
            Assert.Null(result.GetValue());
        }

        [Fact]
        public void SetWithSecondaryIndexShouldSucceed()
        {
            var result = sdk.Set("test-key", "value-123", "test-secondary-index");
            Assert.True(result.IsSuccess());
            Assert.Equal("OK", result.GetMessage());
        }

        [Fact]
        public void SetWithSecondaryAndTertiaryIndexShouldSucceed()
        {
            var result = sdk.Set("test-key", "value-123", "test-secondary-index", "test-tertiary-index");
            Assert.True(result.IsSuccess());
            Assert.Equal("OK", result.GetMessage());
        }

        [Fact]
        public void GetBySecondaryIndexShouldReturnValue()
        {
            sdk.Set("test-key", "value-123", "test-secondary-index");
            var result = sdk.GetBySecondaryIndex("test-secondary-index");
            Assert.True(result.IsSuccess());
            Assert.Equal("value-123", result.GetValue());
        }

        [Fact]
        public void GetByTertiaryIndexShouldReturnValue()
        {
            sdk.Set("test-key", "value-123", "test-secondary-index", "test-tertiary-index");
            var result = sdk.GetByTertiaryIndex("test-tertiary-index");
            Assert.True(result.IsSuccess());
            Assert.Equal("value-123", result.GetValue());
        }

/*
        [Fact]
        public void FailedHostShouldThrow()
        {
            var option = new ClientOption
            {
                ClientId = CLIENT_ID,
                ClientToken = CLIENT_TOKEN,
                ServerHost = "bad-host",
                ServerPort = SERVER_PORT,
                EnableTlsEncryption = ENABLE_TLS,
                TlsCertificate = TEST_TLS_CERT,
                EncryptionMode = EncryptionMode.ASYMMETRIC,
                PublicKey = TEST_PUBLIC_KEY
            };
            Assert.Throws<VertexCacheSdkException>(() => new VertexCacheSDK(option).OpenConnection());
        }

        [Fact]
        public void FailedPortShouldThrow()
        {
            var option = new ClientOption
            {
                ClientId = CLIENT_ID,
                ClientToken = CLIENT_TOKEN,
                ServerHost = SERVER_HOST,
                ServerPort = 0,
                EnableTlsEncryption = ENABLE_TLS,
                TlsCertificate = TEST_TLS_CERT,
                EncryptionMode = EncryptionMode.ASYMMETRIC,
                PublicKey = TEST_PUBLIC_KEY
            };
            Assert.Throws<VertexCacheSdkException>(() => new VertexCacheSDK(option).OpenConnection());
        }

        [Fact]
        public void FailedSecureTlsShouldThrow()
        {
            var option = new ClientOption
            {
                ClientId = CLIENT_ID,
                ClientToken = CLIENT_TOKEN,
                ServerHost = SERVER_HOST,
                ServerPort = SERVER_PORT,
                EnableTlsEncryption = true,
                VerifyCertificate = true,
                TlsCertificate = TEST_TLS_CERT,
                EncryptionMode = EncryptionMode.ASYMMETRIC,
                PublicKey = TEST_PUBLIC_KEY
            };
            var ex = Assert.Throws<VertexCacheSdkException>(() => new VertexCacheSDK(option).OpenConnection());
            Assert.Contains("Failed to create Secure Socket", ex.Message);
        }

        [Fact]
        public void NonSecureTlsShouldSucceed()
        {
            var option = new ClientOption
            {
                ClientId = CLIENT_ID,
                ClientToken = CLIENT_TOKEN,
                ServerHost = SERVER_HOST,
                ServerPort = SERVER_PORT,
                EnableTlsEncryption = true,
                VerifyCertificate = false,
                TlsCertificate = null,
                EncryptionMode = EncryptionMode.ASYMMETRIC,
                PublicKey = TEST_PUBLIC_KEY
            };
            var tempSdk = new VertexCacheSDK(option);
            tempSdk.OpenConnection();
            tempSdk.Close();
        }

        [Fact]
        public void InvalidPublicKeyShouldThrow()
        {
            var option = new ClientOption
            {
                ClientId = CLIENT_ID,
                ClientToken = CLIENT_TOKEN,
                ServerHost = SERVER_HOST,
                ServerPort = SERVER_PORT,
                EnableTlsEncryption = ENABLE_TLS,
                VerifyCertificate = false,
                TlsCertificate = TEST_TLS_CERT,
                EncryptionMode = EncryptionMode.ASYMMETRIC
            };

            var ex = Assert.Throws<VertexCacheSdkException>(() => option.SetPublicKey(TEST_PUBLIC_KEY + "_BAD"));
            Assert.Contains("Invalid public key", ex.Message);
        }

        [Fact]
        public void InvalidSharedKeyShouldThrow()
        {
            var option = new ClientOption
            {
                ClientId = CLIENT_ID,
                ClientToken = CLIENT_TOKEN,
                ServerHost = SERVER_HOST,
                ServerPort = SERVER_PORT,
                EnableTlsEncryption = ENABLE_TLS,
                VerifyCertificate = false,
                TlsCertificate = TEST_TLS_CERT,
                EncryptionMode = EncryptionMode.SYMMETRIC
            };

            var ex = Assert.Throws<VertexCacheSdkException>(() => option.SetSharedEncryptionKey("_BAD_SHARED_KEY"));
            Assert.Contains("Invalid shared key", ex.Message);
        }
            */
    }

}
