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
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using VertexCacheSdk.Comm;
using VertexCacheSdk.Model;
using Xunit;

namespace VertexCacheSdk.Tests.Comm
{
    [CollectionDefinition("ClientConnectorTest")]
    public class ClientConnectorTestCollection : ICollectionFixture<ClientConnectorTestFixture> { }

    [Collection("ClientConnectorTest")]
    public class ClientConnectorTest : IDisposable
    {
        private const int Port = 19191;
        private const string TestSharedKey = "neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc=";
        private const string TestPublicKey = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\n" +
            "bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\n" +
            "UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\n" +
            "GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\n" +
            "NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n" +
            "6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\n" +
            "EwIDAQAB\n" +
            "-----END PUBLIC KEY-----";

        public void Dispose()
        {
            try
            {
                using var s = new System.Net.Sockets.TcpClient("127.0.0.1", Port); // unblock accept()
            }
            catch { }
        }

        [Fact]
        public void TestSymmetricEncryption_shouldSucceed()
        {
            var opt = new ClientOption
            {
                ServerHost = "127.0.0.1",
                ServerPort = Port,
                EncryptionMode = EncryptionMode.Symmetric,
                SharedEncryptionKey = TestSharedKey
            };
            opt.SetClientId("test-sym");

            var client = new ClientConnector(opt);
            client.Connect();
            Assert.True(client.IsConnected());

            var reply = client.Send("secure-msg");
            Assert.False(string.IsNullOrEmpty(reply));

            client.Close();
            Assert.False(client.IsConnected());
        }

        [Fact]
        public void TestInvalidSymmetricKey_shouldThrow()
        {
            var opt = new ClientOption
            {
                ServerHost = "127.0.0.1",
                ServerPort = Port,
                EncryptionMode = EncryptionMode.Symmetric
            };
            opt.SetClientId("bad-sym");

            var ex = Assert.Throws<VertexCacheSdkException>(() =>
            {
                opt.SharedEncryptionKey = "short";
                _ = KeyParserHelper.ConfigSharedKeyIfEnabled(opt.SharedEncryptionKey);
            });

            Assert.Contains("Invalid shared key", ex.Message);
        }

        [Fact]
        public void TestInvalidAsymmetricKey_shouldThrow()
        {
            var opt = new ClientOption
            {
                ServerHost = "127.0.0.1",
                ServerPort = Port,
                EncryptionMode = EncryptionMode.Asymmetric
            };
            opt.SetClientId("bad-asym");

            var ex = Assert.Throws<VertexCacheSdkException>(() =>
            {
                opt.PublicKey = TestPublicKey + "_BAD";
                _ = KeyParserHelper.ConfigPublicKeyIfEnabled(opt.PublicKey);
            });

            Assert.Contains("Invalid public key", ex.Message);
        }

        [Fact]
        public void TestConnectToWrongPort_shouldFailGracefully()
        {
            var opt = new ClientOption
            {
                ServerHost = "127.0.0.1",
                ServerPort = 65530,
                EncryptionMode = EncryptionMode.None
            };
            opt.SetClientId("wrong-port");

            var client = new ClientConnector(opt);
            var ex = Assert.Throws<VertexCacheSdkException>(() => client.Connect());
            Assert.False(string.IsNullOrEmpty(ex.Message));
        }

        [Fact]
        public void TestIdentHandshakeFailure_shouldThrow()
        {
            const int TempPort = 19292;
            var serverThread = new Thread(() =>
            {
                try
                {
                    var listener = new TcpListener(IPAddress.Loopback, TempPort);
                    listener.Start();

                    using var socket = listener.AcceptTcpClient();
                    using var stream = socket.GetStream();

                    _ = MessageCodec.ReadFramedMessage(stream); // read IDENT
                    MessageCodec.WriteFramedMessage(stream, Encoding.UTF8.GetBytes("-ERR Not authorized"));
                    stream.Flush();

                    listener.Stop();
                }
                catch { }
            });

            serverThread.Start();
            Thread.Sleep(100); // ensure server is ready

            var opt = new ClientOption
            {
                ServerHost = "127.0.0.1",
                ServerPort = TempPort,
                EncryptionMode = EncryptionMode.None
            };
            opt.SetClientId("fail-ident");

            var client = new ClientConnector(opt);
            var ex = Assert.Throws<VertexCacheSdkException>(() => client.Connect());

            // ✅ Assert generic failure message (Java-style)
            Assert.Contains("Connection failed", ex.Message);
        }

    }

    public class ClientConnectorTestFixture : IDisposable
    {
        private readonly Thread _echoThread;
        private volatile bool _running = true;

        public ClientConnectorTestFixture()
        {
            _echoThread = new Thread(() =>
            {
                var listener = new TcpListener(IPAddress.Loopback, 19191);
                listener.Start();

                while (_running)
                {
                    try
                    {
                        using var client = listener.AcceptTcpClient();
                        using var stream = new BufferedStream(client.GetStream());

                        var ident = MessageCodec.ReadFramedMessage(stream);
                        if (ident == null) continue;

                        MessageCodec.WriteFramedMessage(stream, Encoding.UTF8.GetBytes("+OK IDENT ACK"));
                        stream.Flush();

                        while (true)
                        {
                            var msg = MessageCodec.ReadFramedMessage(stream);
                            if (msg == null) break;

                            var response = Encoding.UTF8.GetBytes("echo:" + Encoding.UTF8.GetString(msg));
                            MessageCodec.WriteFramedMessage(stream, response);
                            stream.Flush();
                        }
                    }
                    catch { }
                }

                listener.Stop();
            });

            _echoThread.Start();
        }

        public void Dispose()
        {
            _running = false;
        }
    }
}
