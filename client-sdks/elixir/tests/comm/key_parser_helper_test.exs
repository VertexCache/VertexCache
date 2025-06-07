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

defmodule VertexCacheSdk.Comm.KeyParserHelperTest do
  use ExUnit.Case

  alias VertexCacheSdk.Comm.KeyParserHelper
  alias VertexCacheSdk.Model.VertexCacheSdkException


  @valid_pem """
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

  @invalid_pem "-----BEGIN PUBLIC KEY-----INVALID-----END PUBLIC KEY-----"
  @valid_shared_key "YWJjZGVmZ2hpamtsbW5vcA=="
  @invalid_shared_key "%%%INVALID%%%"

  test "config_public_key_if_enabled should succeed with valid PEM" do
    result = KeyParserHelper.config_public_key_if_enabled(@valid_pem)
    assert is_binary(result)
    assert byte_size(result) > 0
  end

  test "config_public_key_if_enabled should fail with invalid PEM" do
    assert_raise VertexCacheSdkException, "Invalid public key", fn ->
      KeyParserHelper.config_public_key_if_enabled(@invalid_pem)
    end
  end

  test "config_shared_key_if_enabled should succeed with valid base64" do
    result = KeyParserHelper.config_shared_key_if_enabled(@valid_shared_key)
    assert is_binary(result)
    assert byte_size(result) == 16
  end

  test "config_shared_key_if_enabled should match expected bytes" do
    expected = "abcdefghijklmnop"
    result = KeyParserHelper.config_shared_key_if_enabled(@valid_shared_key)
    assert result == expected
  end

  test "config_shared_key_if_enabled should fail with invalid base64" do
    assert_raise VertexCacheSdkException, "Invalid shared key", fn ->
      KeyParserHelper.config_shared_key_if_enabled(@invalid_shared_key)
    end
  end
end
