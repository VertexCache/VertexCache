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
using System.IO;
using System.Net.Sockets;
using System.Security.Cryptography;
using VertexCacheSdk.Model;

namespace VertexCacheSdk.Comm
{
    /// <summary>
    /// TcpClient is the core transport used by the VertexCache SDK to connect to and communicate with a VertexCache server.
    /// It manages socket creation, TLS negotiation, IDENT handshake, and encryption using AES-GCM or RSA.
    /// </summary>
    public class ClientConnector
    {
        private Stream _writer;
        private Stream _reader;
        private ClientOption _options;
        private bool _connected = false;
        private TcpClient _tcpClient;

        public ClientConnector(ClientOption options)
        {
            _options = options;
        }

        public void Connect()
        {
            try
            {
                if (_options.EnableTlsEncryption)
                {
                    var sslStream = SocketHelper.CreateSecureSocket(_options);
                    _writer = new BufferedStream(sslStream);
                    _reader = new BufferedStream(sslStream);
                }
                else
                {
                    var socket = SocketHelper.CreateSocketNonTLS(_options);
                    var stream = new NetworkStream(socket);
                    _writer = new BufferedStream(stream);
                    _reader = new BufferedStream(stream);
                }

                byte[] identPayload = EncryptIfEnabled(System.Text.Encoding.UTF8.GetBytes(_options.BuildIdentCommand()));
                MessageCodec.WriteFramedMessage(_writer, identPayload);
                _writer.Flush();

                byte[] identResponse = MessageCodec.ReadFramedMessage(_reader);
                string responseStr = identResponse == null ? "" : System.Text.Encoding.UTF8.GetString(identResponse).Trim();

                if (!responseStr.StartsWith("+OK"))
                {
                    throw new VertexCacheSdkException("Authorization failed: " + responseStr);
                }

                _connected = true;
            }
            catch (Exception ex)
            {
                throw new VertexCacheSdkException("Connection failed", ex);
            }
        }

        public string Send(string message)
        {
            lock (this)
            {
                try
                {
                    byte[] toSend = EncryptIfEnabled(System.Text.Encoding.UTF8.GetBytes(message));
                    MessageCodec.WriteFramedMessage(_writer, toSend);
                    _writer.Flush();

                    byte[] response = MessageCodec.ReadFramedMessage(_reader);
                    if (response == null)
                    {
                        throw new VertexCacheSdkException("Connection closed by server");
                    }

                    return System.Text.Encoding.UTF8.GetString(response);
                }
                catch (Exception ex)
                {
                    throw new VertexCacheSdkException("Unexpected failure during send", ex);
                }
            }
        }

        private byte[] EncryptIfEnabled(byte[] plainText)
        {
            try
            {
                switch (_options.EncryptionMode)
                {
                    case EncryptionMode.Asymmetric:
                        using (RSA rsa = KeyParserHelper.ConfigPublicKeyIfEnabled(_options.PublicKey))
                        {
                            return rsa.Encrypt(plainText, RSAEncryptionPadding.Pkcs1);
                        }

                    case EncryptionMode.Symmetric:
                        return GcmCryptoHelper.Encrypt(plainText, KeyParserHelper.ConfigSharedKeyIfEnabled(_options.SharedEncryptionKey));

                    case EncryptionMode.None:
                    default:
                        return plainText;
                }
            }
            catch (Exception ex)
            {
                throw new VertexCacheSdkException("Encryption failed for plaintext message", ex);
            }
        }

        public bool IsConnected()
        {
            return _connected;
        }

        public void Close()
        {
            try
            {
                _writer?.Close();
                _reader?.Close();
            }
            catch { }
            _connected = false;
        }
    }
}
