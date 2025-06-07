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

use vertexcache_sdk::comm::gcm_crypto_helper::GcmCryptoHelper;

#[test]
fn test_encrypt_decrypt_round_trip() {
    let plaintext = b"VertexCacheGCMTest";
    let key = [0u8; 32];

    let encrypted = GcmCryptoHelper::encrypt(plaintext, &key).unwrap();
    let decrypted = GcmCryptoHelper::decrypt(&encrypted, &key).unwrap();

    assert_eq!(decrypted, plaintext);
}

#[test]
fn test_decrypt_fails_on_tampered_ciphertext() {
    let plaintext = b"VertexCacheGCMTest";
    let key = [0u8; 32];

    let mut encrypted = GcmCryptoHelper::encrypt(plaintext, &key).unwrap();
    let len = encrypted.len();
    encrypted[len - 1] ^= 0xFF; // flip last byte

    assert!(GcmCryptoHelper::decrypt(&encrypted, &key).is_err());
}

#[test]
fn test_decrypt_fails_if_too_short() {
    let key = [0u8; 32];
    let short_ciphertext = vec![0x00, 0x01, 0x02]; // shorter than 12-byte IV

    assert!(GcmCryptoHelper::decrypt(&short_ciphertext, &key).is_err());
}

#[test]
fn test_base64_encode_decode_round_trip() {
    let bytes = b"12345678901234567890123456789012"; // 32 bytes
    let encoded = GcmCryptoHelper::encode_base64(bytes);
    let decoded = GcmCryptoHelper::decode_base64(&encoded).unwrap();
    assert_eq!(decoded, bytes);
}

#[test]
fn test_reconciliation_with_fixed_iv() {
    let plaintext = b"VertexCacheGCMTest";
    let key = [0u8; 32];
    let iv = [0u8; 12];

    let encrypted = GcmCryptoHelper::encrypt_with_fixed_iv(plaintext, &key, &iv).unwrap();
    let decrypted = GcmCryptoHelper::decrypt_with_fixed_iv(&encrypted, &key, &iv).unwrap();

    println!("[RECON] Plaintext: {:?}", String::from_utf8_lossy(plaintext));
    println!("[RECON] Key (hex): {}", hex::encode(&key));
    println!("[RECON] IV (hex): {}", hex::encode(&iv));
    println!("[RECON] Encrypted (hex): {}", hex::encode(&encrypted));

    assert_eq!(plaintext, &decrypted[..]);
}
