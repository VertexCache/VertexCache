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

defmodule VertexCacheSdk.Command.Impl.SetCommand do
  @moduledoc """
  Handles the SET command in VertexCache.

  Stores a value in the cache under the specified key, optionally assigning
  secondary (`idx1`) and tertiary (`idx2`) indexes for lookup. Existing keys will
  be overwritten. Supports expiration and format validation if configured.

  Requires the client to have WRITE or ADMIN access.

  Validation:
  - Primary key and value are required.
  - If secondary index is provided, it must be non-blank.
  - If tertiary index is used, secondary index must also be present and non-blank.
  """

  @behaviour VertexCacheSdk.Command

  alias VertexCacheSdk.Command.{CommandBase, CommandType}
  alias VertexCacheSdk.Model.{CommandResult, VertexCacheSdkException}
  alias VertexCacheSdk.Comm.ClientConnector

  @type t :: %__MODULE__{
               primary_key: String.t(),
               value: String.t(),
               secondary_key: String.t() | nil,
               tertiary_key: String.t() | nil
             }

  defstruct primary_key: nil, value: nil, secondary_key: nil, tertiary_key: nil

  @spec new(String.t(), String.t()) :: t
  def new(primary_key, value), do: new(primary_key, value, nil, nil)

  @spec new(String.t(), String.t(), String.t()) :: t
  def new(primary_key, value, idx1), do: new(primary_key, value, idx1, nil)

  @spec new(String.t(), String.t(), String.t() | nil, String.t() | nil) :: t
  def new(primary_key, value, idx1, idx2) do
    cond do
      is_blank(primary_key) ->
        raise VertexCacheSdkException, message: "Missing Primary Key"

      is_blank(value) ->
        raise VertexCacheSdkException, message: "Missing Value"

      not is_nil(idx1) and is_blank(idx1) ->
        raise VertexCacheSdkException, message: "Secondary key can't be empty when used"

      not is_nil(idx2) and is_blank(idx2) ->
        raise VertexCacheSdkException, message: "Tertiary key can't be empty when used"

      not is_nil(idx2) and (is_nil(idx1) or is_blank(idx1)) ->
        raise VertexCacheSdkException, message: "Secondary key must be present when using tertiary index"


      true ->
        %__MODULE__{
          primary_key: primary_key,
          value: value,
          secondary_key: idx1,
          tertiary_key: idx2
        }
    end
  end

  @spec execute(t, ClientConnector.t()) :: CommandResult.t()
  def execute(%__MODULE__{} = cmd, connector) do
    CommandBase.execute(build_command(cmd), connector, &parse_response/1)
  end

  @spec build_command(t) :: String.t()
  def build_command(%__MODULE__{primary_key: pk, value: val, secondary_key: idx1, tertiary_key: idx2}) do
    base = [
      CommandType.keyword(:set),
      pk,
      val
    ]

    base
    |> append_idx("IDX1", idx1)
    |> append_idx("IDX2", idx2)
    |> Enum.join(" ")
  end

  defp append_idx(cmd_parts, _label, nil), do: cmd_parts
  defp append_idx(cmd_parts, _label, ""), do: cmd_parts
  defp append_idx(cmd_parts, label, key), do: cmd_parts ++ [label, key]

  @spec parse_response(String.t()) :: :ok | no_return()
  def parse_response("OK"), do: :ok
  def parse_response(_), do: raise "OK Not received"

  defp is_blank(nil), do: true
  defp is_blank(str) when is_binary(str), do: String.trim(str) == ""
end
