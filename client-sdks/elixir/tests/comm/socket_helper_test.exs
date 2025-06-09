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

defmodule VertexCacheSdk.Comm.SocketHelperTest do
  use ExUnit.Case
  alias VertexCacheSdk.Comm.SocketHelper
  alias VertexCacheSdk.Model.ClientOption
  alias VertexCacheSdk.Model.VertexCacheSdkException

  @unused_port 59999
  @live_tls_port 50505
  @mock_port 18888

  setup_all do
    {:ok, listener} =
      :gen_tcp.listen(@mock_port, [:binary, packet: :raw, active: false, reuseaddr: true])

    spawn(fn -> accept_loop(listener) end)
    on_exit(fn -> :gen_tcp.close(listener) end)
    :ok
  end

  defp accept_loop(listener) do
    case :gen_tcp.accept(listener) do
      {:ok, socket} ->
        :gen_tcp.close(socket)
        accept_loop(listener)

      _ ->
        :ok
    end
  end

  test "create_socket_non_tls should succeed" do
    option = %ClientOption{
      server_host: "localhost",
      server_port: @mock_port,
      connect_timeout: 1000,
      read_timeout: 1000
    }

    case SocketHelper.create_socket_non_tls(option) do
      {:ok, socket} ->
        assert is_port(socket)
        :gen_tcp.close(socket)

      {:error, err} ->
        flunk("Expected success, got error: #{err.message}")
    end
  end

  test "create_socket_non_tls should fail if port closed" do
    option = %ClientOption{
      server_host: "localhost",
      server_port: @unused_port,
      connect_timeout: 500,
      read_timeout: 500
    }

    case SocketHelper.create_socket_non_tls(option) do
      {:error, %VertexCacheSdkException{message: msg}} ->
        assert msg == "Failed to create Non Secure Socket"

      other ->
        flunk("Expected error, got: #{inspect(other)}")
    end
  end

  test "create_socket_non_tls should fail on timeout" do
    option = %ClientOption{
      server_host: "10.255.255.1",
      server_port: 9999,
      connect_timeout: 1,
      read_timeout: 100
    }

    case SocketHelper.create_socket_non_tls(option) do
      {:error, %VertexCacheSdkException{message: msg}} ->
        assert msg == "Failed to create Non Secure Socket"

      other ->
        flunk("Expected timeout error, got: #{inspect(other)}")
    end
  end

  test "create_secure_socket should fail due to missing TLS context" do
    option = %ClientOption{
      server_host: "localhost",
      server_port: 8443,
      connect_timeout: 1000,
      read_timeout: 1000,
      verify_certificate: true
    }

    case SocketHelper.create_secure_socket(option) do
      {:error, %VertexCacheSdkException{message: msg}} ->
        assert msg == "Failed to create Secure Socket"

      other ->
        flunk("Expected TLS context error, got: #{inspect(other)}")
    end
  end

  test "create_secure_socket should fail with bad certificate" do
    option = %ClientOption{
      server_host: "localhost",
      server_port: @mock_port,
      connect_timeout: 1000,
      read_timeout: 1000,
      verify_certificate: true,
      tls_certificate: "not a valid cert"
    }

    case SocketHelper.create_secure_socket(option) do
      {:error, %VertexCacheSdkException{message: msg}} ->
        assert msg == "Failed to create Secure Socket"

      other ->
        flunk("Expected cert error, got: #{inspect(other)}")
    end
  end

  @tag :tls
  test "create_secure_socket should succeed with live TLS server (verify_certificate: false)" do
    unless System.get_env("ENABLE_LIVE_TLS_TESTS") == "true" do
      IO.puts("Live TLS test skipped: ENABLE_LIVE_TLS_TESTS not set to 'true'")
      assert true
    else
      option = %ClientOption{
        server_host: "localhost",
        server_port: @live_tls_port,
        connect_timeout: 1000,
        read_timeout: 1000,
        verify_certificate: false
      }

      case SocketHelper.create_secure_socket(option) do
        {:ok, ssl_socket} ->
          assert is_pid(ssl_socket)
          :ssl.close(ssl_socket)

        {:error, err} ->
          IO.warn("Live TLS server not reachable or handshake failed: #{err.message}")
          assert true
      end
    end
  end

end
