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

use crate::model::encryption_mode::EncryptionMode;
use crate::model::vertex_cache_sdk_exception::VertexCacheSdkException;
use rsa::{pkcs8::DecodePublicKey, RsaPublicKey};
use std::str;

pub struct ClientOption {
    client_id: Option<String>,
    client_token: Option<String>,

    server_host: String,
    server_port: u16,

    enable_tls_encryption: bool,
    tls_certificate: Option<String>,
    verify_certificate: bool,

    encryption_mode: EncryptionMode,
    encrypt_with_public_key: bool,
    encrypt_with_shared_key: bool,

    public_key: Option<String>,
    shared_encryption_key: Option<String>,

    read_timeout: u32,
    connect_timeout: u32,
}

impl ClientOption {
    pub const DEFAULT_CLIENT_ID: &'static str = "sdk-client";
    pub const DEFAULT_HOST: &'static str = "127.0.0.1";
    pub const DEFAULT_PORT: u16 = 50505;
    pub const DEFAULT_READ_TIMEOUT: u32 = 3000;
    pub const DEFAULT_CONNECT_TIMEOUT: u32 = 3000;

    pub fn new() -> Self {
        Self {
            client_id: Some(Self::DEFAULT_CLIENT_ID.to_string()),
            client_token: None,
            server_host: Self::DEFAULT_HOST.to_string(),
            server_port: Self::DEFAULT_PORT,
            enable_tls_encryption: false,
            tls_certificate: None,
            verify_certificate: false,
            encryption_mode: EncryptionMode::NONE,
            encrypt_with_public_key: false,
            encrypt_with_shared_key: false,
            public_key: None,
            shared_encryption_key: None,
            read_timeout: Self::DEFAULT_READ_TIMEOUT,
            connect_timeout: Self::DEFAULT_CONNECT_TIMEOUT,
        }
    }

    pub fn resolve_protocol_version(&self) -> u32 {
        match self.encryption_mode() {
            EncryptionMode::ASYMMETRIC => 0x00000201,
            EncryptionMode::SYMMETRIC => 0x00000801,
            EncryptionMode::NONE => 0x00000001,
        }
    }

    pub fn public_key_as_object(&self) -> Result<RsaPublicKey, VertexCacheSdkException> {
        let pem = self.public_key.as_ref().ok_or_else(|| {
            VertexCacheSdkException::new("Missing public key for asymmetric encryption")
        })?;

        RsaPublicKey::from_public_key_pem(pem).map_err(|_| {
            VertexCacheSdkException::new("Failed to parse RSA public key from PEM")
        })
    }

    pub fn shared_encryption_key_as_bytes(&self) -> Result<&[u8], VertexCacheSdkException> {
        self.shared_encryption_key
            .as_ref()
            .map(|key| key.as_bytes())
            .ok_or_else(|| {
                VertexCacheSdkException::new("Missing shared encryption key for symmetric mode")
        })
    }

    pub fn get_client_id(&self) -> String {
        self.client_id.clone().unwrap_or_default()
    }

    pub fn get_client_token(&self) -> String {
        self.client_token.clone().unwrap_or_default()
    }

    pub fn build_ident_command(&self) -> String {
        format!(
            "IDENT {{\"client_id\":\"{}\", \"token\":\"{}\"}}",
            self.get_client_id(),
            self.get_client_token()
        )
    }

    // Setters
    pub fn set_client_id(&mut self, value: Option<String>) {
        self.client_id = value;
    }

    pub fn set_client_token(&mut self, value: Option<String>) {
        self.client_token = value;
    }

    pub fn set_server_host(&mut self, value: String) {
        self.server_host = value;
    }

    pub fn set_server_port(&mut self, value: u16) {
        self.server_port = value;
    }

    pub fn set_enable_tls_encryption(&mut self, value: bool) {
        self.enable_tls_encryption = value;
    }

    pub fn set_tls_certificate(&mut self, value: Option<String>) {
        self.tls_certificate = value;
    }

    pub fn set_verify_certificate(&mut self, value: bool) {
        self.verify_certificate = value;
    }

    pub fn set_encryption_mode(&mut self, value: EncryptionMode) {
        self.encryption_mode = value;
    }

    pub fn set_read_timeout(&mut self, value: u32) {
        self.read_timeout = value;
    }

    pub fn set_connect_timeout(&mut self, value: u32) {
        self.connect_timeout = value;
    }

    pub fn set_public_key(&mut self, value: Option<String>) {
        self.public_key = value;
    }

    pub fn set_shared_encryption_key(&mut self, value: Option<String>) {
        self.shared_encryption_key = value;
    }

    // Getters
    pub fn server_host(&self) -> &str {
        &self.server_host
    }

    pub fn server_port(&self) -> u16 {
        self.server_port
    }

    pub fn tls_certificate(&self) -> Option<&String> {
        self.tls_certificate.as_ref()
    }

    pub fn verify_certificate(&self) -> bool {
        self.verify_certificate
    }

    pub fn enable_tls_encryption(&self) -> bool {
        self.enable_tls_encryption
    }

    pub fn encryption_mode(&self) -> EncryptionMode {
        self.encryption_mode
    }

    pub fn read_timeout(&self) -> u32 {
        self.read_timeout
    }

    pub fn connect_timeout(&self) -> u32 {
        self.connect_timeout
    }

    pub fn public_key(&self) -> Option<&String> {
        self.public_key.as_ref()
    }

    pub fn shared_encryption_key(&self) -> Option<&String> {
        self.shared_encryption_key.as_ref()
    }

    pub fn get_shared_encryption_key_as_bytes(&self) -> Result<Vec<u8>, VertexCacheSdkException> {
        match &self.shared_encryption_key {
            Some(b64) => {
                match base64::decode(b64) {
                    Ok(bytes) => {
                        Ok(bytes)
                    },
                    Err(_) => Err(VertexCacheSdkException::new("Invalid base64 shared key")),
                }
            }
            None => Err(VertexCacheSdkException::new("Missing shared key")),
        }
    }
}
