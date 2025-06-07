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
use vertexcache_sdk::comm::key_parser_helper::{
    config_public_key_if_enabled, config_shared_key_if_enabled,
};
use vertexcache_sdk::model::vertex_cache_sdk_exception::VertexCacheSdkException;

const VALID_PEM: &str = "
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
EwIDAQAB
-----END PUBLIC KEY-----
";

const INVALID_PEM: &str = "-----BEGIN PUBLIC KEY-----INVALID-----END PUBLIC KEY-----";
const VALID_BASE64: &str = "YWJjZGVmZ2hpamtsbW5vcA=="; // "abcdefghijklmnop"
const INVALID_BASE64: &str = "%%%INVALID%%%";

#[test]
fn test_config_public_key_if_enabled_valid() {
    let result = config_public_key_if_enabled(VALID_PEM);
    assert!(result.is_ok());
    assert!(!result.unwrap().is_empty());
}

#[test]
fn test_config_public_key_if_enabled_invalid() {
    let err = config_public_key_if_enabled(INVALID_PEM).unwrap_err();
    assert_eq!(err.message(), "Invalid public key");
}

#[test]
fn test_config_shared_key_if_enabled_valid() {
    let result = config_shared_key_if_enabled(VALID_BASE64).unwrap();
    assert_eq!(result.len(), 16);
    assert_eq!(result, b"abcdefghijklmnop");
}

#[test]
fn test_config_shared_key_if_enabled_invalid() {
    let err = config_shared_key_if_enabled(INVALID_BASE64).unwrap_err();
    assert_eq!(err.message(), "Invalid shared key");
}
