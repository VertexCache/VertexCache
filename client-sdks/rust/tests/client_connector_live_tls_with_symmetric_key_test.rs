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

use vertexcache_sdk::comm::client_connector::ClientConnector;
use vertexcache_sdk::model::client_option::ClientOption;
use vertexcache_sdk::model::encryption_mode::EncryptionMode;
use std::env;

const HOST: &str = "127.0.0.1";
const PORT: u16 = 50505;
const CLIENT_ID: &str = "sdk-client-rust";
const CLIENT_TOKEN: &str = "635006e5-65d4-4cff-a0d4-197ecf2b3be3";

const TEST_SHARED_KEY: &str = "neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc=";

#[test]
fn test_live_connect_and_ping_should_succeed() {
    if env::var("VC_LIVE_TEST").unwrap_or_default() != "true" {
        eprintln!("Skipping live test, set VC_LIVE_TEST=true to enable.");
       return;
    }

    let mut option = ClientOption::new();
    option.set_client_id(Some(CLIENT_ID.to_string()));
    option.set_client_token(Some(CLIENT_TOKEN.to_string()));
    option.set_server_host(HOST.to_string());
    option.set_server_port(PORT);
    option.set_enable_tls_encryption(true);
    option.set_verify_certificate(false);
    option.set_tls_certificate(None); // Only required if verify is true
    option.set_encryption_mode(EncryptionMode::SYMMETRIC);
    option.set_shared_encryption_key(Some(TEST_SHARED_KEY.to_string()));

    let mut client = ClientConnector::new(option);
    client.connect().expect("Should connect successfully");
    assert!(client.is_connected());

    let response = client.send("PING").expect("Should send and receive PING");
    assert!(response.starts_with("+PONG"));

    client.close();
    assert!(!client.is_connected());
}
