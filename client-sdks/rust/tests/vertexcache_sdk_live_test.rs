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

use std::env;
use vertexcache_sdk::model::*;
use vertexcache_sdk::vertexcache_sdk::VertexCacheSDK;
use vertexcache_sdk::model::client_option::ClientOption;
use vertexcache_sdk::model::encryption_mode::EncryptionMode;



const HOST: &str = "127.0.0.1";
const PORT: u16 = 50505;
const CLIENT_ID: &str = "sdk-client-rust";
const CLIENT_TOKEN: &str = "635006e5-65d4-4cff-a0d4-197ecf2b3be3";
const TEST_PUBLIC_KEY: &str = "-----BEGIN PUBLIC KEY-----\n\
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\n\
bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\n\
UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\n\
GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\n\
NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n\
6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\n\
EwIDAQAB\n-----END PUBLIC KEY-----";

fn get_test_client_option() -> ClientOption {
    let mut option = ClientOption::new();
    option.set_client_id(Some(CLIENT_ID.to_string()));
    option.set_client_token(Some(CLIENT_TOKEN.to_string()));
    option.set_server_host(HOST.to_string());
    option.set_server_port(PORT);
    option.set_enable_tls_encryption(true);
    option.set_verify_certificate(false);
    option.set_tls_certificate(None); // Only needed if verify=true
    option.set_encryption_mode(EncryptionMode::ASYMMETRIC);
    option.set_public_key(Some(TEST_PUBLIC_KEY.to_string()));
    option
}


#[test]
#[serial_test::serial]
fn test_01_ping_should_succeed() {
    if env::var("VC_LIVE_TLS_ASYMMETRIC_TEST").unwrap_or_default() != "true" {
        return;
    }

    let mut sdk = VertexCacheSDK::new(get_test_client_option());
    sdk.open_connection().unwrap();
    let result = sdk.ping();
    assert!(result.success);
    assert!(result.message.starts_with("PONG"));
    sdk.close();
}

#[test]
#[serial_test::serial]
fn test_02_set_should_succeed() {
    let mut sdk = VertexCacheSDK::new(get_test_client_option());
    sdk.open_connection().unwrap();
    let result = sdk.set("test-key", "value-123", None, None);
    assert!(
        result.success,
        "Set failed: {}",
        result.message
    );
    assert_eq!(result.message, "OK");
    sdk.close();
}

#[test]
#[serial_test::serial]
fn test_03_get_should_return_set_value() {
    let mut sdk = VertexCacheSDK::new(get_test_client_option());
    sdk.open_connection().unwrap();
    sdk.set("test-key", "value-123", None, None);
    let result = sdk.get("test-key");
    assert!(result.success);
    assert_eq!(result.value.unwrap(), "value-123");
    sdk.close();
}

#[test]
#[serial_test::serial]
fn test_04_del_should_remove_key() {
    let mut sdk = VertexCacheSDK::new(get_test_client_option());
    sdk.open_connection().unwrap();
    sdk.set("delete-key", "to-be-deleted", None, None);
    let del = sdk.del("delete-key");
    assert!(del.success);
    let result = sdk.get("delete-key");
    assert!(result.success);
    assert!(result.value.is_none());
    sdk.close();
}

#[test]
#[serial_test::serial]
fn test_05_get_missing_key_should_return_nil() {
    let mut sdk = VertexCacheSDK::new(get_test_client_option());
    sdk.open_connection().unwrap();
    let result = sdk.get("nonexistent-key");
    assert!(result.success);
    assert!(result.value.is_none());
    sdk.close();
}

#[test]
#[serial_test::serial]
fn test_06_set_secondary_index_should_succeed() {
    let mut sdk = VertexCacheSDK::new(get_test_client_option());
    sdk.open_connection().unwrap();
    let result = sdk.set("key-sec", "val-sec", Some("sec-idx".to_string()), None);
    assert!(result.success);
    assert_eq!(result.message, "OK");
    sdk.close();
}

#[test]
#[serial_test::serial]
fn test_07_set_secondary_and_tertiary_index_should_succeed() {
    let mut sdk = VertexCacheSDK::new(get_test_client_option());
    sdk.open_connection().unwrap();
    let result = sdk.set("key-ter", "val-ter", Some("sec-idx".to_string()), Some("ter-idx".to_string()));
    assert!(result.success);
    assert_eq!(result.message, "OK");
    sdk.close();
}

#[test]
#[serial_test::serial]
fn test_08_get_by_secondary_should_return_value() {
    let mut sdk = VertexCacheSDK::new(get_test_client_option());
    sdk.open_connection().unwrap();
    sdk.set("key-sec", "val-sec", Some("sec-idx".to_string()), None);
    let result = sdk.get_by_secondary_index("sec-idx");
    assert!(result.success);
    assert_eq!(result.value.unwrap(), "val-sec");
    sdk.close();
}

#[test]
#[serial_test::serial]
fn test_09_get_by_tertiary_should_return_value() {
    let mut sdk = VertexCacheSDK::new(get_test_client_option());
    sdk.open_connection().unwrap();
    sdk.set("key-ter", "val-ter", Some("sec-idx".to_string()), Some("ter-idx".to_string()));
    let result = sdk.get_by_tertiary_index("ter-idx");
    assert!(result.success);
    assert_eq!(result.value.unwrap(), "val-ter");
    sdk.close();
}
