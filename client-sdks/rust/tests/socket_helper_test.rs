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

use vertexcache_sdk::comm::socket_helper::{create_secure_socket, create_socket_non_tls};
use vertexcache_sdk::model::client_option::ClientOption;

const UNUSED_PORT: u16 = 65534;
const BLACKHOLE_IP: &str = "10.255.255.1";

const VALID_PEM_CERT: &str = r#"-----BEGIN CERTIFICATE-----
MIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV
...
-----END CERTIFICATE-----"#;

#[test]
fn test_create_socket_non_tls_should_fail_if_port_closed() {
    let mut option = ClientOption::new();
    option.set_server_host("127.0.0.1".to_string());
    option.set_server_port(UNUSED_PORT);
    option.set_connect_timeout(500);
    option.set_read_timeout(500);

    let result = create_socket_non_tls(&option);
    assert!(result.is_err());
    let err = result.unwrap_err();
    assert!(err.message().contains("Non Secure Socket"));
}

#[test]
fn test_create_socket_non_tls_should_fail_on_timeout() {
    let mut option = ClientOption::new();
    option.set_server_host(BLACKHOLE_IP.to_string());
    option.set_server_port(12345);
    option.set_connect_timeout(300);
    option.set_read_timeout(500);

    let result = create_socket_non_tls(&option);
    assert!(result.is_err());
    let err = result.unwrap_err();
    assert!(err.message().contains("Non Secure Socket"));
}

#[test]
fn test_create_secure_socket_should_fail_due_to_missing_tls_context() {
    let mut option = ClientOption::new();
    option.set_server_host("127.0.0.1".to_string());
    option.set_server_port(50505); // Live TLS port
    option.set_connect_timeout(1000);
    option.set_read_timeout(1000);
    option.set_verify_certificate(true);
    option.set_tls_certificate(None);

    let result = create_secure_socket(&option);
    assert!(result.is_err());
    let err = result.unwrap_err();
    assert!(
        err.message().contains("Secure Socket") ||
        err.message().contains("secure socket connection") ||
        err.message().contains("Missing TLS certificate")
    );
}

#[test]
fn test_create_secure_socket_should_fail_with_bad_certificate() {
    let mut option = ClientOption::new();
    option.set_server_host("127.0.0.1".to_string());
    option.set_server_port(50505);
    option.set_connect_timeout(1000);
    option.set_read_timeout(1000);
    option.set_verify_certificate(true);
    option.set_tls_certificate(Some("not a cert".to_string()));

    let result = create_secure_socket(&option);
    assert!(result.is_err());

    let err = result.unwrap_err();
    eprintln!("❗ Observed error message: {}", err.message()); // 👈 log it
    assert!(
        err.message().contains("Secure Socket") ||
        err.message().contains("secure socket connection") ||
        err.message().contains("TLS handshake failed") ||
        err.message().contains("Invalid certificate format")
    );
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

    let result = create_secure_socket(&option);
    assert!(result.is_ok());
}
