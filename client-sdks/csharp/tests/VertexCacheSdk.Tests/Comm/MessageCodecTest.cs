// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// You may not use this file except in compliance with the License.
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
using System.Text;
using VertexCacheSdk.Comm;
using Xunit;

namespace VertexCacheSdk.Tests.Comm
{
    public class MessageCodecTest
    {
        [Fact]
        public void TestWriteThenReadFramedMessage()
        {
            string original = "Hello VertexCache";
            byte[] payload = Encoding.UTF8.GetBytes(original);

            using var output = new MemoryStream();
            MessageCodec.WriteFramedMessage(output, payload);

            using var input = new MemoryStream(output.ToArray());
            byte[] result = MessageCodec.ReadFramedMessage(input)!;

            Assert.NotNull(result);
            Assert.Equal(payload, result);
        }

        [Fact]
        public void TestInvalidVersionByte()
        {
            byte[] badFrame = new byte[8];
            byte[] len = BitConverter.GetBytes(3);
            if (BitConverter.IsLittleEndian) Array.Reverse(len);
            Array.Copy(len, 0, badFrame, 0, 4);
            badFrame[4] = 0x02;
            Array.Copy(Encoding.UTF8.GetBytes("abc"), 0, badFrame, 5, 3);

            using var input = new MemoryStream(badFrame);
            Assert.Throws<IOException>(() => MessageCodec.ReadFramedMessage(input));
        }

        [Fact]
        public void TestTooShortHeaderReturnsNull()
        {
            byte[] shortHeader = new byte[] { 0x01, 0x02 };
            using var input = new MemoryStream(shortHeader);
            var result = MessageCodec.ReadFramedMessage(input);
            Assert.Null(result);
        }

        [Fact]
        public void TestTooLargePayloadRejected()
        {
            byte[] bigPayload = new byte[MessageCodec.MaxMessageSize + 1];
            using var output = new MemoryStream();
            Assert.Throws<IOException>(() => MessageCodec.WriteFramedMessage(output, bigPayload));
        }

        [Fact]
        public void TestWriteEmptyPayloadThenReadShouldFail()
        {
            byte[] payload = Array.Empty<byte>();
            using var output = new MemoryStream();
            MessageCodec.WriteFramedMessage(output, payload);
            Assert.Equal(5, output.ToArray().Length);

            using var input = new MemoryStream(output.ToArray());
            Assert.Throws<IOException>(() => MessageCodec.ReadFramedMessage(input));
        }

        [Fact]
        public void TestUtf8MultibytePayload()
        {
            string original = "你好, VertexCache 🚀";
            byte[] payload = Encoding.UTF8.GetBytes(original);

            using var output = new MemoryStream();
            MessageCodec.WriteFramedMessage(output, payload);

            using var input = new MemoryStream(output.ToArray());
            var result = MessageCodec.ReadFramedMessage(input);
            string resultStr = Encoding.UTF8.GetString(result!);
            Assert.Equal(original, resultStr);
        }

        [Fact]
        public void TestHexDumpForInterSdkComparison()
        {
            byte[] payload = Encoding.UTF8.GetBytes("ping");
            using var output = new MemoryStream();
            MessageCodec.WriteFramedMessage(output, payload);

            string hex = BytesToHex(output.ToArray());
            Console.WriteLine("Framed hex: " + hex);
        }

        private string BytesToHex(byte[] bytes)
        {
            var sb = new StringBuilder();
            foreach (var b in bytes)
                sb.Append(b.ToString("X2"));
            return sb.ToString();
        }
    }
}
