/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
use std::io::{Write, BufReader, BufWriter};
use crate::comm::gcm_crypto_helper::GcmCryptoHelper;
use crate::comm::message_codec::{MessageCodec, DEFAULT_PROTOCOL_VERSION, PROTOCOL_VERSION_AES_GCM, PROTOCOL_VERSION_RSA_OAEP_SHA256};
use crate::comm::socket_helper::SocketHelper;
use crate::comm::key_parser_helper::KeyParserHelper;
use crate::comm::read_write_stream::ReadWriteStream;
use crate::model::client_option::ClientOption;
use crate::model::encryption_mode::EncryptionMode;
use crate::model::vertex_cache_sdk_exception::VertexCacheSdkException;

pub struct ClientConnector {
    options: ClientOption,
    stream: Option<Box<dyn ReadWriteStream>>,
    connected: bool,
}

impl ClientConnector {
    pub fn new(options: ClientOption) -> Self {
        ClientConnector {
            options,
            stream: None,
            connected: false,
        }
    }

    pub fn connect(&mut self) -> Result<(), VertexCacheSdkException> {
        let mut boxed_stream: Box<dyn ReadWriteStream> = if self.options.enable_tls_encryption() {
            Box::new(SocketHelper::create_secure_socket(&self.options)?)
        } else {
            Box::new(SocketHelper::create_socket_non_tls(&self.options)?)
        };

        // Send IDENT command
        {
            let mut writer = BufWriter::new(&mut *boxed_stream);
            let ident_command = self.options.build_ident_command();
            let to_send = self.encrypt_if_enabled(ident_command.as_bytes())?;
            MessageCodec::write_framed_message(&mut writer, &to_send, self.resolve_protocol_version())?;
            writer.flush()?;
        }

        // Read server response
        {
            let mut reader = BufReader::new(&mut *boxed_stream);
            let response = MessageCodec::read_framed_message(&mut reader)?;
            let (_, payload) = response.ok_or_else(|| VertexCacheSdkException::new("Missing payload"))?;
            let response_str = String::from_utf8_lossy(&payload).trim().to_string();
            if !response_str.starts_with("+OK") {
                return Err(VertexCacheSdkException::new(&format!("Authorization failed: {}", response_str)));
            }
        }

        self.stream = Some(boxed_stream);
        self.connected = true;
        Ok(())
    }

    pub fn send(&mut self, message: &str) -> Result<String, VertexCacheSdkException> {
        let to_send = self.encrypt_if_enabled(message.as_bytes())?;
        let protocol_version = self.resolve_protocol_version();

        if let Some(stream) = &mut self.stream {
            // Write message
            {
                let mut writer = BufWriter::new(&mut **stream);
                MessageCodec::write_framed_message(&mut writer, &to_send, protocol_version)?;
                writer.flush()?;
            }

            // Read response
            {
                let mut reader = BufReader::new(&mut **stream);
                let response = MessageCodec::read_framed_message(&mut reader)?;
                let (_, payload) = response.ok_or_else(|| VertexCacheSdkException::new("Missing payload"))?;
                return Ok(String::from_utf8_lossy(&payload).to_string());
            }
        }

        Err(VertexCacheSdkException::new("No active connection"))
    }

    fn encrypt_if_enabled(&self, plain: &[u8]) -> Result<Vec<u8>, VertexCacheSdkException> {
        match self.options.encryption_mode() {
            EncryptionMode::ASYMMETRIC => {
                let pem = self
                    .options
                    .public_key()
                    .ok_or_else(|| VertexCacheSdkException::new("Missing public key"))?;
                KeyParserHelper::encrypt_with_rsa(pem, plain)
            }
            EncryptionMode::SYMMETRIC => {
                if let Some(key) = self.options.shared_encryption_key() {
                    let key_bytes = key.as_bytes();
                    GcmCryptoHelper::encrypt(plain, key_bytes)
                        .map_err(|_| VertexCacheSdkException::new("AES-GCM encryption failed"))
                } else {
                    Err(VertexCacheSdkException::new("Missing shared encryption key"))
                }
            }
            EncryptionMode::NONE => Ok(plain.to_vec()),
        }
    }

    pub fn resolve_protocol_version(&self) -> u32 {
        match self.options.encryption_mode() {
            EncryptionMode::ASYMMETRIC => PROTOCOL_VERSION_RSA_OAEP_SHA256,
            EncryptionMode::SYMMETRIC => PROTOCOL_VERSION_AES_GCM,
            EncryptionMode::NONE => DEFAULT_PROTOCOL_VERSION,
        }
    }

    pub fn is_connected(&self) -> bool {
        self.connected && self.stream.is_some()
    }

    pub fn close(&mut self) {
        self.stream = None;
        self.connected = false;
    }
}
