// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ------------------------------------------------------------------------------

use vertexcache_sdk::comm::ssl_helper::SSLHelper;
use vertexcache_sdk::model::vertex_cache_sdk_exception::VertexCacheSdkException;

use std::net::TcpStream;
use native_tls::TlsConnector;

const VALID_PEM_CERT: &str = r#"-----BEGIN CERTIFICATE-----
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
-----END CERTIFICATE-----"#;

#[test]
fn test_create_verified_tls_connector_valid_cert_should_succeed() {
    let result = SSLHelper::create_verified_tls_connector(VALID_PEM_CERT);
    assert!(result.is_ok());
}

#[test]
fn test_create_verified_tls_connector_invalid_cert_should_fail() {
    let result = SSLHelper::create_verified_tls_connector("invalid");
    assert!(matches!(result, Err(VertexCacheSdkException { .. })));
}

#[test]
fn test_create_insecure_tls_connector_should_succeed() {
    let result = SSLHelper::create_insecure_tls_connector();
    assert!(result.is_ok());
}

#[test]
fn test_live_tls_connection_verified_mode_should_succeed() {
    let enable_live = false;
    if !enable_live {
        eprintln!("Verified TLS test skipped; enable_live = false");
        return;
    }

    let connector = SSLHelper::create_verified_tls_connector(VALID_PEM_CERT)
        .expect("Failed to create verified TLS connector");

    let stream = TcpStream::connect("127.0.0.1:50505")
        .expect("Failed to connect to localhost:50505");

    let tls_stream = connector.connect("localhost", stream);

    match tls_stream {
        Ok(mut stream) => {
            use std::io::Write;
            let _ = stream.write_all(b"PING\n");
            assert!(true);
        }
        Err(err) => {
            eprintln!("Verified TLS connection failed: {:?}", err);
            assert!(false, "Verified TLS handshake failed");
        }
    }
}

#[test]
fn test_live_tls_connection_insecure_mode_should_succeed() {
    let enable_live = false; // flip to true to run real test
    if !enable_live {
        eprintln!("Live TLS test skipped; enable_live = false");
        return;
    }

    let connector = SSLHelper::create_insecure_tls_connector()
        .expect("Failed to create insecure TLS connector");

    let stream = TcpStream::connect("127.0.0.1:50505")
        .expect("Failed to connect to localhost:50505");

    let tls_stream = connector.connect("localhost", stream);

    match tls_stream {
        Ok(mut stream) => {
            use std::io::Write;
            let _ = stream.write_all(b"PING\n");
            assert!(true);
        }
        Err(err) => {
            eprintln!("TLS connection failed: {:?}", err);
            assert!(false, "TLS handshake failed");
        }
    }
}