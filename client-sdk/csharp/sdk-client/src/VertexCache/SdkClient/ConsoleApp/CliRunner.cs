using System;
using System.Threading.Tasks;
using VertexCache.Sdk.Results;
using VertexCache.SdkClient.Config;
using VertexCache.SdkClient.ConsoleApp;
using VertexCache.Sdk.Core;
using VertexCache.Sdk.Transport;

namespace VertexCache.SdkClient.ConsoleApp
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
            ConsoleDisplay.PrintWelcomeBanner(_options);

            while (true)
            {
                ConsoleDisplay.PrintPrompt(_options);

                string? input = System.Console.ReadLine();
                if (string.IsNullOrWhiteSpace(input)) continue;

                if (input.Trim().Equals("exit", StringComparison.OrdinalIgnoreCase))
                    break;

                var (cmd, args) = CommandParser.Parse(input);

                var result = await _client.RunCommandAsync(cmd, args);

                if (result.IsSuccess)
                {
                    System.Console.WriteLine(result.Message); // ✅ Print success message
                }
                else
                {
                    System.Console.WriteLine($"[ERROR] {result.Message}"); // ✅ Print error
                }
            }
        }
    }
}
