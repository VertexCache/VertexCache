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

namespace VertexCacheSdk.Comm
{
    public static class GcmCryptoHelper
    {
        private const int IvLength = 12;       // 96 bits
        private const int TagLength = 16;      // 128 bits (in bytes)

        public static byte[] Encrypt(byte[] plaintext, byte[] key)
        {
            byte[] iv = new byte[IvLength];
            RandomNumberGenerator.Fill(iv);

            byte[] ciphertext = new byte[plaintext.Length];
            byte[] tag = new byte[TagLength];

            using (AesGcm aes = new AesGcm(key))
            {
                aes.Encrypt(iv, plaintext, ciphertext, tag);
            }

            // Concatenate IV + ciphertext + tag
            byte[] result = new byte[IvLength + ciphertext.Length + TagLength];
            Buffer.BlockCopy(iv, 0, result, 0, IvLength);
            Buffer.BlockCopy(ciphertext, 0, result, IvLength, ciphertext.Length);
            Buffer.BlockCopy(tag, 0, result, IvLength + ciphertext.Length, TagLength);
            return result;
        }

        public static byte[] Decrypt(byte[] encrypted, byte[] key)
        {
            if (encrypted.Length < IvLength + TagLength)
                throw new ArgumentException("Invalid encrypted data: too short");

            byte[] iv = new byte[IvLength];
            byte[] tag = new byte[TagLength];
            byte[] ciphertext = new byte[encrypted.Length - IvLength - TagLength];

            Buffer.BlockCopy(encrypted, 0, iv, 0, IvLength);
            Buffer.BlockCopy(encrypted, IvLength, ciphertext, 0, ciphertext.Length);
            Buffer.BlockCopy(encrypted, IvLength + ciphertext.Length, tag, 0, TagLength);

            byte[] plaintext = new byte[ciphertext.Length];
            using (AesGcm aes = new AesGcm(key))
            {
                aes.Decrypt(iv, ciphertext, tag, plaintext);
            }

            return plaintext;
        }

        public static byte[] DecodeBase64Key(string base64)
        {
            return Convert.FromBase64String(base64.Trim());
        }

        public static string EncodeBase64Key(byte[] raw)
        {
            return Convert.ToBase64String(raw);
        }

        public static string GenerateBase64Key()
        {
            using (Aes aes = Aes.Create())
            {
                aes.KeySize = 256;
                aes.GenerateKey();
                return EncodeBase64Key(aes.Key);
            }
        }
    }
}
