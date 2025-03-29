using System;
using System.Threading.Tasks;
using VertexCache.Sdk;

namespace VertexCache.SdkClient
{
    public class CliRunner
    {
        private readonly IVCacheClient _client;
        private readonly VertexCacheSdkOptions _options;

        public CliRunner(IVCacheClient client, VertexCacheSdkOptions options)
        {
            _client = client;
            _options = options;
        }

        public async Task RunInteractiveAsync()
        {
            PrintStartupBanner();

            while (true)
            {
                Console.Write($"VertexCache Console, {_options.ServerHost}:{_options.ServerPort}> ");
                string? input = Console.ReadLine()?.Trim();

                if (string.IsNullOrEmpty(input)) continue;
                if (input.Equals("exit", StringComparison.OrdinalIgnoreCase)) break;

                var parts = input.Split(' ', StringSplitOptions.RemoveEmptyEntries);
                string command = parts[0];
                string[] args = parts.Length > 1 ? parts[1..] : Array.Empty<string>();

                var result = await _client.RunCommandAsync(command, args);
                Console.WriteLine(result.Raw);
            }
        }

        private void PrintStartupBanner()
        {
            Console.WriteLine("VertexCache Console:");
            Console.WriteLine("  Version: 1.0.0");
            Console.WriteLine($"  Host: {_options.ServerHost}");
            Console.WriteLine($"  Port: {_options.ServerPort}");
            Console.WriteLine($"  Message Layer Encryption Enabled: {(ToYesNo(_options.EnableEncryption))}");
            Console.WriteLine($"  Transport Layer Encryption Enabled: {(ToYesNo(_options.EnableEncryptionTransport))}");
            Console.WriteLine($"  Transport Layer Verify Certificate: {(ToYesNo(_options.EnableVerifyCertificate))}");
            Console.WriteLine($"  Config file set: Yes"); // TODO: Make this dynamic if needed
            Console.WriteLine($"  Config file loaded with no errors: Yes"); // TODO: Make this dynamic if needed
            Console.WriteLine($"  Config file location: ./vertex-cache-config/console/.env"); // TODO: Make this dynamic if needed
            Console.WriteLine("Status: OK, Console Client Started\n");
        }

        private static string ToYesNo(bool value) => value ? "Yes" : "No";
    }
}
