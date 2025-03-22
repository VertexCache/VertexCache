using System;

namespace VertexCache.SdkClient
{
    public static class CommandParser
    {
        public static (string command, string[] args) Parse(string input)
        {
            var parts = input.Trim().Split(' ', StringSplitOptions.RemoveEmptyEntries);
            if (parts.Length == 0) return ("", Array.Empty<string>());
            var command = parts[0].ToLowerInvariant();
            var args = parts.Length > 1 ? parts[1..] : Array.Empty<string>();
            return (command, args);
        }
    }
}
