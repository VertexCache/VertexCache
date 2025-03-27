using System;
using System.Collections.Generic;
using System.IO;

namespace VertexCache.Sdk.Helpers
{
    public static class Env
    {
        static Env()
        {
            LoadEnvFile("config/.env");
        }

        private static void LoadEnvFile(string filePath)
        {
            if (!File.Exists(filePath))
                return;

            foreach (var line in File.ReadAllLines(filePath))
            {
                var trimmed = line.Trim();
                if (trimmed.StartsWith("#") || string.IsNullOrWhiteSpace(trimmed))
                    continue;

                var separatorIndex = trimmed.IndexOf('=');
                if (separatorIndex < 0)
                    continue;

                var key = trimmed.Substring(0, separatorIndex).Trim();
                var value = trimmed.Substring(separatorIndex + 1).Trim();

                // Remove surrounding quotes
                if (value.StartsWith("\"") && value.EndsWith("\""))
                {
                    value = value.Substring(1, value.Length - 2);
                    value = value.Replace("\\n", "\n");
                }

                Environment.SetEnvironmentVariable(key, value);
            }
        }

        public static string? GetString(string key, string? defaultValue = null)
        {
            var value = Environment.GetEnvironmentVariable(key);
            return string.IsNullOrWhiteSpace(value) ? defaultValue : value;
        }

        public static int GetInt(string key, int defaultValue = 0)
        {
            var value = GetString(key);
            return int.TryParse(value, out var result) ? result : defaultValue;
        }

        public static bool GetBool(string key, bool defaultValue = false)
        {
            var value = GetString(key);
            return bool.TryParse(value, out var result) ? result : defaultValue;
        }
    }
}
