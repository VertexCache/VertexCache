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

defmodule VertexCacheSdk.Command.CommandBase do
  @moduledoc """
  Provides shared helpers for implementing VertexCache SDK commands.

  This module handles:
  - Raw response decoding and success/error detection
  - Default implementations for `parse_response/1`
  - Utility functions for setting command status

  Commands can delegate to these helpers to keep their logic clean and testable.
  """

  alias VertexCacheSdk.Model.CommandResult
  alias VertexCacheSdk.Comm.ClientConnector
  alias VertexCacheSdk.Model.VertexCacheSdkException

  @spec execute(String.t(), ClientConnector.t(), (String.t() -> any())) :: CommandResult.t()
  def execute(cmd_string, connector, parser_fn \\ fn _ -> nil end) do
    case ClientConnector.send(connector, cmd_string) do
      {:ok, raw} ->
        raw = String.trim(raw)

        cond do
          String.starts_with?(raw, "+") ->
            body = String.slice(raw, 1..-1//-1)
            parser_fn.(body)
            %CommandResult{success: true, status_message: body}

          String.starts_with?(raw, "-") ->
            %CommandResult{success: false, status_message: String.slice(raw, 1..-1//-1)}

          true ->
            %CommandResult{success: false, status_message: "Unexpected response: #{raw}"}
        end

      {:error, %VertexCacheSdkException{} = e} ->
        %CommandResult{success: false, status_message: e.message}
    end

  end


end
