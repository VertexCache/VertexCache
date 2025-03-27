using System;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using VertexCache.Sdk;

namespace VertexCache.SdkClient
{
    public class CliRunner
    {
        private readonly VCachePersistentClient _client;

        public CliRunner(VCachePersistentClient client)
        {
            _client = client;
        }

        public async Task RunInteractiveAsync()
        {
            Console.WriteLine("🧠 VertexCache Console - Persistent Mode");
            Console.WriteLine("Type 'exit' to quit.");

            var connectResult = await _client.ConnectAsync();
            if (!connectResult.IsSuccess)
            {
                Console.WriteLine($"❌ Connection failed: {connectResult.Message}");
                return;
            }

            Console.WriteLine("✅ Connected to VertexCache");

            while (true)
            {
                Console.Write("> ");
                var line = Console.ReadLine();

                if (string.IsNullOrWhiteSpace(line))
                    continue;

                var trimmed = line.Trim();

                if (trimmed.Equals("exit", StringComparison.OrdinalIgnoreCase))
                    break;

                var (command, args) = CommandParser.Parse(trimmed);

                if (string.IsNullOrWhiteSpace(command))
                {
                    Console.WriteLine("⚠️  Empty command. Try again.");
                    continue;
                }

                var result = await _client.SendCommandAsync(command, args);

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
