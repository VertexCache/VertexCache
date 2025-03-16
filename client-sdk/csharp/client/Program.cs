using System;
using VertexCache.Sdk;

namespace VertexCache.Client
{
    class Program
    {
        static void Main()
        {
            var sdk = new VertexCacheSdk();
            Console.WriteLine(sdk.GetMessage());
        }
    }
}
