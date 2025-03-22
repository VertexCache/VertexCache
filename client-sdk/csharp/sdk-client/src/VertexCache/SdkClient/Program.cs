using System.Threading.Tasks;

namespace VertexCache.SdkClient
{
    class Program
    {
        static async Task Main(string[] args)
        {
            var runner = new CliRunner();
            await runner.RunAsync();
        }
    }
}
