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
using System.Net;
using System.Net.Sockets;
using System.Net.Security;
using System.Threading;
using System.Threading.Tasks;
using VertexCacheSdk.Comm;
using VertexCacheSdk.Model;
using Xunit;

namespace VertexCacheSdk.Tests.Comm
{
    public class SocketHelperTest : IAsyncLifetime
    {
        private const int MOCK_PORT = 18888;
        private const int UNUSED_PORT = 65534;
        private const bool ENABLE_LIVE_TLS_TESTS = false;
        private Thread mockServerThread;
        private volatile bool serverRunning;

        private const string VALID_PEM_CERT = @"-----BEGIN CERTIFICATE-----
MIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV
... (snipped for brevity) ...
2d9dhcP
-----END CERTIFICATE-----";

        public Task InitializeAsync()
        {
            serverRunning = true;
            mockServerThread = new Thread(() =>
            {
                TcpListener listener = new TcpListener(IPAddress.Loopback, MOCK_PORT);
                listener.Start();

                while (serverRunning)
                {
                    try
                    {
                        if (listener.Pending())
                        {
                            var client = listener.AcceptTcpClient();
                            client.Close();
                        }
                        else
                        {
                            Thread.Sleep(10);
                        }
                    }
                    catch
                    {
                        // Ignore during shutdown
                    }
                }

                try { listener.Stop(); } catch { }
            });

            mockServerThread.IsBackground = true;
            mockServerThread.Start();
            return Task.CompletedTask;
        }

        public Task DisposeAsync()
        {
            serverRunning = false;

            // Unblock accept() if needed
            try { new TcpClient("localhost", MOCK_PORT).Close(); } catch { }

            if (mockServerThread != null && mockServerThread.IsAlive)
            {
                mockServerThread.Join(500); // wait up to 500ms for clean exit
            }

            return Task.CompletedTask;
        }

        [Fact]
        public void CreateSocketNonTLS_ShouldSucceed()
        {
            var option = new ClientOption
            {
                ServerHost = "localhost",
                ServerPort = MOCK_PORT,
                ConnectTimeout = 1000,
                ReadTimeout = 1000
            };

            var socket = SocketHelper.CreateSocketNonTLS(option);
            Assert.True(socket.Connected);
            socket.Close();
        }

        [Fact]
        public void CreateSocketNonTLS_ShouldFailIfPortClosed()
        {
            var option = new ClientOption
            {
                ServerHost = "localhost",
                ServerPort = UNUSED_PORT,
                ConnectTimeout = 500,
                ReadTimeout = 500
            };

            var ex = Assert.Throws<VertexCacheSdkException>(() =>
            {
                SocketHelper.CreateSocketNonTLS(option);
            });
            Assert.Equal("Failed to create Non Secure Socket", ex.Message);
        }

        [Fact]
        public void CreateSocketNonTLS_ShouldFailOnTimeout()
        {
            var option = new ClientOption
            {
                ServerHost = "10.255.255.1", // unroutable
                ServerPort = 12345,
                ConnectTimeout = 300,
                ReadTimeout = 500
            };

            var ex = Assert.Throws<VertexCacheSdkException>(() =>
            {
                SocketHelper.CreateSocketNonTLS(option);
            });
            Assert.Equal("Failed to create Non Secure Socket", ex.Message);
        }

        [Fact]
        public void CreateSecureSocket_ShouldFailDueToMissingTLSContext()
        {
            var option = new ClientOption
            {
                ServerHost = "localhost",
                ServerPort = MOCK_PORT,
                ConnectTimeout = 1000,
                ReadTimeout = 1000,
                VerifyCertificate = true,
                TlsCertificate = null
            };

            var ex = Assert.Throws<VertexCacheSdkException>(() =>
            {
                SocketHelper.CreateSecureSocket(option);
            });
            Assert.Equal("Failed to create Secure Socket", ex.Message);
        }

        [Fact]
        public void CreateSecureSocket_ShouldFailWithBadCertificate()
        {
            var option = new ClientOption
            {
                ServerHost = "localhost",
                ServerPort = MOCK_PORT,
                ConnectTimeout = 1000,
                ReadTimeout = 1000,
                VerifyCertificate = true,
                TlsCertificate = "not a cert"
            };

            var ex = Assert.Throws<VertexCacheSdkException>(() =>
            {
                SocketHelper.CreateSecureSocket(option);
            });
            Assert.Equal("Failed to create Secure Socket", ex.Message);
        }

        [Fact]
        public void CreateSecureSocket_ShouldSucceedWithLiveServer()
        {
            if (!ENABLE_LIVE_TLS_TESTS)
                return;

            var option = new ClientOption
            {
                ServerHost = "localhost",
                ServerPort = 50505,
                ConnectTimeout = 1000,
                ReadTimeout = 1000,
                VerifyCertificate = true,
                TlsCertificate = VALID_PEM_CERT
            };

            var stream = SocketHelper.CreateSecureSocket(option);
            Assert.NotNull(stream);
            Assert.True(stream.IsAuthenticated);
            stream.Close();
        }
    }
}
