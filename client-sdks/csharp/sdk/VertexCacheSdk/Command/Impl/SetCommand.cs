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

using System.Text;
using VertexCacheSdk.Command;
using VertexCacheSdk.Model;

namespace VertexCacheSdk.Command.Impl
{
    /// <summary>
    /// Handles the SET command in VertexCache.
    ///
    /// Stores a value in the cache under the specified key, optionally assigning
    /// secondary (idx1) and tertiary (idx2) indexes for lookup. Existing keys will
    /// be overwritten. Supports expiration and format validation if configured.
    ///
    /// Requires the client to have WRITE or ADMIN access.
    ///
    /// Validation:
    /// - Key and value are required arguments.
    /// - Optional arguments may include index fields and TTL metadata.
    /// </summary>
    public class SetCommand : CommandBase<SetCommand>
    {
        private readonly string primaryKey;
        private readonly string value;
        private readonly string secondaryKey;
        private readonly string tertiaryKey;

        public SetCommand(string primaryKey, string value)
            : this(primaryKey, value, null, null) { }

        public SetCommand(string primaryKey, string value, string secondaryKey)
            : this(primaryKey, value, secondaryKey, null) { }

        public SetCommand(string primaryKey, string value, string secondaryKey, string tertiaryKey)
        {
            if (string.IsNullOrWhiteSpace(primaryKey))
            {
                throw new VertexCacheSdkException("Missing Primary Key");
            }

            if (string.IsNullOrWhiteSpace(value))
            {
                throw new VertexCacheSdkException("Missing Value");
            }

            this.primaryKey = primaryKey;
            this.value = value;
            this.secondaryKey = secondaryKey;
            this.tertiaryKey = tertiaryKey;
        }

        protected override string BuildCommand()
        {
            var sb = new StringBuilder();
            sb.Append(CommandType.SET).Append(COMMAND_SPACER)
              .Append(primaryKey).Append(COMMAND_SPACER)
              .Append(value);

            if (!string.IsNullOrWhiteSpace(secondaryKey))
            {
                sb.Append(" ").Append(CommandType.IDX1).Append(" ").Append(secondaryKey);
            }

            if (!string.IsNullOrWhiteSpace(tertiaryKey))
            {
                sb.Append(" ").Append(CommandType.IDX2).Append(" ").Append(tertiaryKey);
            }

            return sb.ToString();
        }

        protected override void ParseResponse(string responseBody)
        {
            if (!responseBody.Equals("OK", StringComparison.OrdinalIgnoreCase))
            {
                SetFailure("OK Not received");
            }
            else
            {
                SetSuccess();
            }
        }
    }
}
