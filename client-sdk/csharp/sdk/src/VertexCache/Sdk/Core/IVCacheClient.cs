using System.Threading.Tasks;
using VertexCache.Sdk.Results;

namespace VertexCache.Sdk.Core
{
    public interface IVCacheClient
    {
        Task<VCacheResult> RunCommandAsync(string command, string[] args);
    }
}
