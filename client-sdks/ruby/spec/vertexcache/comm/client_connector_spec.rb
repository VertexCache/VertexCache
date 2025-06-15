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
require 'vertexcache/comm/client_connector'
require 'vertexcache/model/client_option'
require 'vertexcache/model/encryption_mode'
require 'vertexcache/comm/message_codec'

RSpec.describe VertexCache::Comm::ClientConnector do
  it 'resolves protocol version correctly for NONE mode' do
    option = VertexCache::Model::ClientOption.new
    option.encryption_mode = VertexCache::Model::EncryptionMode::NONE
    connector = VertexCache::Comm::ClientConnector.new(option)

    expect(connector.resolve_protocol_version).to eq(VertexCache::Comm::MessageCodec::DEFAULT_PROTOCOL_VERSION)
  end

  it 'resolves protocol version correctly for SYMMETRIC mode' do
    option = VertexCache::Model::ClientOption.new
    option.encryption_mode = VertexCache::Model::EncryptionMode::SYMMETRIC
    option.shared_encryption_key = '00000000000000000000000000000000'
    connector = VertexCache::Comm::ClientConnector.new(option)

    expect(connector.resolve_protocol_version).to eq(VertexCache::Comm::MessageCodec::PROTOCOL_VERSION_AES_GCM)
  end

  it 'resolves protocol version correctly for ASYMMETRIC mode' do
    public_key = <<~PEM
      -----BEGIN PUBLIC KEY-----
      MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAK7m8GboIry9a1W1P6g0UCeHPCXnKMV0
      MxB8F3uowcdUokTQvO0g7th3pduDDgWkYbWX3XP4sz9tB09J74s3pHECAwEAAQ==
      -----END PUBLIC KEY-----
    PEM

    option = VertexCache::Model::ClientOption.new
    option.encryption_mode = VertexCache::Model::EncryptionMode::ASYMMETRIC
    option.public_key = public_key.strip
    connector = VertexCache::Comm::ClientConnector.new(option)

    expect(connector.resolve_protocol_version).to eq(VertexCache::Comm::MessageCodec::PROTOCOL_VERSION_RSA_PKCS1)
  end

  it 'initializes correctly with basic configuration' do
    option = VertexCache::Model::ClientOption.new
    option.server_host = 'localhost'
    option.server_port = 50505
    option.enable_tls_encryption = false
    option.connect_timeout = 2000
    option.read_timeout = 2000

    connector = VertexCache::Comm::ClientConnector.new(option)
    expect(connector.connected?).to be false
  end
end
