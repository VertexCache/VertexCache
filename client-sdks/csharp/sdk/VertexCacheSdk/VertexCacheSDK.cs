// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
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
using VertexCacheSdk.Command.Impl;
using VertexCacheSdk.Model;

namespace VertexCacheSdk
{
    /// <summary>
    /// VertexCacheSDK serves as the main entry point for interacting with the VertexCache server.
    /// It provides methods to perform cache operations such as GET, SET, and DEL, and abstracts away
    /// the underlying TCP transport details.
    ///
    /// This SDK handles encryption (symmetric/asymmetric), TLS negotiation, authentication, and framing
    /// of commands and responses. Errors are surfaced through structured exceptions to aid client integration.
    /// </summary>
    public class VertexCacheSDK
    {
        private ClientConnector clientConnector;

        public VertexCacheSDK(ClientOption clientOption)
        {
            this.clientConnector = new ClientConnector(clientOption);
        }

        public void OpenConnection()
        {
            clientConnector.Connect();
        }

        public CommandResult Ping()
        {
            var cmd = new PingCommand().Execute(clientConnector) as PingCommand;
            return new CommandResult(cmd.IsSuccess(), cmd.GetStatusMessage());
        }

        public CommandResult Set(string key, string value)
        {
            var cmd = new SetCommand(key, value).Execute(clientConnector) as SetCommand;
            return new CommandResult(cmd.IsSuccess(), cmd.GetStatusMessage());
        }

        public CommandResult Set(string key, string value, string secondaryIndexKey)
        {
            var cmd = new SetCommand(key, value, secondaryIndexKey).Execute(clientConnector) as SetCommand;
            return new CommandResult(cmd.IsSuccess(), cmd.GetStatusMessage());
        }

        public CommandResult Set(string key, string value, string secondaryIndexKey, string tertiaryIndexKey)
        {
            var cmd = new SetCommand(key, value, secondaryIndexKey, tertiaryIndexKey).Execute(clientConnector) as SetCommand;
            return new CommandResult(cmd.IsSuccess(), cmd.GetStatusMessage());
        }

        public CommandResult Del(string key)
        {
            var cmd = new DelCommand(key).Execute(clientConnector) as DelCommand;
            return new CommandResult(cmd.IsSuccess(), cmd.GetStatusMessage());
        }

        public GetResult Get(string key)
        {
            var cmd = new GetCommand(key).Execute(clientConnector) as GetCommand;
            return new GetResult(cmd.IsSuccess(), cmd.GetStatusMessage(), cmd.GetValue());
        }

        public GetResult GetBySecondaryIndex(string key)
        {
            var cmd = new GetSecondaryIdxOneCommand(key).Execute(clientConnector) as GetSecondaryIdxOneCommand;
            return new GetResult(cmd.IsSuccess(), cmd.GetStatusMessage(), cmd.GetValue());
        }

        public GetResult GetByTertiaryIndex(string key)
        {
            var cmd = new GetSecondaryIdxTwoCommand(key).Execute(clientConnector) as GetSecondaryIdxTwoCommand;
            return new GetResult(cmd.IsSuccess(), cmd.GetStatusMessage(), cmd.GetValue());
        }

        public bool IsConnected()
        {
            return clientConnector.IsConnected();
        }

        public void Close()
        {
            clientConnector.Close();
        }
    }
}
