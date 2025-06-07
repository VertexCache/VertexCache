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

use aes_gcm::{Aes256Gcm, KeyInit, Nonce};
use aes_gcm::aead::{Aead, OsRng, rand_core::RngCore};
use base64::{engine::general_purpose, Engine as _};
use thiserror::Error;

pub struct GcmCryptoHelper;

#[derive(Debug, Error)]
pub enum GcmCryptoError {
    #[error("encryption failed")]
    EncryptionFailed,

    #[error("decryption failed")]
    DecryptionFailed,

    #[error("input too short")]
    InvalidInputLength,

    #[error("base64 decode error")]
    Base64Error(#[from] base64::DecodeError),
}

impl GcmCryptoHelper {
    pub fn encrypt(plaintext: &[u8], key: &[u8]) -> Result<Vec<u8>, GcmCryptoError> {
        let cipher = Aes256Gcm::new_from_slice(key).map_err(|_| GcmCryptoError::EncryptionFailed)?;
        let mut iv = [0u8; 12];
        OsRng.fill_bytes(&mut iv);
        let nonce = Nonce::from_slice(&iv);

        let ciphertext = cipher.encrypt(nonce, plaintext).map_err(|_| GcmCryptoError::EncryptionFailed)?;

        let mut result = iv.to_vec();
        result.extend_from_slice(&ciphertext);
        Ok(result)
    }

    pub fn decrypt(ciphertext: &[u8], key: &[u8]) -> Result<Vec<u8>, GcmCryptoError> {
        if ciphertext.len() < 12 {
            return Err(GcmCryptoError::InvalidInputLength);
        }

        let (iv, cipher_data) = ciphertext.split_at(12);
        let cipher = Aes256Gcm::new_from_slice(key).map_err(|_| GcmCryptoError::DecryptionFailed)?;
        let nonce = Nonce::from_slice(iv);

        cipher.decrypt(nonce, cipher_data).map_err(|_| GcmCryptoError::DecryptionFailed)
    }

    pub fn encrypt_with_fixed_iv(plaintext: &[u8], key: &[u8], iv: &[u8]) -> Result<Vec<u8>, GcmCryptoError> {
        if iv.len() != 12 {
            return Err(GcmCryptoError::InvalidInputLength);
        }

        let cipher = Aes256Gcm::new_from_slice(key).map_err(|_| GcmCryptoError::EncryptionFailed)?;
        let nonce = Nonce::from_slice(iv);
        let ciphertext = cipher.encrypt(nonce, plaintext).map_err(|_| GcmCryptoError::EncryptionFailed)?;

        Ok(ciphertext) // DO NOT prepend IV
    }


    pub fn decrypt_with_fixed_iv(ciphertext: &[u8], key: &[u8], iv: &[u8]) -> Result<Vec<u8>, GcmCryptoError> {
        if iv.len() != 12 {
            return Err(GcmCryptoError::InvalidInputLength);
        }

        let cipher = Aes256Gcm::new_from_slice(key).map_err(|_| GcmCryptoError::DecryptionFailed)?;
        let nonce = Nonce::from_slice(iv);
        let plaintext = cipher.decrypt(nonce, ciphertext).map_err(|_| GcmCryptoError::DecryptionFailed)?;

        Ok(plaintext)
    }

    pub fn encode_base64(bytes: &[u8]) -> String {
        general_purpose::STANDARD.encode(bytes)
    }

    pub fn decode_base64(encoded: &str) -> Result<Vec<u8>, GcmCryptoError> {
        Ok(general_purpose::STANDARD.decode(encoded)?)
    }
}