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
using System.IO;
using System.Net.Sockets;
using System.Net.Security;
using System.Threading.Tasks;
using VertexCacheSdk.Comm;
using VertexCacheSdk.Model;
using Xunit;

namespace VertexCacheSdk.Tests.Comm
{
    public class SSLHelperTest
    {
        private const bool ENABLE_LIVE_TLS_TESTS = false;

        // Test PEM — Do NOT use for real usage
        private const string VALID_PEM_CERT = @"
-----BEGIN CERTIFICATE-----
MIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV
BAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x
EDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv
Y2FsaG9zdDAeFw0yNTA1MTgwMzU2NDdaFw0zNTA1MTYwMzU2NDdaMG4xEDAOBgNV
BAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x
EDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv
Y2FsaG9zdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMHVT5HdQkUj
Ia3sYmLQUeOja7tKtAXi1cuhCLlrvgS2DKJa9cpkgi1dsKOjJmsTqo580e+jrpdQ
J+mTybdKoG6CZWEqfMizut48aTQoBteiLFSZ9J2/6nCXhxugA+aQ94lhkj3lJIHf
lIZeIYHaPNXH9/K4oCODJ8P6MfeQjY1ZWbrcQ9PxHQhWV/60AfTuJRJ4T/HQmOqM
6IcYz2t7iviIYvQq37A+wr1ClgxlfuT6JScEA8J34GivskB2p/MEn8E8y/durORz
aaF5RBpnsc+fzVwQuvkth993rnDemdrcvTF1bdF5t88Zt5FiPD4qDF+pKloHNMRQ
DXBYb9Wf/t8CAwEAAaMhMB8wHQYDVR0OBBYEFOYQaTvkoqgLjRhCYBMrwLqrVfJo
MA0GCSqGSIb3DQEBDAUAA4IBAQCVBHT1uqtm72g085JuWdjBoBDa6bJD3Wj3L+GH
JaKOF26wQmXtLV0KraH3t3SUxWOM865OcbOkIiSUjMIgqmmFh1quoF4NMBa0wye8
JguLk6Qpffd+YXfzddxi33jdCUWgyqcTKq7bfB5DbMP4U5yVxnlXwKB0dxkaEFSx
iAUrhcZ1+iYjelrERk8MPj9FQIzQ8FwwF4oB8ShNDhDNWCOVbSdLXwMOLH84u/ul
v/I4U/5/mqGGTtwNyyzFS0GYgrYua4H7Aqer2g4wv8PUYwkaAfQ49CWm9kFQxgD4
qwwA44GZv7zAa89WHNpbIMAA8keexZkPzJBIQNSKy2d9dhcP
-----END CERTIFICATE-----
";

        private const string INVALID_PEM_CERT = "-----BEGIN CERTIFICATE-----\nINVALID DATA\n-----END CERTIFICATE-----";

        //
        // --- Offline Tests: Don't hit live server ---
        //

        [Fact]
        public void CreateVerifiedSocketFactory_ShouldFailWithInvalidCert()
        {
            using var dummy = new MemoryStream();
            var ex = Assert.Throws<VertexCacheSdkException>(() =>
            {
                SSLHelper.CreateVerifiedSocketFactory(dummy, INVALID_PEM_CERT);
            });
            Assert.Equal("Failed to create secure socket connection", ex.Message);
        }

        [Fact]
        public void CreateVerifiedSocketFactory_ShouldFailWithEmptyCert()
        {
            using var dummy = new MemoryStream();
            var ex = Assert.Throws<VertexCacheSdkException>(() =>
            {
                SSLHelper.CreateVerifiedSocketFactory(dummy, "");
            });
            Assert.Equal("Failed to create secure socket connection", ex.Message);
        }

        [Fact]
        public void CreateVerifiedSocketFactory_ShouldFailWithRandomText()
        {
            using var dummy = new MemoryStream();
            var ex = Assert.Throws<VertexCacheSdkException>(() =>
            {
                SSLHelper.CreateVerifiedSocketFactory(dummy, "this is not even PEM");
            });
            Assert.Equal("Failed to create secure socket connection", ex.Message);
        }

        //
        // --- Live TLS Tests (only if server is running on localhost:50505) ---
        //

        [Fact]
        public void CreateVerifiedSocketFactory_ShouldSucceedWithValidCert()
        {
            if (!ENABLE_LIVE_TLS_TESTS) return;

            using var client = new TcpClient("localhost", 50505);
            var stream = client.GetStream();

            var sslStream = SSLHelper.CreateVerifiedSocketFactory(stream, VALID_PEM_CERT);

            Assert.NotNull(sslStream);
            Assert.True(sslStream.IsAuthenticated);
            sslStream.Close();
        }

        [Fact]
        public void CreateInsecureSocketFactory_ShouldSucceed()
        {
            if (!ENABLE_LIVE_TLS_TESTS) return;

            using var client = new TcpClient("localhost", 50505);
            var stream = client.GetStream();

            var sslStream = SSLHelper.CreateInsecureSocketFactory(stream);

            Assert.NotNull(sslStream);
            Assert.True(sslStream.IsAuthenticated);
            sslStream.Close();
        }
    }
}
