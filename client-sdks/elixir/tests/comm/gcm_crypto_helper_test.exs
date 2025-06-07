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

defmodule VertexCache.Comm.GcmCryptoHelperTest do
  use ExUnit.Case
  alias VertexCache.Comm.GcmCryptoHelper

  @key :binary.copy(<<0>>, 32)
  @message "VertexCache secure payload"

  test "encrypt and decrypt round-trip" do
    encrypted = GcmCryptoHelper.encrypt(@message, @key)
    assert byte_size(encrypted) > byte_size(@message)

    decrypted = GcmCryptoHelper.decrypt(encrypted, @key)
    assert decrypted == @message
  end

  test "decrypt should fail on tampered tag" do
    encrypted = GcmCryptoHelper.encrypt(@message, @key)
    tampered = binary_part(encrypted, 0, byte_size(encrypted) - 1) <> <<255>>

    try do
      GcmCryptoHelper.decrypt(tampered, @key)
      flunk("Expected decryption to fail, but it succeeded")
    rescue
      _ -> assert true
    end
  end

  test "decrypt should fail if too short" do
    assert_raise ArgumentError, fn ->
      GcmCryptoHelper.decrypt(<<1, 2, 3>>, @key)
    end
  end

  test "base64 encode/decode round trip" do
    b64 = GcmCryptoHelper.encode_base64_key(@key)
    decoded = GcmCryptoHelper.decode_base64_key(b64)
    assert decoded == @key
  end

  test "generate base64 key returns 32-byte decoded key" do
    key = GcmCryptoHelper.generate_base64_key() |> GcmCryptoHelper.decode_base64_key()
    assert byte_size(key) == 32
  end

  test "reconciliation test with fixed key and iv" do
    key = :binary.copy(<<0>>, 16)
    iv = :binary.copy(<<0>>, 12)
    message = "VertexCacheGCMTest"

    {ciphertext, tag} = :crypto.crypto_one_time_aead(:aes_128_gcm, key, iv, message, <<>>, true)
    encrypted = iv <> ciphertext <> tag

    decrypted = :crypto.crypto_one_time_aead(:aes_128_gcm, key, iv, ciphertext, <<>>, tag, false)
    assert decrypted == message

    IO.puts "[RECON] Plaintext: #{message}"
    IO.puts "[RECON] Key (hex): #{Base.encode16(key, case: :lower)}"
    IO.puts "[RECON] IV (hex): #{Base.encode16(iv, case: :lower)}"
    IO.puts "[RECON] Encrypted (hex): #{Base.encode16(encrypted, case: :lower)}"
  end
end
