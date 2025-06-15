// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ------------------------------------------------------------------------------

using VertexCacheSdk.Command;
using VertexCacheSdk.Model;

namespace VertexCacheSdk.Command.Impl
{
    /// <summary>
    /// Handles the GET command in VertexCache.
    ///
    /// Retrieves the value for a given key from the cache.
    /// Returns an error if the key is missing or expired.
    ///
    /// Requires the client to have READ, READ_WRITE, or ADMIN access.
    /// This command supports primary key lookups only.
    /// </summary>
    public class GetCommand : CommandBase<GetCommand>
    {
        private readonly string key;
        private string value;

        public GetCommand(string key)
        {
            if (string.IsNullOrWhiteSpace(key))
            {
                throw new VertexCacheSdkException("GET command requires a non-empty key");
            }
            this.key = key;
        }

        protected override string BuildCommand()
        {
            return "GET " + key;
        }

        protected override void ParseResponse(string responseBody)
        {
            if (responseBody.Equals("(nil)", StringComparison.OrdinalIgnoreCase))
            {
                SetSuccess("No matching key found, +(nil)");
                return;
            }

            if (responseBody.StartsWith("ERR"))
            {
                SetFailure("GET failed: " + responseBody);
            }
            else
            {
                value = responseBody;
            }
        }

        public string GetValue()
        {
            return value;
        }
    }
}
