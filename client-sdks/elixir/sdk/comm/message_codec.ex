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

defmodule VertexCacheSdk.Comm.MessageCodec do
  @moduledoc """
  Handles TCP message framing and deframing using VertexCache protocol.

  Format:
    - 4 bytes big-endian payload length
    - 1 byte protocol version
    - N bytes payload
  """

  @max_message_size 10 * 1024 * 1024
  @protocol_version 0x01

  @doc """
  Encodes the given payload as a framed binary.

  ## Raises
    - `ArgumentError` if payload exceeds max size.
  """
  def write_framed_message(payload) when is_binary(payload) do
    if byte_size(payload) > @max_message_size do
      raise ArgumentError, "Message too large: #{byte_size(payload)}"
    end

    length = <<byte_size(payload)::32-big>>
    version = <<@protocol_version>>
    length <> version <> payload
  end

  @doc """
  Decodes a framed binary from a given binary stream buffer.

  ## Returns
    - `{:ok, payload, rest}` if valid
    - `:error` if header is incomplete
    - `{:error, reason}` if validation fails
  """
  def read_framed_message(<<len::32-big, version::8, rest::binary>>) do
    cond do
      version != @protocol_version ->
        {:error, :unsupported_version}

      len <= 0 or len > @max_message_size ->
        {:error, :invalid_length}

      byte_size(rest) < len ->
        :error  # wait for more data

      true ->
        <<payload::binary-size(len), remaining::binary>> = rest
        {:ok, payload, remaining}
    end
  end

  def read_framed_message(_), do: :error
end
