# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# You may not use this file except in compliance with the License.
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

require 'base64'
require 'openssl'
require 'vertexcache/model/vertex_cache_sdk_exception'


module VertexCache
  module Comm
    class KeyParserHelper
      def self.config_public_key_if_enabled(pem_string)
        begin
          cleaned = pem_string.gsub(/-----BEGIN PUBLIC KEY-----/, '')
                              .gsub(/-----END PUBLIC KEY-----/, '')
                              .gsub(/\s+/, '')
          decoded = Base64.strict_decode64(cleaned)
          OpenSSL::PKey.read(decoded)
          decoded
        rescue
          raise VertexCache::Model::VertexCacheSdkException.new('Invalid public key')
        end
      end

      def self.config_shared_key_if_enabled(base64_string)
        begin
          decoded = Base64.strict_decode64(base64_string)
          if Base64.strict_encode64(decoded) != base64_string.gsub(/\s+/, '')
            raise VertexCache::Model::VertexCacheSdkException.new('Invalid shared key')
          end
          decoded
        rescue
          raise VertexCache::Model::VertexCacheSdkException.new('Invalid shared key')
        end
      end
    end
  end
end
