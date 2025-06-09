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

defmodule VertexCacheSdk.Comm.SocketHelper do
  alias VertexCacheSdk.Model.ClientOption
  alias VertexCacheSdk.Model.VertexCacheSdkException
  alias VertexCacheSdk.Comm.SSLHelper

  @doc """
  Creates a non-TLS (plain) TCP socket connection.
  """
  def create_socket_non_tls(%ClientOption{} = opts) do
    connect_opts = [
      :binary,
      active: false,
      packet: :raw
    ]

    case :gen_tcp.connect(
           String.to_charlist(opts.server_host),
           opts.server_port,
           connect_opts,
           opts.connect_timeout
         ) do
      {:ok, socket} ->
        :inet.setopts(socket, [{:recv_timeout, opts.read_timeout}])
        {:ok, socket}

      _ ->
        {:error, %VertexCacheSdkException{message: "Failed to create Non Secure Socket"}}
    end
  end

  @doc """
  Creates a TLS socket using provided options.
  Delegates TLS config to SSLHelper.
  """
  def create_secure_socket(%ClientOption{} = opts) do
    ssl_opts_result =
      if opts.verify_certificate and opts.tls_certificate != nil do
        SSLHelper.create_verified_socket_opts(opts.tls_certificate)
      else
        {:ok, SSLHelper.create_insecure_socket_opts()}
      end

    case ssl_opts_result do
      {:ok, base_opts} ->
        merged_opts =
          Keyword.merge(base_opts,
            active: false,
            binary: true
          )

        case :ssl.connect(
               String.to_charlist(opts.server_host),
               opts.server_port,
               merged_opts,
               opts.connect_timeout
             ) do
          {:ok, ssl_socket} ->
            :ssl.setopts(ssl_socket, [{:recv_timeout, opts.read_timeout}])
            {:ok, ssl_socket}

          _ ->
            {:error, %VertexCacheSdkException{message: "Failed to create Secure Socket"}}
        end

      {:error, _reason} ->
        {:error, %VertexCacheSdkException{message: "Failed to create Secure Socket"}}
    end
  end
end
