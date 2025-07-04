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

defmodule VertexCacheSdk.Comm.MessageCodec do
  @moduledoc """
  Handles TCP message framing and deframing using VertexCache protocol.

  Format:
    - 4 bytes big-endian payload length
    - 4 bytes big-endian protocol version
    - N bytes payload
  """

  @max_message_size 10 * 1024 * 1024
  @protocol_version_rsa_pkcs1 0x00000101
  @protocol_version_aes_gcm 0x00000181

  @doc """
  Encodes the given payload as a framed binary.

  ## Raises
    - `ArgumentError` if payload exceeds max size.
  """
  def write_framed_message(payload, client_option) when is_binary(payload) do
    if byte_size(payload) > @max_message_size do
      raise ArgumentError, "Message too large: #{byte_size(payload)}"
    end

    version =
      case client_option.encryption_mode do
        :symmetric -> @protocol_version_aes_gcm
        :asymmetric -> @protocol_version_rsa_pkcs1
        _ -> @protocol_version_rsa_pkcs1
      end

    <<byte_size(payload)::32-big, version::32-big, payload::binary>>
  end

  @doc """
  Decodes a framed binary from a given binary stream buffer.

  ## Returns
    - `{:ok, payload, rest}` if valid
    - `:error` if header is incomplete
    - `{:error, reason}` if validation fails
  """
  def read_framed_message(<<len::32-big, version::32-big, rest::binary>>) do
    cond do
      version not in [@protocol_version_rsa_pkcs1, @protocol_version_aes_gcm] ->
        {:error, :unsupported_version}

      len <= 0 or len > @max_message_size ->
        {:error, :invalid_length}

      byte_size(rest) < len ->
        :error

      true ->
        <<payload::binary-size(len), remaining::binary>> = rest
        {:ok, payload, remaining}
    end
  end

  def read_framed_message(_), do: :error
end
