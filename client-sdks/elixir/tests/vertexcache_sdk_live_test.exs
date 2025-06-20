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

if System.get_env("VC_LIVE_TLS_ASYMMETRIC_TEST") == "true" do
  defmodule VertexCacheSdkLiveTest do
    use ExUnit.Case, async: false
    alias VertexCacheSdk
    alias VertexCacheSdk.Model.{ClientOption, EncryptionMode, CommandResult, GetResult, VertexCacheSdkException}

    @moduletag :live

    @client_id "sdk-client-elixir"
    @client_token "c4466a14-fd53-44a4-9dbb-730bb3b16274"
    @host "localhost"
    @port 50505
    @cert System.get_env("VC_LIVE_TLS_CERT") || ""
    @pubkey """
    -----BEGIN PUBLIC KEY-----
    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
    bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
    UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
    GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
    NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
    6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
    EwIDAQAB
    -----END PUBLIC KEY-----
    """

    setup_all do
      opts =
        %ClientOption{}
        |> Map.put(:client_id, @client_id)
        |> Map.put(:client_token, @client_token)
        |> Map.put(:server_host, @host)
        |> Map.put(:server_port, @port)
        |> Map.put(:enable_tls_encryption, true)
        |> Map.put(:tls_certificate, @cert)
        |> Map.put(:encryption_mode, :asymmetric)
        |> Map.put(:public_key, @pubkey)

      result = VertexCacheSdk.open_connection(opts)

      cond do
        match?({:ok, _}, result) ->
          {:ok, sdk} = result
          on_exit(fn -> VertexCacheSdk.close(sdk) end)
          {:ok, sdk: sdk}

        match?(%VertexCacheSdk{}, result) ->
          sdk = result
          on_exit(fn -> VertexCacheSdk.close(sdk) end)
          {:ok, sdk: sdk}

        match?({:error, %VertexCacheSdkException{}}, result) ->
          {:error, %VertexCacheSdkException{message: msg}} = result
          IO.puts("[FATAL] Failed to open connection: #{msg}")
          :error
      end
    end


    @tag :live
    test "ping should succeed", %{sdk: sdk} do
      result = VertexCacheSdk.ping(sdk)
      assert result.success
    end

    @tag :live
    test "02: set should succeed", %{sdk: sdk} do
      result = VertexCacheSdk.set(sdk, "test-key", "value-123")
      assert result.success
      assert result.status_message == "OK"
    end

    @tag :live
    test "03: get should return previously set value", %{sdk: sdk} do
      VertexCacheSdk.set(sdk, "test-key", "value-123")
      result = VertexCacheSdk.get(sdk, "test-key")
      assert result.success
      assert result.value == "value-123"
    end

    @tag :live
    test "04: del should succeed and remove key", %{sdk: sdk} do
      VertexCacheSdk.set(sdk, "delete-key", "to-be-deleted")
      del_result = VertexCacheSdk.del(sdk, "delete-key")
      assert del_result.success

      get_result = VertexCacheSdk.get(sdk, "delete-key")
      assert get_result.success
      assert is_nil(get_result.value)
    end

    @tag :live
    test "05: get on missing key should succeed and return nil", %{sdk: sdk} do
      result = VertexCacheSdk.get(sdk, "nonexistent-key")
      assert result.success
      assert is_nil(result.value)
    end

    @tag :live
    test "06: set secondary index should succeed", %{sdk: sdk} do
      result = VertexCacheSdk.set(sdk, "key1", "val", "idx1")
      assert result.success
      assert result.status_message == "OK"
    end

    @tag :live
    test "07: set secondary and tertiary index should succeed", %{sdk: sdk} do
      result = VertexCacheSdk.set(sdk, "key2", "val", "idx1", "idx2")
      assert result.success
      assert result.status_message == "OK"
    end

    @tag :live
    test "08: get by secondary index should return previously set value", %{sdk: sdk} do
      VertexCacheSdk.set(sdk, "key3", "val-sec", "sec-key")
      result = VertexCacheSdk.get_by_secondary_index(sdk, "sec-key")
      assert result.success
      assert result.value == "val-sec"
    end

    @tag :live
    test "09: get by tertiary index should return previously set value", %{sdk: sdk} do
      VertexCacheSdk.set(sdk, "key4", "val-ter", "sec-k", "ter-k")
      result = VertexCacheSdk.get_by_tertiary_index(sdk, "ter-k")
      assert result.success
      assert result.value == "val-ter"
    end

    @tag :live
    test "10: multibyte key and value should succeed", %{sdk: sdk} do
      key = "键🔑値🌟"
      value = "测试🧪データ💾"
      set_result = VertexCacheSdk.set(sdk, key, value)
      assert set_result.success

      get_result = VertexCacheSdk.get(sdk, key)
      assert get_result.success
      assert get_result.value == value
    end

    @tag :live
    test "17: set with empty key should fail", %{sdk: sdk} do
      assert_raise VertexCacheSdkException, ~r/Missing Primary Key/, fn ->
        VertexCacheSdk.set(sdk, "", "value-123")
      end
    end

    @tag :live
    test "18: set with empty value should fail", %{sdk: sdk} do
      assert_raise VertexCacheSdkException, ~r/Missing Value/, fn ->
        VertexCacheSdk.set(sdk, "empty-value-key", "")
      end
    end

    @tag :live
    test "19: set with nil key should throw", %{sdk: sdk} do
      assert_raise VertexCacheSdkException, ~r/Missing Primary Key/, fn ->
        VertexCacheSdk.set(sdk, nil, "value-123")
      end
    end

    @tag :live
    test "20: set with nil value should throw", %{sdk: sdk} do
      assert_raise VertexCacheSdkException, ~r/Missing Value/, fn ->
        VertexCacheSdk.set(sdk, "key", nil)
      end
    end

    @tag :live
    test "21: set with empty secondary index should throw", %{sdk: sdk} do
      assert_raise VertexCacheSdkException, ~r/Secondary key can't be empty/, fn ->
        VertexCacheSdk.set(sdk, "key", "value", "")
      end
    end

    @tag :live
    test "22: set with empty tertiary index should throw", %{sdk: sdk} do
      assert_raise VertexCacheSdkException, ~r/Tertiary key can't be empty/, fn ->
        VertexCacheSdk.set(sdk, "key", "value", "sec", "")
      end
    end


  end
else
  IO.puts("[SKIP] VertexCacheSdkLiveTest: VC_LIVE_TLS_ASYMMETRIC_TEST not enabled")
end
