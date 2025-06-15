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
require 'vertexcache/comm/key_parser_helper'
require 'vertexcache/model/vertex_cache_sdk_exception'

RSpec.describe VertexCache::Comm::KeyParserHelper do
  let(:valid_pem) do
    <<~PEM
      -----BEGIN PUBLIC KEY-----
      MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
      bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
      UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
      GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
      NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
      6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
      EwIDAQAB
      -----END PUBLIC KEY-----
    PEM
  end

  let(:invalid_pem) { "-----BEGIN PUBLIC KEY-----INVALID-----END PUBLIC KEY-----" }
  let(:valid_shared_key) { "YWJjZGVmZ2hpamtsbW5vcA==" } # "abcdefghijklmnop"
  let(:invalid_shared_key) { "%%%INVALID%%%" }

  it 'config_public_key_if_enabled should succeed with valid PEM' do
    result = described_class.config_public_key_if_enabled(valid_pem)
    expect(result).to be_a(String)
    expect(result.bytesize).to be > 0
  end

  it 'config_public_key_if_enabled should fail with invalid PEM' do
    expect {
      described_class.config_public_key_if_enabled(invalid_pem)
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, 'Invalid public key')
  end

  it 'config_shared_key_if_enabled should succeed with valid base64' do
    result = described_class.config_shared_key_if_enabled(valid_shared_key)
    expect(result).to eq('abcdefghijklmnop')
    expect(result.bytesize).to eq(16)
  end

  it 'config_shared_key_if_enabled should fail with invalid base64' do
    expect {
      described_class.config_shared_key_if_enabled(invalid_shared_key)
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, 'Invalid shared key')
  end
end
