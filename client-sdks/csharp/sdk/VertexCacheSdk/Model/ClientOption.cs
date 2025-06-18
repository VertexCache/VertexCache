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

using System;

namespace VertexCacheSdk.Model
{
    /// <summary>
    /// Configuration container for initializing the VertexCache SDK client.
    ///
    /// This class holds all user-specified options required to establish a connection
    /// to a VertexCache server, including host, port, TLS settings, authentication tokens,
    /// encryption modes (asymmetric or symmetric), and related keys or certificates.
    ///
    /// It provides a flexible way to customize client behavior, including security preferences.
    /// </summary>
    public class ClientOption
    {
        public const string DefaultClientId = "sdk-client";
        public const string DefaultHost = "127.0.0.1";
        public const int DefaultPort = 50505;
        public const int DefaultReadTimeout = 3000;
        public const int DefaultConnectTimeout = 3000;

        private string _clientId = DefaultClientId;
        private string _clientToken = null;

        public string ServerHost { get; set; } = DefaultHost;
        public int ServerPort { get; set; } = DefaultPort;

        public bool EnableTlsEncryption { get; set; } = false;
        public string TlsCertificate { get; set; }
        public bool VerifyCertificate { get; set; } = false;

        public EncryptionMode EncryptionMode { get; set; } = EncryptionMode.None;
        public bool EncryptWithPublicKey { get; set; } = false;
        public bool EncryptWithSharedKey { get; set; } = false;

        public string PublicKey { get; set; }
        public string SharedEncryptionKey { get; set; }

        public int ReadTimeout { get; set; } = DefaultReadTimeout;
        public int ConnectTimeout { get; set; } = DefaultConnectTimeout;

        public string ClientId { get; set; } = string.Empty;
        public string ClientToken { get; set; } = string.Empty;

        /// <summary>
        /// Constructs the IDENT command used during client handshake.
        /// </summary>
        public string BuildIdentCommand()
        {
            return $"IDENT {{\"client_id\":\"{ClientId}\", \"token\":\"{ClientToken}\"}}";
        }
    }
}
