# frozen_string_literal: true

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

require 'stringio'
require 'vertexcache/model/vertex_cache_sdk_exception'

module VertexCache
  module Comm
    class MessageCodec
      HEADER_LEN = 8
      MAX_PAYLOAD_SIZE = 10 * 1024 * 1024
      MAX_MESSAGE_SIZE = 4 * 1024 * 1024

      PROTOCOL_VERSION_RSA_PKCS1 = 0x00000101
      PROTOCOL_VERSION_AES_GCM   = 0x00000181
      DEFAULT_PROTOCOL_VERSION   = 0x00000001

      def self.write_framed_message(io, payload, version)
        raise VertexCache::Model::VertexCacheSdkException, 'Payload must be non-empty' if payload.nil? || payload.empty?
        raise VertexCache::Model::VertexCacheSdkException, 'Payload too large' if payload.bytesize > MAX_MESSAGE_SIZE

        length_prefix = [payload.bytesize].pack('N')     # 4 bytes big-endian
        version_bytes = [version].pack('N')              # 4 bytes big-endian
        io.write(length_prefix + version_bytes + payload)
        io.flush
      end

      def self.read_framed_message(io)
        header = io.read(HEADER_LEN)
        return nil if header.nil? || header.bytesize < HEADER_LEN

        length = header[0, 4].unpack1('N')
        version = header[4, 4].unpack1('N')

        raise VertexCache::Model::VertexCacheSdkException, 'Invalid message length' if length <= 0 || length > MAX_PAYLOAD_SIZE

        payload = io.read(length)
        raise VertexCache::Model::VertexCacheSdkException, 'Unexpected end of payload' if payload.nil? || payload.bytesize != length

        [version, payload]
      end

      def self.hex_dump(payload, version)
        io = StringIO.new
        write_framed_message(io, payload, version)
        io.rewind
        io.read.unpack1('H*').upcase
      end
    end
  end
end
