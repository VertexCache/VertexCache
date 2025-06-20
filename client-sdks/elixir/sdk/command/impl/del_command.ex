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

defmodule VertexCacheSdk.Command.Impl.DelCommand do
  @moduledoc """
  Handles the DEL command in VertexCache.

  Deletes a key and its associated value from the cache.
  If the system is configured to allow idempotent deletes,
  then attempting to delete a non-existent key will still
  return a success response ("OK DEL (noop)").

  Requires the client to have WRITE or ADMIN access.

  Configuration:
  - `del_command_idempotent`: when true, deletion of missing keys does not result in an error.
  """

  @behaviour VertexCacheSdk.Command

  alias VertexCacheSdk.Command.CommandBase
  alias VertexCacheSdk.Command.CommandType
  alias VertexCacheSdk.Model.CommandResult
  alias VertexCacheSdk.Model.VertexCacheSdkException
  alias VertexCacheSdk.Comm.ClientConnector

  @type t :: %__MODULE__{key: String.t()}
  defstruct key: nil

  @spec new(String.t()) :: t
  def new(key) when is_binary(key) do
    if String.trim(key) == "" do
      raise(VertexCacheSdkException, message: "#{CommandType.keyword(:del)} command requires a non-empty key")
    else
      %__MODULE__{key: key}
    end
  end

  def new(_), do:
    raise(VertexCacheSdkException, message: "#{CommandType.keyword(:del)} command requires a non-empty key")

  @spec execute(t, ClientConnector.t()) :: CommandResult.t()
  def execute(%__MODULE__{key: key}, connector) do
    cmd = "#{CommandType.keyword(:del)} #{key}"
    CommandBase.execute(cmd, connector, &parse_response/1)
  end

  @spec parse_response(String.t()) :: :ok
  def parse_response("OK"), do: :ok
  def parse_response(response), do: raise "DEL failed: #{response}"

  @spec build_command(t) :: String.t()
  def build_command(%__MODULE__{key: key}) do
    "#{CommandType.keyword(:del)} #{key}"
  end
end
