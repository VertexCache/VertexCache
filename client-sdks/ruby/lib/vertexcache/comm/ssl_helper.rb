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

require 'openssl'
require 'tempfile'
require 'vertexcache/model/vertex_cache_sdk_exception'

module VertexCache
  module Comm
    class SSLHelper
      # Creates an SSLContext with certificate verification enabled using a PEM cert string.
      #
      # @param pem_cert [String] PEM-encoded certificate.
      # @return [OpenSSL::SSL::SSLContext]
      # @raise [VertexCache::Model::VertexCacheSdkException] if context creation fails.
      def self.create_verified_ssl_context(pem_cert)
        begin
          if pem_cert.nil? || !pem_cert.include?('BEGIN CERTIFICATE')
            raise ArgumentError, 'PEM certificate is empty or invalid'
          end

          file = Tempfile.new('cert')
          file.write(pem_cert)
          file.close

          ctx = OpenSSL::SSL::SSLContext.new
          ctx.ca_file = file.path
          ctx.verify_mode = OpenSSL::SSL::VERIFY_PEER
          return ctx
        rescue => _e
          raise VertexCache::Model::VertexCacheSdkException, 'Failed to create secure socket context'
        end
      end

      # Creates an insecure SSLContext that bypasses certificate validation.
      #
      # @return [OpenSSL::SSL::SSLContext]
      def self.create_insecure_ssl_context
        OpenSSL::SSL::SSLContext.new.tap do |ctx|
          ctx.verify_mode = OpenSSL::SSL::VERIFY_NONE
        end
      end
    end
  end
end
