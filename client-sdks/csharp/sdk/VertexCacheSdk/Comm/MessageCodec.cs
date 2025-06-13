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

namespace VertexCacheSdk.Comm
{
    /// <summary>
    /// MessageCodec handles framing and deframing of messages transmitted over TCP.
    ///
    /// This utility class provides methods to:
    /// - <c>WriteFramedMessage(Stream, byte[])</c>: Prefixes the message with its 4-byte length and 4-byte protocol version.
    /// - <c>ReadFramedMessage(Stream)</c>: Reads the 4-byte length header and 4-byte protocol version, then the payload.
    ///
    /// This framing protocol ensures message boundaries are preserved across TCP transmissions,
    /// which is essential since TCP is a stream-oriented protocol with no built-in message demarcation.
    /// </summary>
    public static class MessageCodec
    {
        public const int MaxMessageSize = 10 * 1024 * 1024;

        /// <summary>
        /// Protocol version used for compatibility validation.
        /// </summary>
        public const int ProtocolVersion = 0x00000101;

        /// <summary>
        /// Writes a framed message to the given stream using the VertexCache protocol.
        ///
        /// Format:
        /// - 4 bytes: message length (big-endian)
        /// - 4 bytes: protocol version (big-endian)
        /// - N bytes: payload
        /// </summary>
        public static void WriteFramedMessage(Stream stream, byte[] data)
        {
            if (data.Length > MaxMessageSize)
                throw new IOException($"Message too large: {data.Length}");

            byte[] header = new byte[8];

            byte[] lengthBytes = BitConverter.GetBytes(data.Length);
            byte[] versionBytes = BitConverter.GetBytes(ProtocolVersion);

            if (BitConverter.IsLittleEndian)
            {
                Array.Reverse(lengthBytes);
                Array.Reverse(versionBytes);
            }

            Array.Copy(lengthBytes, 0, header, 0, 4);
            Array.Copy(versionBytes, 0, header, 4, 4);

            stream.Write(header, 0, 8);
            stream.Write(data, 0, data.Length);
        }

        /// <summary>
        /// Reads a framed message from the given stream.
        ///
        /// Format:
        /// - 4 bytes: message length (big-endian)
        /// - 4 bytes: protocol version (big-endian)
        /// - N bytes: payload
        /// </summary>
        public static byte[]? ReadFramedMessage(Stream stream)
        {
            byte[] header = new byte[8];
            int bytesRead = stream.Read(header, 0, 8);
            if (bytesRead < 8) return null;

            if (BitConverter.IsLittleEndian)
            {
                Array.Reverse(header, 0, 4); // length
                Array.Reverse(header, 4, 4); // version
            }

            int length = BitConverter.ToInt32(header, 0);
            int version = BitConverter.ToInt32(header, 4);

            if (version != ProtocolVersion)
                throw new IOException($"Unsupported protocol version: 0x{version:X8}");

            if (length <= 0 || length > MaxMessageSize)
                throw new IOException($"Invalid message length: {length}");

            byte[] buffer = new byte[length];
            int totalRead = 0;
            while (totalRead < length)
            {
                int read = stream.Read(buffer, totalRead, length - totalRead);
                if (read <= 0)
                    throw new IOException("Unexpected end of stream while reading payload");
                totalRead += read;
            }

            return buffer;
        }
    }
}
