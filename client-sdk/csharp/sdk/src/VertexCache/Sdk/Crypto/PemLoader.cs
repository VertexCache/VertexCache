using System;
using System.IO;
using VertexCache.Sdk;

namespace VertexCache.Sdk.Crypto
{
    public static class PemLoader
    {
        public static string? LoadFromFileOrRaw(string? value, string? fallback = null)
        {
            if (string.IsNullOrWhiteSpace(value)) return fallback;

            var trimmed = value.Trim();

            if (File.Exists(trimmed))
                return File.ReadAllText(trimmed);

            if (trimmed.Contains("\\n"))
                return trimmed.Replace("\\n", "\n");

            return trimmed;
        }
    }
}
