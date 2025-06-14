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

use base64::{engine::general_purpose::STANDARD, Engine as _};
use rsa::{Oaep, pkcs8::DecodePublicKey, RsaPublicKey};
use sha2::Sha256;
use rand::thread_rng;

use crate::model::vertex_cache_sdk_exception::VertexCacheSdkException;

pub struct KeyParserHelper;

impl KeyParserHelper {
    pub fn public_key_as_bytes(pem: &str) -> Result<Vec<u8>, VertexCacheSdkException> {
        let cleaned = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .split_whitespace()
            .collect::<String>();

        let decoded = STANDARD
            .decode(cleaned)
            .map_err(|_| VertexCacheSdkException::new("Invalid public key"))?;

        RsaPublicKey::from_public_key_der(&decoded)
            .map_err(|_| VertexCacheSdkException::new("Invalid public key"))?;

        Ok(decoded)
    }

    pub fn public_key_as_object(pem: &str) -> Result<RsaPublicKey, VertexCacheSdkException> {
        RsaPublicKey::from_public_key_pem(pem)
            .map_err(|_| VertexCacheSdkException::new("Invalid public key"))
    }

    pub fn encrypt_with_rsa(pem: &str, plain: &[u8]) -> Result<Vec<u8>, VertexCacheSdkException> {
        let public_key = Self::public_key_as_object(pem)?;
        public_key
            .encrypt(&mut thread_rng(), Oaep::new::<Sha256>(), plain)
            .map_err(|_| VertexCacheSdkException::new("RSA encryption failed"))
    }

    pub fn shared_key_as_bytes(base64: &str) -> Result<Vec<u8>, VertexCacheSdkException> {
        let decoded = STANDARD
            .decode(base64)
            .map_err(|_| VertexCacheSdkException::new("Invalid shared key"))?;

        if decoded.len() != 16 && decoded.len() != 32 {
            return Err(VertexCacheSdkException::new("Invalid shared key"));
        }

        let reencoded = STANDARD.encode(&decoded);
        if reencoded != base64.replace('\n', "").replace('\r', "") {
            return Err(VertexCacheSdkException::new("Invalid shared key"));
        }

        Ok(decoded)
    }
}
