using VertexCache.Sdk.Core;

namespace VertexCache.SdkClient.ConsoleApp
{
    public static class ConsoleDisplay
    {
        public static void PrintWelcomeBanner(VertexCacheSdkOptions options)
        {
            System.Console.Clear();
            System.Console.WriteLine("VertexCache C# Client Console:");
            System.Console.WriteLine($"  Host: {options.ServerHost}");
            System.Console.WriteLine($"  Port: {options.ServerPort}");
            System.Console.WriteLine($"  Message Layer Encryption Enabled: {(options.EnableEncryption ? "Yes" : "No")}");
            System.Console.WriteLine($"  Transport Layer Encryption Enabled: {(options.EnableEncryptionTransport ? "Yes" : "No")}");
            System.Console.WriteLine($"  Transport Layer Verify Certificate: {(options.EnableVerifyCertificate ? "Yes" : "No")}");
            System.Console.WriteLine();
        }

        public static void PrintPrompt(VertexCacheSdkOptions options)
        {
            System.Console.Write($"VertexCache Console, {options.ServerHost}:{options.ServerPort}> ");
        }
    }
}
