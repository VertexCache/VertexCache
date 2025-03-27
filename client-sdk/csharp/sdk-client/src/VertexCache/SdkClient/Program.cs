using System;
using System.Threading.Tasks;

namespace VertexCache.SdkClient
{
    class Program
    {
        static async Task Main(string[] args)
        {
            var client = new VertexCacheSdkClient();
            var cli = new CliRunner(client);
            await cli.RunInteractiveAsync();
        }
    }
}
