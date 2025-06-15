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

using VertexCacheSdk.Comm;

namespace VertexCacheSdk.Command
{
    /// <summary>
    /// CommandInterface represents a generic interface for all command types that can be executed by the VertexCache SDK.
    ///
    /// Implementations of this interface must define how a command is executed against the connector,
    /// and how results such as success status and message output are exposed.
    ///
    /// This abstraction allows polymorphic handling of different commands (e.g., GET, SET, DEL)
    /// in a unified way, enabling streamlined processing within the SDK's transport layer.
    /// </summary>
    public interface CommandInterface
    {
        CommandInterface Execute(ClientConnector client);
        bool IsSuccess();
        string GetResponse();
        string GetError();
        string GetStatusMessage();
    }
}
