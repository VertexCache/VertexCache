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

defmodule VertexCacheSdk.Command.Impl.PingCommand do
  @moduledoc """
  Handles the PING command in VertexCache.

  This command is used to check server availability and latency.
  It returns a basic `"PONG"` response and can be used by clients to verify liveness.

  PING is always allowed regardless of authentication state or client role.
  It does not require access validation or key arguments.
  """

  @behaviour VertexCacheSdk.Command

  alias VertexCacheSdk.Command.CommandBase
  alias VertexCacheSdk.Model.CommandResult
  alias VertexCacheSdk.Comm.ClientConnector

  @spec execute(ClientConnector.t()) :: CommandResult.t()
  def execute(connector) do
    CommandBase.execute("PING", connector, &parse_response/1)
  end

  @spec build_command() :: String.t()
  def build_command, do: "PING"

  @spec parse_response(String.t()) :: :ok
  def parse_response("PONG"), do: :ok
  def parse_response(_), do: raise "PONG not received"
end
