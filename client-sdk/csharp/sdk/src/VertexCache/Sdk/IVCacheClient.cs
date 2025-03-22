using System.Threading.Tasks;

namespace VertexCache.Sdk
{
    public interface IVCacheClient
    {
        Task<VCacheResult> RunCommandAsync(string command, string[] args);
    }
}
