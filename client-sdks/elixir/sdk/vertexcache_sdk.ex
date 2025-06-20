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

defmodule VertexCacheSdk do
  @moduledoc """
  VertexCacheSdk is the main entry point for interacting with the VertexCache server.

  It provides methods to perform cache operations such as GET, SET, and DEL, and abstracts
  away the underlying TCP transport details.

  This SDK handles encryption (symmetric/asymmetric), TLS negotiation, authentication, and
  framing of commands and responses.
  """

  alias VertexCacheSdk.Comm.ClientConnector
  alias VertexCacheSdk.Model.{ClientOption, CommandResult, GetResult}
  alias VertexCacheSdk.Command.Impl.{
    PingCommand,
    SetCommand,
    DelCommand,
    GetCommand,
    GetSecondaryIdxOneCommand,
    GetSecondaryIdxTwoCommand
    }

  defstruct connector: nil

  @type t :: %__MODULE__{
               connector: ClientConnector.t()
             }

  @spec open_connection(ClientOption.t()) :: {:ok, t} | {:error, VertexCacheSdkException.t()}
  def open_connection(%ClientOption{} = opts) do
    case ClientConnector.connect(%{opts: opts}) do
      {:ok, connector} ->
        {:ok, %__MODULE__{connector: connector}}

      {:error, reason} ->
        {:error, reason}
    end
  end

  @spec is_connected?(t) :: boolean
  def is_connected?(%__MODULE__{connector: conn}) do
    ClientConnector.connected?(conn)
  end

  @spec close(t) :: :ok
  def close(%__MODULE__{connector: conn}) do
    ClientConnector.close(conn)
  end

  @spec ping(t) :: CommandResult.t()
  def ping(%__MODULE__{connector: conn}) do
    PingCommand.execute(conn)
  end

  @spec set(t, String.t(), String.t()) :: CommandResult.t()
  def set(%__MODULE__{connector: conn}, key, value) do
    SetCommand.new(key, value) |> SetCommand.execute(conn)
  end

  @spec set(t, String.t(), String.t(), String.t()) :: CommandResult.t()
  def set(%__MODULE__{connector: conn}, key, value, idx1) do
    SetCommand.new(key, value, idx1) |> SetCommand.execute(conn)
  end

  @spec set(t, String.t(), String.t(), String.t(), String.t()) :: CommandResult.t()
  def set(%__MODULE__{connector: conn}, key, value, idx1, idx2) do
    SetCommand.new(key, value, idx1, idx2) |> SetCommand.execute(conn)
  end

  @spec del(t, String.t()) :: CommandResult.t()
  def del(%__MODULE__{connector: conn}, key) do
    DelCommand.new(key) |> DelCommand.execute(conn)
  end

  @spec get(t, String.t()) :: GetResult.t()
  def get(%__MODULE__{connector: conn}, key) do
    GetCommand.new(key) |> GetCommand.execute(conn)
  end

  @spec get_by_secondary_index(t, String.t()) :: GetResult.t()
  def get_by_secondary_index(%__MODULE__{connector: conn}, idx1) do
    GetSecondaryIdxOneCommand.new(idx1) |> GetSecondaryIdxOneCommand.execute(conn)
  end

  @spec get_by_tertiary_index(t, String.t()) :: GetResult.t()
  def get_by_tertiary_index(%__MODULE__{connector: conn}, idx2) do
    GetSecondaryIdxTwoCommand.new(idx2) |> GetSecondaryIdxTwoCommand.execute(conn)
  end
end
