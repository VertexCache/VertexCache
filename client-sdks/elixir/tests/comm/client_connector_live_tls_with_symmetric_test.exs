# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
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

defmodule VertexCacheSdk.Comm.ClientConnectorLiveTest do
  use ExUnit.Case

  alias VertexCacheSdk.Comm.ClientConnector
  alias VertexCacheSdk.Model.ClientOption
  alias VertexCacheSdk.Model.EncryptionMode

  @moduletag :live

  @host "127.0.0.1"
  @port 50505
  @client_id "sdk-client-elixir"
  @client_token "c4466a14-fd53-44a4-9dbb-730bb3b16274"
  @shared_key "neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc="
  @tls_cert System.get_env("VC_LIVE_TLS_SYMMETRIC_TEST") || ""

  test "live connect and send PING if VC_LIVE_TLS_SYMMETRIC_TEST=true" do
    if System.get_env("VC_LIVE_TLS_SYMMETRIC_TEST") != "true" do
      IO.puts("⚠️  Skipping live TLS test (disabled via VC_LIVE_TLS_SYMMETRIC_TEST)")
      return = :ok
      return
    end

    opts = %ClientOption{
      client_id: @client_id,
      client_token: @client_token,
      server_host: @host,
      server_port: @port,
      enable_tls_encryption: true,
      verify_certificate: false,  # change to true if cert is trusted
      tls_certificate: @tls_cert,
      encryption_mode: :symmetric,
      shared_encryption_key: @shared_key
    }

    case ClientConnector.connect(%{opts: opts}) do
      {:ok, state} ->
        assert ClientConnector.is_connected(state)

        case ClientConnector.send(state, "PING") do
          {:ok, reply} ->
            assert String.starts_with?(reply, "+PONG")
          {:error, err} ->
            flunk("Send failed: #{inspect(err)}")
        end

        ClientConnector.close(state)
        refute ClientConnector.is_connected(state)

      {:error, err} ->
        flunk("Connect failed: #{inspect(err)}")
    end
  end
end
