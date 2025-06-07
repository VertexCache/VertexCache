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

defmodule VertexCacheSdk.Model.ClientOption do
  @moduledoc """
  Configuration container for initializing the VertexCache SDK client.

  This struct holds all user-specified options required to establish a connection
  to a VertexCache server, including host, port, TLS settings, authentication tokens,
  encryption modes (asymmetric or symmetric), and related keys or certificates.

  It provides a flexible way to customize client behavior, including security preferences.
  """

  alias VertexCacheSdk.Model.EncryptionMode

  @default_client_id "sdk-client"
  @default_host "127.0.0.1"
  @default_port 50505
  @default_read_timeout 3000
  @default_connect_timeout 3000

  defstruct client_id: @default_client_id,
            client_token: nil,

            server_host: @default_host,
            server_port: @default_port,

            enable_tls_encryption: false,
            tls_certificate: nil,
            verify_certificate: false,

            encryption_mode: :none,
            encrypt_with_public_key: false,
            encrypt_with_shared_key: false,

            public_key: nil,
            shared_encryption_key: nil,

            read_timeout: @default_read_timeout,
            connect_timeout: @default_connect_timeout

  @doc """
  Returns the IDENT command used to initiate a client handshake.
  """
  @spec build_ident_command(%__MODULE__{}) :: String.t()
  def build_ident_command(%__MODULE__{client_id: cid, client_token: token}) do
    cid = cid || ""
    token = token || ""

    ~s/IDENT {"client_id":"#{cid}", "token":"#{token}"}/
  end
end
