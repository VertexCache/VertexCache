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

defmodule VertexCacheSdk.Command do
  @moduledoc """
  Behaviour for all command modules that can be executed by the VertexCache SDK.

  Implementations must define how the command is built and parsed, and return success/error
  status based on the response. This abstraction allows consistent handling of commands
  (e.g. GET, SET, DEL, PING) across the SDK.
  """

  alias VertexCacheSdk.Comm.ClientConnector

  @callback build_command() :: String.t()
  @callback parse_response(String.t()) :: map()
  @callback execute(ClientConnector.t()) :: map()
end
