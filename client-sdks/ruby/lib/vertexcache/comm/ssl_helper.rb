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
require 'vertexcache/model/vertex_cache_sdk_exception'

module VertexCache
  module Comm
    class SSLHelper
      # Modern secure cipher suites aligned with server and industry standards
      MODERN_CIPHERS = [
        "TLS_AES_128_GCM_SHA256",
        "TLS_AES_256_GCM_SHA384",
        "TLS_CHACHA20_POLY1305_SHA256",
        "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
        "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
        "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
        "TLS_RSA_WITH_AES_128_GCM_SHA256",
        "TLS_RSA_WITH_AES_256_GCM_SHA384"
      ].join(":")

      MODERN_PROTOCOLS = :TLS_CLIENT # Ruby 3+ uses symbolic constants

      def self.create_verified_ssl_context(pem_cert)
        raise_invalid_cert unless valid_cert_format?(pem_cert)

        cert = OpenSSL::X509::Certificate.new(pem_cert)
        store = OpenSSL::X509::Store.new
        store.add_cert(cert)

        ctx = OpenSSL::SSL::SSLContext.new(MODERN_PROTOCOLS)
        ctx.cert_store = store
        ctx.verify_mode = OpenSSL::SSL::VERIFY_PEER
        ctx.ciphers = MODERN_CIPHERS
        ctx.min_version = OpenSSL::SSL::TLS1_2_VERSION
        ctx.max_version = OpenSSL::SSL::TLS1_3_VERSION
        ctx
      rescue OpenSSL::X509::CertificateError
        raise VertexCache::Model::VertexCacheSdkException, 'Failed to parse PEM certificate'
      rescue => e
        raise VertexCache::Model::VertexCacheSdkException, 'Failed to build secure socket context'
      end

      def self.create_insecure_ssl_context
        ctx = OpenSSL::SSL::SSLContext.new(MODERN_PROTOCOLS)
        ctx.verify_mode = OpenSSL::SSL::VERIFY_NONE
        ctx.ciphers = MODERN_CIPHERS
        ctx.min_version = OpenSSL::SSL::TLS1_2_VERSION
        ctx.max_version = OpenSSL::SSL::TLS1_3_VERSION
        ctx
      end

      def self.valid_cert_format?(pem_cert)
        pem_cert && pem_cert.include?("BEGIN CERTIFICATE")
      end

      def self.raise_invalid_cert
        raise VertexCache::Model::VertexCacheSdkException, "Invalid certificate format"
      end
    end
  end
end
