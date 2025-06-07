// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
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

use vertexcache_sdk::model::client_option::ClientOption;
use vertexcache_sdk::model::encryption_mode::EncryptionMode;

#[test]
fn test_defaults_should_be_correct() {
    let option = ClientOption::new();

    assert_eq!(option.get_client_id(), "sdk-client");
    assert_eq!(option.get_client_token(), "");
    assert_eq!(option.server_host(), "127.0.0.1");
    assert_eq!(option.server_port(), 50505);
    assert!(!option.enable_tls_encryption());
    assert!(!option.verify_certificate());
    assert_eq!(option.read_timeout(), 3000);
    assert_eq!(option.connect_timeout(), 3000);
    assert_eq!(option.encryption_mode(), EncryptionMode::NONE);
    assert!(option.build_ident_command().contains("IDENT"));
}

#[test]
fn test_set_values_should_be_correct() {
    let mut option = ClientOption::new();

    option.set_client_id(Some("test-client".to_string()));
    option.set_client_token(Some("token123".to_string()));
    option.set_server_host("192.168.1.100".to_string());
    option.set_server_port(9999);
    option.set_enable_tls_encryption(true);
    option.set_verify_certificate(true);
    option.set_tls_certificate(Some("cert".to_string()));
    option.set_connect_timeout(1234);
    option.set_read_timeout(5678);
    option.set_encryption_mode(EncryptionMode::SYMMETRIC);

    assert_eq!(option.get_client_id(), "test-client");
    assert_eq!(option.get_client_token(), "token123");
    assert_eq!(option.server_host(), "192.168.1.100");
    assert_eq!(option.server_port(), 9999);
    assert!(option.enable_tls_encryption());
    assert!(option.verify_certificate());
    assert_eq!(option.tls_certificate().unwrap(), "cert");
    assert_eq!(option.connect_timeout(), 1234);
    assert_eq!(option.read_timeout(), 5678);
    assert_eq!(option.encryption_mode(), EncryptionMode::SYMMETRIC);
}

#[test]
fn test_ident_command_generation_should_be_correct() {
    let mut option = ClientOption::new();
    option.set_client_id(Some("my-id".to_string()));
    option.set_client_token(Some("my-token".to_string()));
    let expected = "IDENT {\"client_id\":\"my-id\", \"token\":\"my-token\"}";
    assert_eq!(option.build_ident_command(), expected);
}

#[test]
fn test_null_token_and_id_fallback_should_be_empty() {
    let mut option = ClientOption::new();
    option.set_client_id(None);
    option.set_client_token(None);
    let ident = option.build_ident_command();
    assert!(ident.contains("\"client_id\":\"\""));
    assert!(ident.contains("\"token\":\"\""));
}
