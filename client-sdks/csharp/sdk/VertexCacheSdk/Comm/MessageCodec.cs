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

namespace VertexCacheSdk.Comm
{
    /// <summary>
    /// MessageCodec handles framing and deframing of messages transmitted over TCP.
    ///
    /// This utility class provides methods to:
    /// - <c>WriteFramedMessage(Stream, byte[])</c>: Prefixes the message with its 4-byte length and 1-byte protocol version.
    /// - <c>ReadFramedMessage(Stream)</c>: Reads the 4-byte length header and 1-byte protocol version, then the payload.
    ///
    /// This framing protocol ensures message boundaries are preserved across TCP transmissions,
    /// which is essential since TCP is a stream-oriented protocol with no built-in message demarcation.
    /// </summary>
    public static class MessageCodec
    {
        /// <summary>
        /// 10 MB max payload size to prevent abuse (e.g., media uploads).
        /// </summary>
        public const int MaxMessageSize = 10 * 1024 * 1024;

        /// <summary>
        /// Protocol version byte, used for future compatibility.
        /// </summary>
        public const byte ProtocolVersion = 0x01;

        /// <summary>
        /// Writes a framed message to the given stream using the VertexCache protocol.
        ///
        /// The message is framed as:
        /// - A 4-byte big-endian integer representing the length of the payload
        /// - A 1-byte protocol version
        /// - The message payload itself
        ///
        /// This method validates that the message size does not exceed the allowed maximum
        /// before writing the data to the stream.
        /// </summary>
        /// <param name="stream">The output stream to write to.</param>
        /// <param name="data">The payload to send.</param>
        /// <exception cref="IOException">If the payload is too large or a write error occurs.</exception>
        public static void WriteFramedMessage(Stream stream, byte[] data)
        {
            if (data.Length > MaxMessageSize)
                throw new IOException($"Message too large: {data.Length}");

            byte[] header = new byte[5];
            byte[] lengthBytes = BitConverter.GetBytes(data.Length);

            if (BitConverter.IsLittleEndian)
                Array.Reverse(lengthBytes); // Convert to big-endian

            Array.Copy(lengthBytes, 0, header, 0, 4);
            header[4] = ProtocolVersion;

            stream.Write(header, 0, 5);
            stream.Write(data, 0, data.Length);
        }

        /// <summary>
        /// Reads a framed message from the given stream according to the VertexCache protocol.
        ///
        /// The framing format consists of:
        /// - A 4-byte big-endian integer indicating the message length
        /// - A 1-byte protocol version
        /// - The message payload
        ///
        /// This method validates the protocol version and ensures the message length
        /// is within acceptable bounds before reading the payload.
        /// </summary>
        /// <param name="stream">The input stream to read from.</param>
        /// <returns>The payload as a byte array, or <c>null</c> if the header is incomplete.</returns>
        /// <exception cref="IOException">If the version is invalid, the length is out of bounds, or an I/O error occurs.</exception>
        public static byte[]? ReadFramedMessage(Stream stream)
        {
            byte[] header = new byte[5];
            int bytesRead = stream.Read(header, 0, 5);
            if (bytesRead < 5) return null;

            if (BitConverter.IsLittleEndian)
                Array.Reverse(header, 0, 4); // Convert from big-endian

            int length = BitConverter.ToInt32(header, 0);
            byte version = header[4];

            if (version != ProtocolVersion)
                throw new IOException($"Unsupported protocol version: {version}");

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
