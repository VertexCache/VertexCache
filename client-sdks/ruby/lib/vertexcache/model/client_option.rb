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

require_relative 'encryption_mode'
require 'vertexcache/comm/message_codec'
require 'vertexcache/comm/key_parser_helper'
require 'vertexcache/model/vertex_cache_sdk_exception'

module VertexCache
  module Model
    class ClientOption
      DEFAULT_CLIENT_ID = 'sdk-client'
      DEFAULT_HOST = '127.0.0.1'
      DEFAULT_PORT = 50505
      DEFAULT_READ_TIMEOUT = 3000
      DEFAULT_CONNECT_TIMEOUT = 3000

      attr_accessor :client_id,
                    :client_token,
                    :server_host,
                    :server_port,
                    :enable_tls_encryption,
                    :tls_certificate,
                    :verify_certificate,
                    :encryption_mode,
                    :encrypt_with_public_key,
                    :encrypt_with_shared_key,
                    :public_key,
                    :shared_encryption_key,
                    :read_timeout,
                    :connect_timeout

      def initialize
        @client_id = DEFAULT_CLIENT_ID
        @client_token = nil
        @server_host = DEFAULT_HOST
        @server_port = DEFAULT_PORT
        @enable_tls_encryption = false
        @tls_certificate = nil
        @verify_certificate = false
        @encryption_mode = EncryptionMode::NONE
        @encrypt_with_public_key = false
        @encrypt_with_shared_key = false
        @public_key = nil
        @shared_encryption_key = nil
        @read_timeout = DEFAULT_READ_TIMEOUT
        @connect_timeout = DEFAULT_CONNECT_TIMEOUT
      end

      def get_client_id
        @client_id.nil? ? '' : @client_id
      end

      def get_client_token
        @client_token.nil? ? '' : @client_token
      end

      def build_ident_command
        "IDENT {\"client_id\":\"#{get_client_id}\", \"token\":\"#{get_client_token}\"}"
      end

      def resolve_protocol_version
        case @encryption_mode
        when EncryptionMode::ASYMMETRIC
          VertexCache::Comm::MessageCodec::PROTOCOL_VERSION_RSA_PKCS1
        when EncryptionMode::SYMMETRIC
          VertexCache::Comm::MessageCodec::PROTOCOL_VERSION_AES_GCM
        else
          VertexCache::Comm::MessageCodec::DEFAULT_PROTOCOL_VERSION
        end
      end

      def public_key_as_object
        raise VertexCache::Model::VertexCacheSdkException.new('Missing public key for asymmetric encryption') if @public_key.nil?

        begin
          OpenSSL::PKey.read(Base64.decode64(@public_key))
        rescue
          raise VertexCache::Model::VertexCacheSdkException.new('Failed to parse RSA public key from PEM')
        end
      end

      def shared_encryption_key_as_bytes
        raise VertexCache::Model::VertexCacheSdkException.new('Missing shared encryption key for symmetric mode') if @shared_encryption_key.nil?

        begin
          Base64.strict_decode64(@shared_encryption_key)
        rescue
          raise VertexCache::Model::VertexCacheSdkException.new('Invalid shared key format')
        end
      end
    end
  end
end
