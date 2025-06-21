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
use vertexcache_sdk::model::vertex_cache_sdk_exception::VertexCacheSdkException;

#[test]
fn test_base64_key_encoding_and_decoding_should_succeed() {
    let bytes: [u8; 32] = [
        0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88,
        0x99, 0xaa, 0xbb, 0xcc, 0xdd, 0xee, 0xff, 0x00,
        0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x80,
        0x90, 0xa0, 0xb0, 0xc0, 0xd0, 0xe0, 0xf0, 0x01,
    ];

    let encoded = GcmCryptoHelper::encode_base64_key(&bytes);
    let decoded = GcmCryptoHelper::decode_base64_key(&encoded).unwrap();

    assert_eq!(decoded, bytes);
}

#[test]
fn test_encrypt_and_decrypt_with_fixed_iv_should_succeed() {
    let key: [u8; 32] = [
        0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67,
        0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F,
        0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77,
        0x78, 0x79, 0x7A, 0x7B, 0x7C, 0x7D, 0x7E, 0x7F,
    ];

    let iv: [u8; 12] = [0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 0xA7, 0xA8, 0xA9, 0xAA, 0xAB, 0xAC];
    let plaintext = b"this is a test";

    let encrypted = GcmCryptoHelper::encrypt_with_fixed_iv(plaintext, &key, &iv)
        .expect("Encryption failed");

    assert_ne!(encrypted, plaintext);

    let decrypted = GcmCryptoHelper::decrypt_with_fixed_iv(&encrypted, &key, &iv)
        .expect("Decryption failed");

    assert_eq!(decrypted, plaintext);
}
