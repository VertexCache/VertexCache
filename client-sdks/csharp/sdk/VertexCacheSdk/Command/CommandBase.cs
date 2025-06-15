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

using System;
using VertexCacheSdk.Comm;
using VertexCacheSdk.Model;

namespace VertexCacheSdk.Command
{
    /// <summary>
    /// CommandBase defines the foundational structure for all client-issued commands in the VertexCache SDK.
    ///
    /// It encapsulates common metadata and behaviors shared by all command types, including:
    /// - Command type identification (e.g., GET, SET, DEL)
    /// - Internal tracking for retries and timestamps
    /// - Role-based authorization levels
    ///
    /// Subclasses should extend this class to implement specific command logic and payload formatting.
    ///
    /// This abstraction allows the SDK to handle commands in a consistent, extensible, and testable manner.
    /// </summary>
    public abstract class CommandBase<T> : CommandInterface where T : CommandBase<T>
    {
        private static readonly string RESPONSE_OK = "OK";
        protected static readonly string COMMAND_SPACER = " ";

        private bool success;
        private string response;
        private string error;

        public CommandInterface Execute(ClientConnector client)
        {
            try
            {
                string raw = client.Send(BuildCommand()).Trim();

                if (raw.StartsWith("+"))
                {
                    response = raw.Substring(1);
                    ParseResponse(response);
                    if (error == null)
                    {
                        success = true;
                    }
                }
                else if (raw.StartsWith("-"))
                {
                    success = false;
                    error = raw.Substring(1);
                }
                else
                {
                    success = false;
                    error = "Unexpected response: " + raw;
                }
            }
            catch (VertexCacheSdkException e)
            {
                success = false;
                error = e.Message;
            }

            return this;
        }

        protected abstract string BuildCommand();

        protected virtual void ParseResponse(string responseBody)
        {
            // Default: do nothing — override if needed
        }

        public void SetFailure(string response)
        {
            success = false;
            error = response;
        }

        public void SetSuccess()
        {
            success = true;
            response = RESPONSE_OK;
            error = null;
        }

        public void SetSuccess(string response)
        {
            success = true;
            this.response = response;
            error = null;
        }

        public string GetStatusMessage()
        {
            return IsSuccess() ? GetResponse() : GetError();
        }

        public bool IsSuccess()
        {
            return success;
        }

        public string GetResponse()
        {
            return response;
        }

        public string GetError()
        {
            return error;
        }
    }
}
