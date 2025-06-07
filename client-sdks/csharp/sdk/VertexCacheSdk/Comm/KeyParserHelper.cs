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
using VertexCacheSdk.Model;

namespace VertexCacheSdk.Comm
{
    public static class KeyParserHelper
    {
        public static RSA ConfigPublicKeyIfEnabled(string publicKeyPem)
        {
            try
            {
                string cleaned = publicKeyPem
                    .Replace("-----BEGIN PUBLIC KEY-----", "")
                    .Replace("-----END PUBLIC KEY-----", "")
                    .Replace("\n", "")
                    .Replace("\r", "")
                    .Replace(" ", "");

                byte[] keyBytes = Convert.FromBase64String(cleaned);
                RSA rsa = RSA.Create();
                rsa.ImportSubjectPublicKeyInfo(keyBytes, out _);
                return rsa;
            }
            catch
            {
                throw new VertexCacheSdkException("Invalid public key");
            }
        }

        public static byte[] ConfigSharedKeyIfEnabled(string sharedEncryptionKey)
        {
            try
            {
                return Convert.FromBase64String(sharedEncryptionKey);
            }
            catch
            {
                throw new VertexCacheSdkException("Invalid shared key");
            }
        }
    }
}
