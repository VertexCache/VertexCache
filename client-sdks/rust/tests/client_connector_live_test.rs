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
    option.set_encryption_mode(EncryptionMode::ASYMMETRIC);
    option.set_public_key(Some(TEST_PUBLIC_KEY.to_string()));

    let mut client = ClientConnector::new(option);
    client.connect().expect("Should connect successfully");
    assert!(client.is_connected());

    let response = client.send("PING").expect("Should send and receive PING");
    assert!(response.starts_with("+PONG"));

    client.close();
    assert!(!client.is_connected());
}
