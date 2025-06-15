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
    /// Handles the DEL command in VertexCache.
    ///
    /// Deletes a key and its associated value from the cache.
    /// If the system is configured to allow idempotent deletes,
    /// then attempting to delete a non-existent key will still
    /// return a success response ("OK DEL (noop)").
    ///
    /// Requires the client to have WRITE or ADMIN access.
    ///
    /// Configuration:
    /// - del_command_idempotent: when true, deletion of missing keys does not result in an error.
    /// </summary>
    public class DelCommand : CommandBase<DelCommand>
    {
        private readonly string key;

        public DelCommand(string key)
        {
            if (string.IsNullOrWhiteSpace(key))
            {
                throw new VertexCacheSdkException(CommandType.DEL + " command requires a non-empty key");
            }
            this.key = key;
        }

        protected override string BuildCommand()
        {
            return CommandType.DEL + " " + key;
        }

        protected override void ParseResponse(string responseBody)
        {
            if (!responseBody.Equals("OK", StringComparison.OrdinalIgnoreCase))
            {
                SetFailure("DEL failed: " + responseBody);
            }
        }
    }
}
