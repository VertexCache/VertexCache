using System.Threading.Tasks;

namespace VertexCache.Sdk.Core
{
    public interface IVCacheClient
    {
        Task<VCacheResult> RunCommandAsync(string command, string[] args);
    }
}
