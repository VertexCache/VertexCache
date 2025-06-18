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
using System.Text;
using Xunit;
using VertexCacheSdk.Comm;
using VertexCacheSdk.Model;

namespace VertexCacheSdk.Tests.Comm
{
    public class ClientConnectorLiveTlsWithSymmetricKeyTest
    {
        private const string Host = "127.0.0.1";
        private const int Port = 50505;
        private const string ClientId = "sdk-client-csharp";
        private const string ClientToken = "78116067-0200-4838-8718-31ab574bfa5c";

        private const string SharedKey = "neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc=";

        private const string TlsCert = "-----BEGIN CERTIFICATE-----\\nMIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV\\nBAYTB1...";

        [Fact]
        public void TestLiveConnectAndPing_shouldSucceed()
        {
            if (Environment.GetEnvironmentVariable("VC_LIVE_TEST") != "true")
            {
                return; // skipped unless explicitly enabled
            }

            var opt = new ClientOption
            {
                ClientId = ClientId,
                ClientToken = ClientToken,
                ServerHost = Host,
                ServerPort = Port,
                EnableTlsEncryption = true,
                VerifyCertificate = false, // set to true if you trust the cert
                TlsCertificate = TlsCert,
                EncryptionMode = EncryptionMode.Symmetric,
                SharedEncryptionKey = SharedKey
            };

            var client = new ClientConnector(opt);
            client.Connect();
            Assert.True(client.IsConnected());

            var reply = client.Send("PING");
            Assert.NotNull(reply);
            Assert.StartsWith("+PONG", reply);

            client.Close();
            Assert.False(client.IsConnected());
        }
    }
}
