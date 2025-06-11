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

defmodule VertexCacheSdk.Comm.ClientConnector do
  @moduledoc """
  Primary SDK transport layer for VertexCache.

  Handles TLS, IDENT handshake, framing, and encryption.
  """

  alias VertexCacheSdk.Comm.{SocketHelper, MessageCodec}
  alias VertexCacheSdk.Model.{ClientOption, EncryptionMode, VertexCacheSdkException}
  alias VertexCacheSdk.Comm.{GcmCryptoHelper, KeyParserHelper}

  @type socket :: port() | :ssl.sslsocket()
  @type state :: %{socket: socket(), opts: ClientOption.t()}

  @spec connect(ClientOption.t()) :: {:ok, state()} | {:error, VertexCacheSdkException.t()}
  def connect(%ClientOption{} = opts) do
    try do
      {:ok, sock} =
        if opts.enable_tls_encryption do
          SocketHelper.create_secure_socket(opts)
        else
          SocketHelper.create_socket_non_tls(opts)
        end

      ident = ClientOption.build_ident_command(opts)
      payload = encrypt_if_enabled(ident, opts)
      send_payload(sock, payload)

      response = read_response(sock)

      unless String.starts_with?(response, "+OK") do
        raise VertexCacheSdkException, message: "Authorization failed: #{response}"
      end

      {:ok, %{socket: sock, opts: opts}}
    rescue
      e in VertexCacheSdkException -> {:error, e}
      e -> {:error, %VertexCacheSdkException{message: Exception.message(e)}}
    end
  end

  @spec send(state(), String.t()) :: {:ok, String.t()} | {:error, VertexCacheSdkException.t()}
  def send(%{socket: sock, opts: opts}, message) do
    try do
      payload = encrypt_if_enabled(message, opts)
      send_payload(sock, payload)
      response = read_response(sock)
      {:ok, response}
    rescue
      e in VertexCacheSdkException -> {:error, e}
      _ -> {:error, %VertexCacheSdkException{message: "Unexpected failure during send"}}
    end
  end

  @spec is_connected(state()) :: boolean()
  def is_connected(%{socket: nil}), do: false

  def is_connected(%{socket: sock}) do
    raw_sock =
      case sock do
        {:sslsocket, {_, port, _, _}, _} -> port
        _ -> sock
      end

    case :inet.peername(raw_sock) do
      {:ok, _} -> true
      _ -> false
    end
  end

  @spec close(state()) :: :ok
  def close(%{socket: nil}), do: :ok

  def close(%{socket: sock}) do
    if match?({:sslsocket, _, _}, sock) do
      :ssl.close(sock)
    else
      :gen_tcp.close(sock)
    end

    :ok
  end

  # ------------------------
  # Private helpers below
  # ------------------------

  defp encrypt_if_enabled(payload, %ClientOption{encryption_mode: :none}) when is_binary(payload),
       do: payload

  defp encrypt_if_enabled(payload, %ClientOption{
    encryption_mode: :symmetric,
    shared_encryption_key: key
  }) do
    decoded = KeyParserHelper.config_shared_key_if_enabled(key)
    GcmCryptoHelper.encrypt(payload, decoded)
  end

  defp encrypt_if_enabled(payload, %ClientOption{encryption_mode: :asymmetric, public_key: pem}) do
    der = KeyParserHelper.config_public_key_if_enabled(pem)
    entry = {:SubjectPublicKeyInfo, der, :not_encrypted}
    rsa_key = :public_key.pem_entry_decode(entry)
    :public_key.encrypt_public(payload, rsa_key)
  end

  defp send_payload(sock, payload) do
    framed = MessageCodec.write_framed_message(payload)

    if match?({:sslsocket, _, _}, sock) do
      :ssl.send(sock, framed)
    else
      :gen_tcp.send(sock, framed)
    end

    :ok
  end

  defp read_response(sock) do
    raw =
      if match?({:sslsocket, _, _}, sock) do
        :ssl.recv(sock, 0, 5000)
      else
        :gen_tcp.recv(sock, 0, 5000)
      end

    case raw do
      {:ok, data} ->
        case MessageCodec.read_framed_message(data) do
          {:ok, payload, _} -> payload
          :error -> raise VertexCacheSdkException, message: "Incomplete response framing"
          {:error, reason} -> raise VertexCacheSdkException, message: "Framing error: #{inspect(reason)}"
        end

      {:error, reason} ->
        raise VertexCacheSdkException, message: "Socket read failed: #{inspect(reason)}"
    end
  end
end
