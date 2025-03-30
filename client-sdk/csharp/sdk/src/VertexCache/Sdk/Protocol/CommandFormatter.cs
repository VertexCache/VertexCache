namespace VertexCache.Sdk.Protocol
{
    public static class CommandFormatter
    {
        public static string Format(string command, string[] args)
        {
            return $"{command} {string.Join(' ', args)}".Trim();
        }
    }
}
