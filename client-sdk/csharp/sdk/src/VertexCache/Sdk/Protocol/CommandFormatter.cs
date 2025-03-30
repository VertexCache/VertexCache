using System;

namespace VertexCache.Sdk.Protocol
{
    public static class CommandFormatter
    {
        public static string Format(string command, string[] args)
        {
            if (args == null || args.Length == 0)
                return command;

            return $"{command} {string.Join(" ", args)}";
        }
    }
}
