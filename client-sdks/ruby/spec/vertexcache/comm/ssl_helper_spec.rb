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
require 'vertexcache/comm/ssl_helper'
require 'vertexcache/model/vertex_cache_sdk_exception'

RSpec.describe VertexCache::Comm::SSLHelper do
  let(:valid_cert) do
    <<~CERT
      -----BEGIN CERTIFICATE-----
      MIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV
      BAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x
      EDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv
      Y2FsaG9zdDAeFw0yNTA1MTgwMzU2NDdaFw0zNTA1MTYwMzU2NDdaMG4xEDAOBgNV
      BAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x
      EDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv
      Y2FsaG9zdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMHVT5HdQkUj
      Ia3sYmLQUeOja7tKtAXi1cuhCLlrvgS2DKJa9cpkgi1dsKOjJmsTqo580e+jrpdQ
      J+mTybdKoG6CZWEqfMizut48aTQoBteiLFSZ9J2/6nCXhxugA+aQ94lhkj3lJIHf
      lIZeIYHaPNXH9/K4oCODJ8P6MfeQjY1ZWbrcQ9PxHQhWV/60AfTuJRJ4T/HQmOqM
      6IcYz2t7iviIYvQq37A+wr1ClgxlfuT6JScEA8J34GivskB2p/MEn8E8y/durORz
      aaF5RBpnsc+fzVwQuvkth993rnDemdrcvTF1bdF5t88Zt5FiPD4qDF+pKloHNMRQ
      DXBYb9Wf/t8CAwEAAaMhMB8wHQYDVR0OBBYEFOYQaTvkoqgLjRhCYBMrwLqrVfJo
      MA0GCSqGSIb3DQEBDAUAA4IBAQCVBHT1uqtm72g085JuWdjBoBDa6bJD3Wj3L+GH
      JaKOF26wQmXtLV0KraH3t3SUxWOM865OcbOkIiSUjMIgqmmFh1quoF4NMBa0wye8
      JguLk6Qpffd+YXfzddxi33jdCUWgyqcTKq7bfB5DbMP4U5yVxnlXwKB0dxkaEFSx
      iAUrhcZ1+iYjelrERk8MPj9FQIzQ8FwwF4oB8ShNDhDNWCOVbSdLXwMOLH84u/ul
      v/I4U/5/mqGGTtwNyyzFS0GYgrYua4H7Aqer2g4wv8PUYwkaAfQ49CWm9kFQxgD4
      qwwA44GZv7zAa89WHNpbIMAA8keexZkPzJBIQNSKy2d9dhcP
      -----END CERTIFICATE-----
    CERT
  end

  it 'creates a verified SSL context with valid cert' do
    context = described_class.create_verified_ssl_context(valid_cert)
    expect(context).to be_a(OpenSSL::SSL::SSLContext)
    expect(context.verify_mode).to eq(OpenSSL::SSL::VERIFY_PEER)
  end

  it 'throws an exception with an invalid cert' do
    expect {
      described_class.create_verified_ssl_context('invalid')
    }.to raise_error(VertexCache::Model::VertexCacheSdkException)
  end

  it 'creates an insecure SSL context' do
    context = described_class.create_insecure_ssl_context
    expect(context).to be_a(OpenSSL::SSL::SSLContext)
    expect(context.verify_mode).to eq(OpenSSL::SSL::VERIFY_NONE)
  end

  it 'connects using insecure TLS if enabled' do
    enable_live = false
    skip 'Live TLS test skipped; enable_live = false' unless enable_live

    begin
      context = described_class.create_insecure_ssl_context
      socket = TCPSocket.new('localhost', 50505)
      ssl_socket = OpenSSL::SSL::SSLSocket.new(socket, context)
      ssl_socket.connect
      expect(ssl_socket).to be_a(OpenSSL::SSL::SSLSocket)
    rescue => e
      skip("TLS connection failed: #{e.message}")
    ensure
      ssl_socket&.close
      socket&.close
    end
  end

  # --- Additional Rust-aligned tests ---

  it 'raises an exception if PEM has structure but corrupt content' do
    corrupt_pem = <<~PEM
      -----BEGIN CERTIFICATE-----
      invalidbase64==
      -----END CERTIFICATE-----
    PEM

    expect {
      described_class.create_verified_ssl_context(corrupt_pem)
    }.to raise_error(VertexCache::Model::VertexCacheSdkException)
  end

  it 'cert is verifiable through OpenSSL X509 store' do
    context = described_class.create_verified_ssl_context(valid_cert)
    store = OpenSSL::X509::Store.new
    store.add_cert(OpenSSL::X509::Certificate.new(valid_cert))

    expect(
      store.verify(OpenSSL::X509::Certificate.new(valid_cert))
    ).to be true
  end

  it 'prints context debug info (non-failing)' do
    context = described_class.create_verified_ssl_context(valid_cert)
    puts "DEBUG: SSLContext verify_mode = #{context.verify_mode}"
  end
end
