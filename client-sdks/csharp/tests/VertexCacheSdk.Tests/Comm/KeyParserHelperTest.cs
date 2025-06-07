// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
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
using System.Text;
using System.Security.Cryptography;
using VertexCacheSdk.Comm;
using VertexCacheSdk.Model;
using Xunit;

namespace VertexCacheSdk.Tests.Comm
{
    public class KeyParserHelperTest
    {
        private const string VALID_PUBLIC_KEY_PEM = @"
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
EwIDAQAB
-----END PUBLIC KEY-----";

        private const string INVALID_PUBLIC_KEY_PEM = "-----BEGIN PUBLIC KEY-----INVALID-----END PUBLIC KEY-----";
        private const string VALID_SHARED_KEY_BASE64 = "YWJjZGVmZ2hpamtsbW5vcA=="; // "abcdefghijklmnop"
        private const string INVALID_SHARED_KEY_BASE64 = "%%%INVALID%%%";

        [Fact]
        public void ConfigPublicKeyIfEnabled_ShouldSucceedWithValidPEM()
        {
            RSA rsa = KeyParserHelper.ConfigPublicKeyIfEnabled(VALID_PUBLIC_KEY_PEM);
            Assert.NotNull(rsa);
        }

        [Fact]
        public void ConfigPublicKeyIfEnabled_ShouldReturnRSA()
        {
            RSA rsa = KeyParserHelper.ConfigPublicKeyIfEnabled(VALID_PUBLIC_KEY_PEM);
            Assert.Equal("RSA", rsa.KeyExchangeAlgorithm);
        }

        [Fact]
        public void ConfigPublicKeyIfEnabled_ShouldFailWithInvalidPEM()
        {
            var ex = Assert.Throws<VertexCacheSdkException>(() =>
                KeyParserHelper.ConfigPublicKeyIfEnabled(INVALID_PUBLIC_KEY_PEM));
            Assert.Equal("Invalid public key", ex.Message);
        }

        [Fact]
        public void ConfigSharedKeyIfEnabled_ShouldSucceedWithValidBase64()
        {
            byte[] key = KeyParserHelper.ConfigSharedKeyIfEnabled(VALID_SHARED_KEY_BASE64);
            Assert.NotNull(key);
            Assert.Equal(16, key.Length);
        }

        [Fact]
        public void ConfigSharedKeyIfEnabled_ShouldReturnCorrectBytes()
        {
            byte[] expected = Encoding.UTF8.GetBytes("abcdefghijklmnop");
            byte[] actual = KeyParserHelper.ConfigSharedKeyIfEnabled(VALID_SHARED_KEY_BASE64);
            Assert.Equal(expected, actual);
        }

        [Fact]
        public void ConfigSharedKeyIfEnabled_ShouldFailWithInvalidBase64()
        {
            var ex = Assert.Throws<VertexCacheSdkException>(() =>
                KeyParserHelper.ConfigSharedKeyIfEnabled(INVALID_SHARED_KEY_BASE64));
            Assert.Equal("Invalid shared key", ex.Message);
        }
    }
}
