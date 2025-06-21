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

use aes_gcm::{Aes256Gcm, Key, KeyInit, Nonce};
use aes_gcm::aead::{Aead, OsRng, rand_core::RngCore};
use base64::{engine::general_purpose, Engine};
use crate::model::vertex_cache_sdk_exception::VertexCacheSdkException;

pub struct GcmCryptoHelper;

impl GcmCryptoHelper {
    const NONCE_SIZE: usize = 12;

    pub fn encrypt(plain: &[u8], key_bytes: &[u8]) -> Result<Vec<u8>, &'static str> {
        if key_bytes.len() != 32 {
            return Err("Invalid AES key length. Must be 32 bytes for AES-256-GCM");
        }

        let key = Key::<Aes256Gcm>::from_slice(key_bytes);
        let cipher = Aes256Gcm::new(key);

        let mut nonce_bytes = [0u8; Self::NONCE_SIZE];
        OsRng.fill_bytes(&mut nonce_bytes);
        let nonce = Nonce::from_slice(&nonce_bytes);

        match cipher.encrypt(nonce, plain) {
            Ok(mut ciphertext) => {
                let mut result = nonce_bytes.to_vec();
                result.append(&mut ciphertext);
                Ok(result)
            }
            Err(_) => Err("AES-GCM encryption failed"),
        }
    }

    pub fn decrypt(encrypted: &[u8], key_bytes: &[u8]) -> Result<Vec<u8>, &'static str> {
        if key_bytes.len() != 32 {
            return Err("Invalid AES key length. Must be 32 bytes for AES-256-GCM");
        }
        if encrypted.len() <= Self::NONCE_SIZE {
            return Err("Invalid encrypted data. Too short.");
        }

        let (nonce_bytes, ciphertext) = encrypted.split_at(Self::NONCE_SIZE);
        let key = Key::<Aes256Gcm>::from_slice(key_bytes);
        let cipher = Aes256Gcm::new(key);
        let nonce = Nonce::from_slice(nonce_bytes);

        cipher.decrypt(nonce, ciphertext).map_err(|_| "AES-GCM decryption failed")
    }

    pub fn encode_base64_key(raw: &[u8]) -> String {
        general_purpose::STANDARD.encode(raw)
    }

    pub fn decode_base64_key(encoded: &str) -> Result<Vec<u8>, base64::DecodeError> {
        general_purpose::STANDARD.decode(encoded.trim())
    }

    pub fn generate_base64_key() -> String {
        let mut key = [0u8; 32];
        OsRng.fill_bytes(&mut key);
        Self::encode_base64_key(&key)
    }

    pub fn encrypt_with_fixed_iv(
        plaintext: &[u8],
        key: &[u8],
        iv: &[u8],
    ) -> Result<Vec<u8>, VertexCacheSdkException> {
        let cipher = Aes256Gcm::new(aes_gcm::Key::<Aes256Gcm>::from_slice(key));
        let nonce = Nonce::from_slice(iv);
        cipher.encrypt(nonce, plaintext).map_err(|_| {
            VertexCacheSdkException::new("AES-GCM encryption failed with fixed IV")
        })
    }

    pub fn decrypt_with_fixed_iv(
        ciphertext: &[u8],
        key: &[u8],
        iv: &[u8],
    ) -> Result<Vec<u8>, VertexCacheSdkException> {
        let cipher = Aes256Gcm::new(aes_gcm::Key::<Aes256Gcm>::from_slice(key));
        let nonce = Nonce::from_slice(iv);
        cipher.decrypt(nonce, ciphertext).map_err(|_| {
            VertexCacheSdkException::new("AES-GCM decryption failed with fixed IV")
        })
    }
}