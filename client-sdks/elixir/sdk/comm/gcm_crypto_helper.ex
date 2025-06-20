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

defmodule VertexCacheSdk.Comm.GcmCryptoHelper do
  @moduledoc """
  AES-GCM helper module for VertexCache SDK (Elixir).

  Uses AES-256-GCM with a 12-byte IV and 16-byte authentication tag.
  Encrypted payload is: IV || ciphertext || tag
  """

  @iv_length 12
  @tag_length 16
  @key_length 32

  @doc """
  Encrypts the given plaintext using AES-256-GCM.

  Returns a binary of IV || ciphertext || tag.
  """
  def encrypt(plaintext, key) when is_binary(plaintext) and is_binary(key) and byte_size(key) == @key_length do
    iv = :crypto.strong_rand_bytes(@iv_length)
    {ciphertext, tag} = :crypto.crypto_one_time_aead(:aes_256_gcm, key, iv, plaintext, <<>>, true)
    iv <> ciphertext <> tag
  end

  @doc """
  Decrypts an AES-256-GCM encrypted binary of the format IV || ciphertext || tag.
  """
  def decrypt(encrypted, key) when is_binary(encrypted) and is_binary(key) and byte_size(key) == @key_length do
    total_len = byte_size(encrypted)
    min_len = @iv_length + @tag_length

    if total_len < min_len do
      raise ArgumentError, message: "Invalid encrypted data: too short"
    end

    <<iv::binary-size(@iv_length), ciphertext::binary-size(total_len - min_len), tag::binary-size(@tag_length)>> = encrypted
    :crypto.crypto_one_time_aead(:aes_256_gcm, key, iv, ciphertext, <<>>, tag, false)
  end

  @doc """
  Encodes a raw binary key as base64.
  """
  def encode_base64_key(key) when is_binary(key), do: Base.encode64(key)

  @doc """
  Decodes a base64-encoded key.
  """
  def decode_base64_key(encoded) when is_binary(encoded), do: Base.decode64!(encoded)

  @doc """
  Generates a 256-bit AES key encoded in base64.
  """
  def generate_base64_key do
    :crypto.strong_rand_bytes(@key_length) |> Base.encode64()
  end
end
