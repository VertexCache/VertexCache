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
require_relative 'ssl_helper'
require_relative 'read_write_stream'
require_relative '../model/vertex_cache_sdk_exception'

module VertexCache
  module Comm
    class SocketHelper
      def self.create_secure_socket(options)
        begin
          tcp_socket = create_tcp_socket(options)
          context = if options.verify_certificate
                      SSLHelper.create_verified_ssl_context(options.tls_certificate)
                    else
                      SSLHelper.create_insecure_ssl_context
                    end

          ssl_socket = OpenSSL::SSL::SSLSocket.new(tcp_socket, context)
          ssl_socket.sync_close = true
          ssl_socket.connect

          ReadWriteStream.new(ssl_socket)
        rescue => _
          raise VertexCache::Model::VertexCacheSdkException.new("Failed to create Secure Socket")
        end
      end

      def self.create_socket_non_tls(options)
        begin
          tcp_socket = create_tcp_socket(options)
          ReadWriteStream.new(tcp_socket)
        rescue => _
          raise VertexCache::Model::VertexCacheSdkException.new("Failed to create Non Secure Socket")
        end
      end

      private

      def self.create_tcp_socket(options)
        socket = TCPSocket.new(options.server_host, options.server_port)
        seconds = options.read_timeout.to_i
        timeval = [seconds, 0].pack("l_2")

        socket.setsockopt(Socket::SOL_SOCKET, Socket::SO_RCVTIMEO, timeval)
        socket.setsockopt(Socket::SOL_SOCKET, Socket::SO_SNDTIMEO, timeval)

        socket
      end
    end
  end
end
