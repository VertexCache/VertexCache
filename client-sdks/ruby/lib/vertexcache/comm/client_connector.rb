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

require 'socket'
require 'openssl'
require 'stringio'
require 'vertexcache/comm/gcm_crypto_helper'
require 'vertexcache/comm/key_parser_helper'
require 'vertexcache/comm/message_codec'
require 'vertexcache/comm/socket_helper'
require 'vertexcache/model/vertex_cache_sdk_exception'

module VertexCache
  module Comm
    class ClientConnector
      attr_reader :connected

      def initialize(client_option)
        @options = client_option
        @stream = nil
        @connected = false
      end

      def connect
        @stream = if @options.enable_tls_encryption
                    SocketHelper.create_secure_socket(@options)
                  else
                    SocketHelper.create_socket_non_tls(@options)
                  end

        writer = @stream.to_io
        ident_command = @options.build_ident_command
        payload = encrypt_if_enabled(ident_command.encode('UTF-8'))
        MessageCodec.write_framed_message(writer, payload, resolve_protocol_version)
        writer.flush

        version, response_payload = MessageCodec.read_framed_message(@stream)
        raise VertexCache::Model::VertexCacheSdkException, 'Missing payload' if response_payload.nil?

        response = response_payload.force_encoding('UTF-8').strip
        unless response.start_with?('+OK')
          raise VertexCache::Model::VertexCacheSdkException, "Authorization failed: #{response}"
        end

        @connected = true
        true
      end

      def send(message)
        raise VertexCache::Model::VertexCacheSdkException, 'No active connection' unless connected?

        payload = encrypt_if_enabled(message.encode('UTF-8'))
        version = resolve_protocol_version

        writer = @stream.to_io
        MessageCodec.write_framed_message(writer, payload, version)
        writer.flush

        _version, response_payload = MessageCodec.read_framed_message(@stream)
        raise VertexCache::Model::VertexCacheSdkException, 'Missing payload' if response_payload.nil?

        response_payload.force_encoding('UTF-8')
      end

      def encrypt_if_enabled(plain)
        case @options.encryption_mode
        when VertexCache::Model::EncryptionMode::ASYMMETRIC
          pem = @options.public_key
          raise VertexCache::Model::VertexCacheSdkException, 'Missing public key' if pem.nil?

          KeyParserHelper.encrypt_with_rsa(pem, plain)
        when VertexCache::Model::EncryptionMode::SYMMETRIC
          key = @options.shared_encryption_key
          raise VertexCache::Model::VertexCacheSdkException, 'Missing shared encryption key' if key.nil?

          GcmCryptoHelper.encrypt(plain, key.bytes)
        else
          plain
        end
      rescue => e
        raise VertexCache::Model::VertexCacheSdkException, e.message
      end

      def resolve_protocol_version
        case @options.encryption_mode
        when VertexCache::Model::EncryptionMode::ASYMMETRIC
          MessageCodec::PROTOCOL_VERSION_RSA_PKCS1
        when VertexCache::Model::EncryptionMode::SYMMETRIC
          MessageCodec::PROTOCOL_VERSION_AES_GCM
        else
          MessageCodec::DEFAULT_PROTOCOL_VERSION
        end
      end

      def connected?
        @connected && !@stream.nil?
      end

      def close
        @stream.close if @stream
        @stream = nil
        @connected = false
      end
    end
  end
end
