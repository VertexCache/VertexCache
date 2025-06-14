use base64::{engine::general_purpose::STANDARD, Engine as _};
use rsa::pkcs1v15::Pkcs1v15Encrypt;
use rsa::{pkcs8::DecodePublicKey, RsaPublicKey};
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
            .encrypt(&mut thread_rng(), Pkcs1v15Encrypt, plain)
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
