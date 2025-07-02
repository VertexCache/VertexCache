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
// ------------------------------------------------------------------------------
use vertexcache_sdk::comm::socket_helper::SocketHelper;
use vertexcache_sdk::model::client_option::ClientOption;

const UNUSED_PORT: u16 = 65534;
const BLACKHOLE_IP: &str = "10.255.255.1";

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
fn test_create_socket_non_tls_should_fail_if_port_closed() {
    let enable_live = false; // flip to true to run real test
    if !enable_live {
        eprintln!("Live TLS test skipped; enable_live = false");
        return;
    }

    let mut option = ClientOption::new();
    option.set_server_host("127.0.0.1".to_string());
    option.set_server_port(UNUSED_PORT);
    option.set_connect_timeout(500);
    option.set_read_timeout(500);

    let result = SocketHelper::create_socket_non_tls(&option);
    assert!(result.is_err());

    if let Err(err) = result {
        assert!(err.message().contains("Non Secure Socket"));
    }
}

#[test]
fn test_create_socket_non_tls_should_fail_on_timeout() {
    let mut option = ClientOption::new();
    option.set_server_host(BLACKHOLE_IP.to_string());
    option.set_server_port(12345);
    option.set_connect_timeout(300);
    option.set_read_timeout(500);

    let result = SocketHelper::create_socket_non_tls(&option);
    assert!(result.is_err());

    if let Err(err) = result {
        assert!(err.message().contains("Non Secure Socket"));
    }
}

#[test]
fn test_create_secure_socket_should_fail_due_to_missing_tls_context() {
    let enable_live = false; // flip to true to run real test
    if !enable_live {
        eprintln!("Live TLS test skipped; enable_live = false");
        return;
    }

    let mut option = ClientOption::new();
    option.set_server_host("127.0.0.1".to_string());
    option.set_server_port(50505);
    option.set_connect_timeout(1000);
    option.set_read_timeout(1000);
    option.set_verify_certificate(true);
    option.set_tls_certificate(None);

    let result = SocketHelper::create_secure_socket(&option);
    assert!(result.is_err());

    if let Err(err) = result {
        assert!(
            err.message().contains("Secure Socket") ||
            err.message().contains("secure socket connection") ||
            err.message().contains("Missing TLS certificate")
        );
    }
}

#[test]
fn test_create_secure_socket_should_fail_with_bad_certificate() {
    let enable_live = false; // flip to true to run real test
    if !enable_live {
        eprintln!("Live TLS test skipped; enable_live = false");
        return;
    }

    let mut option = ClientOption::new();
    option.set_server_host("127.0.0.1".to_string());
    option.set_server_port(50505);
    option.set_connect_timeout(1000);
    option.set_read_timeout(1000);
    option.set_verify_certificate(true);
    option.set_tls_certificate(Some("not a cert".to_string()));

    let result = SocketHelper::create_secure_socket(&option);
    assert!(result.is_err());

    if let Err(err) = result {
        eprintln!("❗ Secure socket error: {}", err.message());
        assert!(
            err.message().contains("Secure Socket") ||
            err.message().contains("secure socket connection") ||
            err.message().contains("TLS handshake failed") ||
            err.message().contains("Invalid certificate format")
        );
    }
}

#[test]
fn test_create_secure_socket_insecure_mode_should_succeed() {
    let enable_live = false;
    if !enable_live {
        eprintln!("Insecure TLS test skipped");
        return;
    }

    let mut option = ClientOption::new();
    option.set_server_host("127.0.0.1".to_string());
    option.set_server_port(50505);
    option.set_connect_timeout(1000);
    option.set_read_timeout(1000);
    option.set_verify_certificate(false);
    option.set_tls_certificate(None);

    let result = SocketHelper::create_secure_socket(&option);
    assert!(result.is_ok());
}

#[test]
fn test_create_secure_socket_live_tls_connection_should_succeed() {
    let enable_live = false;
    if !enable_live {
        eprintln!("Live TLS test skipped");
        return;
    }

    let mut option = ClientOption::new();
    option.set_server_host("127.0.0.1".to_string());
    option.set_server_port(50505);
    option.set_connect_timeout(1000);
    option.set_read_timeout(1000);
    option.set_verify_certificate(true);
    option.set_tls_certificate(Some(VALID_PEM_CERT.to_string()));

    let result = SocketHelper::create_secure_socket(&option);
    assert!(result.is_ok());
}
