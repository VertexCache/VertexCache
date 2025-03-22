using System.Security.Cryptography;
using System.Text;

namespace VertexCache.Sdk
{
    public static class EncryptionHelper
    {
        public static string Encrypt(string plainText, string publicKey)
        {
            using var rsa = RSA.Create();
            rsa.ImportFromPem(publicKey.ToCharArray());
            var bytesToEncrypt = Encoding.UTF8.GetBytes(plainText);
            var encryptedBytes = rsa.Encrypt(bytesToEncrypt, RSAEncryptionPadding.Pkcs1);
            return Convert.ToBase64String(encryptedBytes);
        }
    }
}
