using System;
using System.Threading.Tasks;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using VertexCache.Sdk;
using DotNetEnv;

namespace VertexCache.SdkClient
{
    public class CliRunner
    {
        public async Task RunAsync()
        {
            var sdkClient = new VertexCacheSdkClient();

                while (true)
                {
                    Console.Write("Enter command (ping, get, set, del, exit): ");
                    string? input = Console.ReadLine();
                    if (string.IsNullOrWhiteSpace(input)) continue;

                    input = input.Trim();

                    if (input.Equals("exit", StringComparison.OrdinalIgnoreCase)) break;

                    var (command, args) = CommandParser.Parse(input);
                    var result = await sdkClient.RunCommandAsync(command, args);

                    if (result.Success)
                    {
                        Console.WriteLine($"✅ Success: {result.Response}");
                    }
                    else
                    {
                        Console.WriteLine($"❌ Error: [{result.ErrorCode}] {result.ErrorMessage}");
                    }
                }

        }
    }
}
