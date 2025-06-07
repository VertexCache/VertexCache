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

require 'spec_helper'
require 'vertexcache/comm/gcm_crypto_helper'

RSpec.describe VertexCache::Comm::GcmCryptoHelper do
  let(:key) { "\x00" * 32 }
  let(:data) { 'VertexCache secure payload' }

  it 'encrypts and decrypts round-trip' do
    encrypted = described_class.encrypt(data, key)
    decrypted = described_class.decrypt(encrypted, key)

    expect(decrypted).to eq(data)
  end

  it 'fails decryption if data is tampered' do
    encrypted = described_class.encrypt(data, key)
    encrypted[-1] = (encrypted[-1].ord ^ 0x01).chr

    expect {
      described_class.decrypt(encrypted, key)
    }.to raise_error(OpenSSL::Cipher::CipherError)
  end

  it 'fails decryption if ciphertext is too short' do
    expect {
      described_class.decrypt("short", key)
    }.to raise_error(ArgumentError)
  end

  it 'encodes and decodes base64 key' do
    encoded = described_class.encode_base64_key(key)
    decoded = described_class.decode_base64_key(encoded)

    expect(decoded).to eq(key)
  end

  it 'generates a 256-bit base64 key' do
    encoded = described_class.generate_base64_key
    decoded = Base64.strict_decode64(encoded)

    expect(decoded.bytesize).to eq(32)
  end

  it 'performs reconciliation test with fixed IV' do
    key = "\x00" * 16
    iv = "\x00" * 12
    data = 'VertexCacheGCMTest'

    encrypted = described_class.encrypt_with_iv(data, key, iv)
    decrypted = described_class.decrypt_with_iv(encrypted, key, iv)

    puts "[RECON] Plaintext: #{data}"
    puts "[RECON] Key (hex): #{key.unpack1('H*')}"
    puts "[RECON] IV (hex): #{iv.unpack1('H*')}"
    puts "[RECON] Encrypted (hex): #{encrypted.unpack1('H*')}"

    expect(decrypted).to eq(data)
  end
end
