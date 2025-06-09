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
using System.Net.Sockets;
using System.Net.Security;
using VertexCacheSdk.Model;

namespace VertexCacheSdk.Comm
{
    public static class SocketHelper
    {
        public static SslStream CreateSecureSocket(ClientOption options)
        {
            try
            {
                var tcpClient = new TcpClient();
                tcpClient.Connect(options.ServerHost, options.ServerPort);
                tcpClient.ReceiveTimeout = options.ReadTimeout;
                tcpClient.SendTimeout = options.ReadTimeout;

                var stream = tcpClient.GetStream();

                return options.VerifyCertificate
                    ? SSLHelper.CreateVerifiedSocketFactory(stream, options.TlsCertificate)
                    : SSLHelper.CreateInsecureSocketFactory(stream);
            }
            catch (Exception)
            {
                throw new VertexCacheSdkException("Failed to create Secure Socket");
            }
        }

        public static Socket CreateSocketNonTLS(ClientOption options)
        {
            try
            {
                var socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                socket.Connect(options.ServerHost, options.ServerPort);
                socket.ReceiveTimeout = options.ReadTimeout;
                socket.SendTimeout = options.ReadTimeout;
                return socket;
            }
            catch (Exception)
            {
                throw new VertexCacheSdkException("Failed to create Non Secure Socket");
            }
        }
    }
}
