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
    public class ClientConnectorLiveTlsTest
    {
        private const string Host = "127.0.0.1";
        private const int Port = 50505;
        private const string ClientId = "sdk-client-csharp";
        private const string ClientToken = "78116067-0200-4838-8718-31ab574bfa5c";

        private const string TestPublicKey = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\n" +
            "bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\n" +
            "UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\n" +
            "GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\n" +
            "NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n" +
            "6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\n" +
            "EwIDAQAB\n" +
            "-----END PUBLIC KEY-----";

        private const string TlsCert = "-----BEGIN CERTIFICATE-----\\nMIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV\\nBAYTB1...";

        [Fact]
        public void TestLiveConnectAndPing_shouldSucceed()
        {
            //if (Environment.GetEnvironmentVariable("VC_LIVE_TEST") != "true")
            //{
             //   return; // skipped unless explicitly enabled
            //}

            var opt = new ClientOption
            {
                ServerHost = Host,
                ServerPort = Port,
                EnableTlsEncryption = true,
                VerifyCertificate = false, // set to true if you trust the cert
                TlsCertificate = TlsCert,
                EncryptionMode = EncryptionMode.Asymmetric,
                PublicKey = TestPublicKey
            };

            opt.SetClientId(ClientId);
            opt.SetClientToken(ClientToken);

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
