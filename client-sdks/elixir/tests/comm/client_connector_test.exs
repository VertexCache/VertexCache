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

defmodule VertexCacheSdk.Comm.ClientConnectorTest do
  @moduledoc """
  Go-style unit tests for ClientConnector logic.

  Covers default connection state and IDENT formatting. No live sockets or encryption.
  """

  use ExUnit.Case, async: true

  alias VertexCacheSdk.Comm.ClientConnector
  alias VertexCacheSdk.Model.ClientOption

  describe "is_connected/1 default behavior" do
    test "returns false when socket is nil" do
      assert ClientConnector.is_connected(%{socket: nil}) == false
    end
  end

  describe "close/1 safety" do
    test "does not crash when socket is nil" do
      assert ClientConnector.close(%{socket: nil}) == :ok
    end
  end

  describe "build_ident_command/1 format" do
    test "contains client_id and token" do
      opts = %ClientOption{client_id: "abc", client_token: "xyz"}
      ident = ClientOption.build_ident_command(opts)

      assert String.starts_with?(ident, "IDENT {")
      assert String.contains?(ident, "abc")
      assert String.contains?(ident, "xyz")
    end
  end
end
