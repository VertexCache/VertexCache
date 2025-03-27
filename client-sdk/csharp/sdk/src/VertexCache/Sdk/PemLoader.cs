using System;
using System.IO;

namespace VertexCache.Sdk.Helpers
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
