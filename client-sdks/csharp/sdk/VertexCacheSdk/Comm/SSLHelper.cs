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
using System.Net.Security;
using System.Security.Authentication;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using VertexCacheSdk.Model;

namespace VertexCacheSdk.Comm
{
    public static class SSLHelper
    {
        /// <summary>
        /// Creates a secure SslStream with certificate validation against the provided PEM certificate.
        /// </summary>
        public static SslStream CreateVerifiedSocketFactory(Stream networkStream, string pemCert)
        {
            try
            {
                var cert = LoadCertificateFromPem(pemCert);
                var sslStream = new SslStream(
                    networkStream,
                    leaveInnerStreamOpen: false,
                    (sender, certificate, chain, sslPolicyErrors) =>
                    {
                        return certificate != null && certificate.GetCertHashString() == cert.GetCertHashString();
                    });

                sslStream.AuthenticateAsClient("localhost", null, SslProtocols.Tls12 | SslProtocols.Tls13, false);
                return sslStream;
            }
            catch (Exception)
            {
                throw new VertexCacheSdkException("Failed to create secure socket connection");
            }
        }

        /// <summary>
        /// Creates a secure SslStream that skips certificate validation (insecure).
        /// </summary>
        public static SslStream CreateInsecureSocketFactory(Stream networkStream)
        {
            try
            {
                var sslStream = new SslStream(
                    networkStream,
                    leaveInnerStreamOpen: false,
                    (sender, certificate, chain, sslPolicyErrors) => true);

                sslStream.AuthenticateAsClient("localhost", null, SslProtocols.Tls12 | SslProtocols.Tls13, false);
                return sslStream;
            }
            catch (Exception)
            {
                throw new VertexCacheSdkException("Failed to create secure socket connection");
            }
        }

        private static X509Certificate2 LoadCertificateFromPem(string pem)
        {
            try
            {
                var pemLines = pem.Split('\n');
                var sb = new StringBuilder();
                bool inCert = false;

                foreach (var line in pemLines)
                {
                    if (line.Contains("BEGIN CERTIFICATE"))
                    {
                        inCert = true;
                        continue;
                    }
                    else if (line.Contains("END CERTIFICATE"))
                    {
                        break;
                    }

                    if (inCert)
                        sb.Append(line.Trim());
                }

                var rawData = Convert.FromBase64String(sb.ToString());
                return new X509Certificate2(rawData);
            }
            catch (Exception)
            {
                throw new VertexCacheSdkException("Failed to create secure socket connection");
            }
        }
    }
}
