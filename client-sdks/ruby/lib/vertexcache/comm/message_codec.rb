# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ------------------------------------------------------------------------------

module VertexCache
  module Comm
    class MessageCodec
      VERSION_BYTE = 0x01
      MAX_MESSAGE_SIZE = 1024 * 1024 # 1MB

      # Writes a framed message with a 4-byte length prefix and a version byte.
      # Format: [4-byte length][1-byte version][payload]
      def self.write_framed_message(io, payload)
        raise IOError, "Payload too large" if payload.bytesize > MAX_MESSAGE_SIZE

        total_length = payload.bytesize + 1
        header = [total_length].pack('N') + VERSION_BYTE.chr
        io.write(header)
        io.write(payload)
      end

      # Reads a framed message and returns the payload.
      # Raises IOError if the format is invalid or incomplete.
      def self.read_framed_message(io)
        header = io.read(5)
        return nil if header.nil? || header.bytesize < 5

        total_length = header[0, 4].unpack1('N')
        version = header.getbyte(4)

        raise IOError, "Invalid version byte" unless version == VERSION_BYTE
        raise IOError, "Invalid message length" if total_length <= 1 || total_length > MAX_MESSAGE_SIZE

        payload = io.read(total_length - 1, "")
        raise IOError, "Unexpected end of payload" if payload.nil? || payload.bytesize != total_length - 1

        payload
      end

      # Returns a hex dump of a framed message, useful for inter-SDK test comparison.
      def self.hex_dump(payload)
        io = StringIO.new
        write_framed_message(io, payload)
        io.rewind
        io.read.unpack1('H*')
      end
    end
  end
end
