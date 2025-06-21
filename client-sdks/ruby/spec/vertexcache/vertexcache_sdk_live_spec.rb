# frozen_string_literal: true

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
require 'vertexcache_sdk'
require 'vertexcache/model/encryption_mode'
require 'vertexcache/model/client_option'
require 'vertexcache/model/vertex_cache_sdk_exception'

RSpec.describe VertexCache::VertexCacheSDK do
  CLIENT_ID    = 'sdk-client-ruby'
  CLIENT_TOKEN = 'abf024d1-f3fb-4cc8-ae92-549e87988155'
  HOST         = 'localhost'
  PORT         = 50505
  TEST_CERT    = ENV['VC_LIVE_TLS_CERT'] || ''

  TEST_PUBLIC_KEY = <<~KEY.strip
    -----BEGIN PUBLIC KEY-----
    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
    bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
    UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
    GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
    NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
    6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
    EwIDAQAB
    -----END PUBLIC KEY-----
  KEY

  let(:client_options) do
    opts = VertexCache::Model::ClientOption.new
    opts.client_id     = CLIENT_ID
    opts.client_token  = CLIENT_TOKEN
    opts.server_host   = HOST
    opts.server_port   = PORT
    opts.enable_tls_encryption = true
    opts.tls_certificate       = TEST_CERT
    opts.encryption_mode       = VertexCache::Model::EncryptionMode::ASYMMETRIC
    opts.public_key            = TEST_PUBLIC_KEY
    opts
  end

  before(:each) do
    skip 'VC_LIVE_TLS_ASYMMETRIC_TEST not set' unless ENV['VC_LIVE_TLS_ASYMMETRIC_TEST'] == 'true'
    @sdk = described_class.new(client_options)
    @sdk.open_connection
  end

  after(:each) do
    @sdk.close if @sdk&.is_connected?
  end

  it '01: ping should succeed' do
    result = @sdk.ping
    expect(result.success?).to be true
    expect(result.message.start_with?('PONG')).to be true
  end

  it '02: set and get value' do
    expect(@sdk.set('test-key', 'value-123').success?).to be true
    result = @sdk.get('test-key')
    expect(result.success?).to be true
    expect(result.value).to eq('value-123')
  end

  it '03: del should remove key' do
    @sdk.set('delete-key', 'to-be-deleted')
    expect(@sdk.del('delete-key').success?).to be true
    result = @sdk.get('delete-key')
    expect(result.success?).to be true
    expect(result.value).to be_nil
  end

  it '04: get on missing key should succeed with nil' do
    result = @sdk.get('nonexistent-key')
    expect(result.success?).to be true
    expect(result.value).to be_nil
  end

  it '05: set with secondary index' do
    result = @sdk.set('test-key', 'value-123', 'test-secondary-index')
    expect(result.success?).to be true
    expect(result.message).to eq('OK')
  end

  it '06: set with secondary and tertiary index' do
    result = @sdk.set('test-key', 'value-123', 'test-secondary-index', 'test-tertiary-index')
    expect(result.success?).to be true
    expect(result.message).to eq('OK')
  end

  it '07: get by secondary index' do
    @sdk.set('test-key', 'value-123', 'test-secondary-index')
    result = @sdk.get_by_secondary_index('test-secondary-index')
    expect(result.success?).to be true
    expect(result.value).to eq('value-123')
  end

  it '08: get by tertiary index' do
    @sdk.set('test-key', 'value-123', 'test-secondary-index', 'test-tertiary-index')
    result = @sdk.get_by_tertiary_index('test-tertiary-index')
    expect(result.success?).to be true
    expect(result.value).to eq('value-123')
  end

  it '09: handles multibyte keys and values' do
    key = '键🔑値🌟'
    val = '测试🧪データ💾'
    expect(@sdk.set(key, val).success?).to be true
    result = @sdk.get(key)
    expect(result.success?).to be true
    expect(result.value).to eq(val)
  end

  it '10: connect with bad host fails' do
    bad_opts = client_options
    bad_opts.server_host = 'bad-host'
    expect {
      VertexCache::VertexCacheSDK.new(bad_opts).open_connection
    }.to raise_error(VertexCache::Model::VertexCacheSdkException)
  end

  it '11: connect with bad port fails' do
    bad_opts = client_options
    bad_opts.server_port = 0
    expect {
      VertexCache::VertexCacheSDK.new(bad_opts).open_connection
    }.to raise_error(VertexCache::Model::VertexCacheSdkException)
  end

  it '12: secure TLS fails with invalid cert validation' do
    bad_opts = client_options
    bad_opts.verify_certificate = true
    expect {
      VertexCache::VertexCacheSDK.new(bad_opts).open_connection
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Secure Socket/)
  end

  it '13: connect with TLS in insecure mode should succeed' do
    tls_opts = client_options
    tls_opts.verify_certificate = false
    tls_opts.tls_certificate = nil
    sdk = VertexCache::VertexCacheSDK.new(tls_opts)
    sdk.open_connection
    sdk.close
  end

  it '14: set with empty key fails' do
    expect {
      @sdk.set('', 'value-123')
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Missing Primary Key/)
  end

  it '15: set with empty value fails' do
    expect {
      @sdk.set('key', '')
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Missing Value/)
  end

  it '16: set with nil key fails' do
    expect {
      @sdk.set(nil, 'value-123')
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Missing Primary Key/)
  end

  it '17: set with nil value fails' do
    expect {
      @sdk.set('key', nil)
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Missing Value/)
  end

  it '18: set with empty secondary index fails' do
    expect {
      @sdk.set('key', 'val', '')
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Secondary key/)
  end

  it '19: set with empty tertiary index fails' do
    expect {
      @sdk.set('key', 'val', 'sec', '')
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Tertiary key/)
  end

  it '20: set invalid public key fails' do
    bad_opts = client_options.dup
    expect {
      bad_opts.public_key = TEST_PUBLIC_KEY + '_BAD'
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Invalid public key/)
  end

  it '21: set invalid shared key fails' do
    opts = client_options
    opts.encryption_mode = VertexCache::Model::EncryptionMode::SYMMETRIC
    expect {
      opts.shared_encryption_key = '_BAD_SHARED_KEY'
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Invalid shared key/)
  end
end
