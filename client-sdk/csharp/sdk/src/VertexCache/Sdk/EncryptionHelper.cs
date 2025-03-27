using System;
using System.Security.Cryptography;
using System.Text;

namespace VertexCache.Sdk
{
    public static class EncryptionHelper
    {
        public static string NormalizePublicKey(string key)
        {
            if (string.IsNullOrWhiteSpace(key)) return key;

            key = key.Trim();

            if (key.StartsWith("-----BEGIN PUBLIC KEY-----"))
                return key;

            // Strip any rogue headers
            key = key.Replace("-----BEGIN PUBLIC KEY-----", "")
                     .Replace("-----END PUBLIC KEY-----", "")
                     .Replace("\n", "")
                     .Replace("\r", "");

            var sb = new StringBuilder();
            sb.AppendLine("-----BEGIN PUBLIC KEY-----");

            for (int i = 0; i < key.Length; i += 64)
            {
                int len = Math.Min(64, key.Length - i);
                sb.AppendLine(key.Substring(i, len));
            }

            sb.AppendLine("-----END PUBLIC KEY-----");
            return sb.ToString();
        }

        public static string Encrypt(string data, string publicKeyPem)
        {
            if (string.IsNullOrWhiteSpace(data))
                throw new ArgumentException("Cannot encrypt null or empty data", nameof(data));

            if (string.IsNullOrWhiteSpace(publicKeyPem))
                throw new ArgumentException("Public key is missing", nameof(publicKeyPem));

            var dataBytes = Encoding.UTF8.GetBytes(data);
            if (dataBytes.Length > 245)
                throw new ArgumentException($"Data too long to encrypt with 2048-bit RSA. Length: {dataBytes.Length} bytes");

            using var rsa = RSA.Create();

            try
            {
                rsa.ImportFromPem(publicKeyPem.ToCharArray());
            }
            catch (Exception ex)
            {
                throw new ArgumentException("Failed to import PEM public key: " + ex.Message, nameof(publicKeyPem));
            }

            var encryptedBytes = rsa.Encrypt(dataBytes, RSAEncryptionPadding.Pkcs1);

            // Base64 encode the output for safe transport
            return Convert.ToBase64String(encryptedBytes);
        }
    }
}
