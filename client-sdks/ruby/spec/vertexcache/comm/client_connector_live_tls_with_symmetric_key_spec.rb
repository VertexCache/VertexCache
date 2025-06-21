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

RSpec.describe VertexCache::Comm::ClientConnector do
  HOST = '127.0.0.1'
  PORT = 50505
  CLIENT_ID = 'sdk-client-ruby'
  CLIENT_TOKEN = 'abf024d1-f3fb-4cc8-ae92-549e87988155'
  TEST_SHARED_KEY = 'neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc='

  it 'performs IDENT and PING over TLS with symmetric encryption' do
    unless ENV['VC_LIVE_TLS_SYMMETRIC_TEST'] == 'true'
      skip('Live test skipped. Set VC_LIVE_TEST=true to enable.')
    end

    option = VertexCache::Model::ClientOption.new
    option.client_id = CLIENT_ID
    option.client_token = CLIENT_TOKEN
    option.server_host = HOST
    option.server_port = PORT
    option.enable_tls_encryption = true
    option.verify_certificate = false
    option.tls_certificate = nil
    option.encryption_mode = VertexCache::Model::EncryptionMode::SYMMETRIC
    option.shared_encryption_key = TEST_SHARED_KEY

    connector = VertexCache::Comm::ClientConnector.new(option)
    connector.connect
    expect(connector.connected?).to be true

    response = connector.send('PING')
    expect(response.start_with?('+PONG')).to be true

    connector.close
    expect(connector.connected?).to be false
  end
end
