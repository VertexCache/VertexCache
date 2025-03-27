using System;
using System.IO;

namespace VertexCache.Sdk.Helpers
{
    public static class PemLoader
    {
        public static string? LoadFromFileOrRaw(string? value, string? fallback = null)
        {
            if (string.IsNullOrWhiteSpace(value))
                return fallback;

            var trimmed = value.Trim();

            // Case 1: File path exists
            if (File.Exists(trimmed))
            {
                return File.ReadAllText(trimmed);
            }

            // Case 2: Single-line string with literal \n characters → convert to actual newlines
            if (trimmed.Contains("\\n"))
            {
                return trimmed.Replace("\\n", "\n");
            }

            // Case 3: Assume it's a valid raw multi-line PEM block already
            return trimmed;
        }
    }
}
