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
require 'vertexcache/model/client_option'
require 'vertexcache/model/encryption_mode'

RSpec.describe VertexCache::Model::ClientOption do
  let(:option) { described_class.new }

  describe 'defaults' do
    it 'should have correct default values' do
      expect(option.get_client_id).to eq('sdk-client')
      expect(option.get_client_token).to eq('')
      expect(option.server_host).to eq('127.0.0.1')
      expect(option.server_port).to eq(50505)
      expect(option.enable_tls_encryption).to be false
      expect(option.verify_certificate).to be false
      expect(option.read_timeout).to eq(3000)
      expect(option.connect_timeout).to eq(3000)
      expect(option.encryption_mode).to eq(VertexCache::Model::EncryptionMode::NONE)
      expect(option.build_ident_command).to include('IDENT')
    end
  end

  describe 'setters' do
    it 'should apply configured values correctly' do
      option.client_id = 'test-client'
      option.client_token = 'token123'
      option.server_host = '192.168.1.100'
      option.server_port = 9999
      option.enable_tls_encryption = true
      option.verify_certificate = true
      option.tls_certificate = 'cert'
      option.connect_timeout = 1234
      option.read_timeout = 5678
      option.encryption_mode = VertexCache::Model::EncryptionMode::SYMMETRIC

      expect(option.get_client_id).to eq('test-client')
      expect(option.get_client_token).to eq('token123')
      expect(option.server_host).to eq('192.168.1.100')
      expect(option.server_port).to eq(9999)
      expect(option.enable_tls_encryption).to be true
      expect(option.verify_certificate).to be true
      expect(option.tls_certificate).to eq('cert')
      expect(option.connect_timeout).to eq(1234)
      expect(option.read_timeout).to eq(5678)
      expect(option.encryption_mode).to eq(VertexCache::Model::EncryptionMode::SYMMETRIC)
    end
  end

  describe '#build_ident_command' do
    it 'should build correct IDENT command' do
      option.client_id = 'my-id'
      option.client_token = 'my-token'
      expected = 'IDENT {"client_id":"my-id", "token":"my-token"}'
      expect(option.build_ident_command).to eq(expected)
    end

    it 'should fallback to empty client_id and token if nil' do
      option.client_id = nil
      option.client_token = nil
      ident = option.build_ident_command
      expect(ident).to include('"client_id":""')
      expect(ident).to include('"token":""')
    end
  end
end
