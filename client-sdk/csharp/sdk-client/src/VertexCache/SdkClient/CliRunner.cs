using System;
using System.Threading.Tasks;
using VertexCache.Sdk;

namespace VertexCache.SdkClient
{
    public class CliRunner
    {
        private readonly VertexCacheSdkClient _client;

        public CliRunner(VertexCacheSdkClient client)
        {
            _client = client;
        }

        public async Task RunInteractiveAsync()
        {
            Console.WriteLine("🧠 VertexCache Console - Interactive Mode");
            Console.WriteLine("Type 'exit' to quit.");

            while (true)
            {
                Console.Write("> ");
                var line = Console.ReadLine();

                if (string.IsNullOrWhiteSpace(line)) continue;
                if (line.Trim().ToLower() == "exit") break;

                var parts = line.Split(' ', StringSplitOptions.RemoveEmptyEntries);
                var command = parts[0]; // ✅ this was likely missing the semicolon
                var args = parts.Length > 1 ? parts[1..] : Array.Empty<string>();

                var result = await _client.RunCommandAsync(command, args);

                if (result.IsSuccess)
                {
                    Console.WriteLine($"✅ Server: {result.Message}");
                }
                else
                {
                    Console.WriteLine($"❌ Error: [{result.ErrorCode}] {result.Message}");
                }
            }
        }
    }
}
