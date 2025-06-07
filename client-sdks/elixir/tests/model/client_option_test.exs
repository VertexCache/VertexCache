# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
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

defmodule VertexCacheSdk.Model.ClientOptionTest do
  use ExUnit.Case
  alias VertexCacheSdk.Model.ClientOption

  test "testDefaults" do
    opt = %ClientOption{}

    assert opt.client_id == "sdk-client"
    assert opt.client_token == nil
    assert opt.server_host == "127.0.0.1"
    assert opt.server_port == 50505
    assert opt.enable_tls_encryption == false
    assert opt.verify_certificate == false
    assert opt.read_timeout == 3000
    assert opt.connect_timeout == 3000
    assert opt.encryption_mode == :none

    ident = ClientOption.build_ident_command(opt)
    assert is_binary(ident)
  end

  test "testSetValues" do
    opt = %ClientOption{
      client_id: "test-client",
      client_token: "token123",
      server_host: "192.168.1.100",
      server_port: 9999,
      enable_tls_encryption: true,
      verify_certificate: true,
      tls_certificate: "cert",
      connect_timeout: 1234,
      read_timeout: 5678,
      encryption_mode: :symmetric
    }

    assert opt.client_id == "test-client"
    assert opt.client_token == "token123"
    assert opt.server_host == "192.168.1.100"
    assert opt.server_port == 9999
    assert opt.enable_tls_encryption == true
    assert opt.verify_certificate == true
    assert opt.tls_certificate == "cert"
    assert opt.connect_timeout == 1234
    assert opt.read_timeout == 5678
    assert opt.encryption_mode == :symmetric
  end

  test "testIdentCommandGeneration" do
    opt = %ClientOption{client_id: "my-id", client_token: "my-token"}
    expected = ~s/IDENT {"client_id":"my-id", "token":"my-token"}/
    assert ClientOption.build_ident_command(opt) == expected
  end

  test "testNullTokenAndIdFallback" do
    opt = %ClientOption{client_id: nil, client_token: nil}
    ident = ClientOption.build_ident_command(opt)
    assert ident =~ ~s/"client_id":""/
    assert ident =~ ~s/"token":""/
  end
end
