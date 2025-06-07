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
using System.Security.Cryptography;
using System.Text;
using VertexCacheSdk.Comm;
using Xunit;

namespace VertexCacheSdk.Tests.Comm
{
    public class GcmCryptoHelperTest
    {
        private readonly byte[] key = new byte[32]; // 256-bit all-zero key
        private readonly byte[] message = Encoding.UTF8.GetBytes("VertexCache secure payload");

        [Fact]
        public void EncryptDecryptRoundTrip_ShouldSucceed()
        {
            byte[] encrypted = GcmCryptoHelper.Encrypt(message, key);
            Assert.NotNull(encrypted);
            Assert.True(encrypted.Length > message.Length);

            byte[] decrypted = GcmCryptoHelper.Decrypt(encrypted, key);
            Assert.Equal(message, decrypted);
        }

        [Fact]
        public void Decrypt_ShouldFail_OnTamperedCiphertext()
        {
            byte[] encrypted = GcmCryptoHelper.Encrypt(message, key);
            encrypted[encrypted.Length - 1] ^= 0x01; // flip last byte

            Assert.Throws<AuthenticationTagMismatchException>(() => {
                GcmCryptoHelper.Decrypt(encrypted, key);
            });
        }

        [Fact]
        public void Decrypt_ShouldFail_IfTooShort()
        {
            byte[] invalid = new byte[5];
            Assert.Throws<ArgumentException>(() =>
            {
                GcmCryptoHelper.Decrypt(invalid, key);
            });
        }

        [Fact]
        public void Base64EncodeDecode_ShouldMatchOriginalKey()
        {
            string encoded = GcmCryptoHelper.EncodeBase64Key(key);
            byte[] decoded = GcmCryptoHelper.DecodeBase64Key(encoded);

            Assert.Equal(key, decoded);
        }

        [Fact]
        public void GenerateBase64Key_ShouldReturnValid256BitKey()
        {
            string base64Key = GcmCryptoHelper.GenerateBase64Key();
            byte[] keyBytes = GcmCryptoHelper.DecodeBase64Key(base64Key);

            Assert.Equal(32, keyBytes.Length);
        }

        [Fact]
        public void Encrypt_ShouldGenerateDifferentIVs()
        {
            byte[] key = new byte[32];
            RandomNumberGenerator.Fill(key);

            byte[] c1 = GcmCryptoHelper.Encrypt(message, key);
            byte[] c2 = GcmCryptoHelper.Encrypt(message, key);

            Assert.NotEqual(Convert.ToBase64String(c1), Convert.ToBase64String(c2));
        }

        [Fact]
        public void ReconciliationTest_EncryptWithFixedIv()
        {
            byte[] key = new byte[16]; // match Java
            byte[] iv = new byte[12];  // match Java
            byte[] data = Encoding.UTF8.GetBytes("VertexCacheGCMTest");

            byte[] ciphertext = new byte[data.Length];
            byte[] tag = new byte[16];

            using (AesGcm aes = new AesGcm(key))
            {
                aes.Encrypt(iv, data, ciphertext, tag);
            }

            byte[] combined = new byte[iv.Length + ciphertext.Length + tag.Length];
            Buffer.BlockCopy(iv, 0, combined, 0, iv.Length);
            Buffer.BlockCopy(ciphertext, 0, combined, iv.Length, ciphertext.Length);
            Buffer.BlockCopy(tag, 0, combined, iv.Length + ciphertext.Length, tag.Length);

            // Now decrypt
            byte[] output = GcmCryptoHelper.Decrypt(combined, key);
            Assert.Equal(data, output);

            Console.WriteLine("[RECON] Plaintext: " + Encoding.UTF8.GetString(data));
            Console.WriteLine("[RECON] Key (hex): " + BitConverter.ToString(key).Replace("-", "").ToLower());
            Console.WriteLine("[RECON] IV (hex): " + BitConverter.ToString(iv).Replace("-", "").ToLower());
            Console.WriteLine("[RECON] Encrypted (hex): " + BitConverter.ToString(combined).Replace("-", "").ToLower());
        }
    }
}
