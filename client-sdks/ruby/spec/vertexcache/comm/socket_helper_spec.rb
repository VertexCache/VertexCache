# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# http://www.apache.org/licenses/LICENSE-2.0
# ------------------------------------------------------------------------------

require 'vertexcache/comm/socket_helper'
require 'vertexcache/model/client_option'
require 'vertexcache/model/vertex_cache_sdk_exception'

RSpec.describe VertexCache::Comm::SocketHelper do
  let(:invalid_port) { 65534 } # assumed unused port
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

  it 'raises VertexCacheSdkException on connection failure' do
    option = VertexCache::Model::ClientOption.new
    option.server_host = 'localhost'
    option.server_port = invalid_port
    option.connect_timeout = 1000
    option.read_timeout = 1000

    expect {
      described_class.create_socket_non_tls(option)
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Failed to create Non Secure Socket/)
  end

  it 'raises VertexCacheSdkException on invalid TLS cert' do
    option = VertexCache::Model::ClientOption.new
    option.server_host = 'localhost'
    option.server_port = invalid_port
    option.connect_timeout = 1000
    option.read_timeout = 1000
    option.verify_certificate = true
    option.tls_certificate = 'not a cert'

    expect {
      described_class.create_secure_socket(option)
    }.to raise_error(VertexCache::Model::VertexCacheSdkException, /Failed to create Secure Socket/)
  end

  it 'connects to live TLS server', if: ENV['ENABLE_LIVE_TLS_TESTS'] == 'true' do
    option = VertexCache::Model::ClientOption.new
    option.server_host = 'localhost'
    option.server_port = 50505
    option.connect_timeout = 1000
    option.read_timeout = 1000
    option.verify_certificate = true
    option.tls_certificate = valid_cert

    socket = VertexCache::Comm::SocketHelper.create_secure_socket(option)
    expect(socket).not_to be_nil
    socket.close
  end
end
