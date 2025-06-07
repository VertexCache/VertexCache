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

defmodule VertexCache.Comm.GcmCryptoHelper do
  @moduledoc """
  AES-GCM helper module for VertexCache SDK (Elixir).
  """

  @iv_length 12
  @tag_length 16

  def encrypt(plaintext, key) do
    iv = :crypto.strong_rand_bytes(@iv_length)
    {ciphertext, tag} = :crypto.crypto_one_time_aead(:aes_256_gcm, key, iv, plaintext, <<>>, true)

    iv <> ciphertext <> tag
  end

  def decrypt(encrypted, key) do
    if byte_size(encrypted) < @iv_length + @tag_length do
      raise ArgumentError, message: "Invalid encrypted data: too short"
    end

    <<iv::binary-size(@iv_length), ciphertext::binary-size(byte_size(encrypted) - @iv_length - @tag_length), tag::binary>> = encrypted

    :crypto.crypto_one_time_aead(:aes_256_gcm, key, iv, ciphertext, <<>>, tag, false)
  end

  def encode_base64_key(key), do: Base.encode64(key)
  def decode_base64_key(encoded), do: Base.decode64!(encoded)

  def generate_base64_key do
    :crypto.strong_rand_bytes(32) |> Base.encode64()
  end
end
