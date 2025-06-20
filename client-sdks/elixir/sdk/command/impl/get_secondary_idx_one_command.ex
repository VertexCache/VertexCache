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

defmodule VertexCacheSdk.Command.Impl.GetSecondaryIdxOneCommand do
  @moduledoc """
  Handles the GET Secondary Idx (idx1) command in VertexCache.

  Retrieves the value for a given key from the cache using a secondary index.
  Returns an error if the key is missing or expired.

  Requires the client to have READ, READ_WRITE, or ADMIN access.
  """

  @behaviour VertexCacheSdk.Command

  alias VertexCacheSdk.Model.{CommandResult, GetResult, VertexCacheSdkException}
  alias VertexCacheSdk.Comm.ClientConnector
  alias VertexCacheSdk.Command.CommandBase

  @type t :: %__MODULE__{key: String.t()}
  defstruct key: nil

  @spec new(String.t()) :: t
  def new(key) when is_binary(key) do
    if String.trim(key) == "" do
      raise VertexCacheSdkException, message: "GET By Secondary Index (idx1) command requires a non-empty key"
    else
      %__MODULE__{key: key}
    end
  end

  def new(_),
      do: raise VertexCacheSdkException,
          message: "GET By Secondary Index (idx1) command requires a non-empty key"

  @spec execute(t, ClientConnector.t()) :: GetResult.t()
  def execute(%__MODULE__{key: key}, connector) do
    CommandBase.execute("GETIDX1 " <> key, connector, &parse_response/1)
    |> to_get_result()
  end

  @spec parse_response(String.t()) :: String.t() | no_return()
  def parse_response("(nil)"), do: "(nil)"

  def parse_response(response) when is_binary(response) do
    if String.starts_with?(response, "ERR") do
      raise "GETIDX1 failed: #{response}"
    else
      response
    end
  end

  @spec to_get_result(CommandResult.t()) :: GetResult.t()
  defp to_get_result(%CommandResult{success: true, status_message: "(nil)"} = result) do
    %GetResult{success: true, status_message: result.status_message, value: nil}
  end

  defp to_get_result(%CommandResult{success: true, status_message: value}) do
    %GetResult{success: true, status_message: value, value: value}
  end

  defp to_get_result(%CommandResult{success: false, status_message: error}) do
    %GetResult{success: false, status_message: error, value: nil}
  end
end
