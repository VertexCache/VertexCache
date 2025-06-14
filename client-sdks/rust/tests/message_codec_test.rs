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

use std::io::Cursor;

use vertexcache_sdk::comm::message_codec::MessageCodec;

#[test]
fn test_framed_message_round_trip_aes_gcm() {
    let payload = b"vertexcache-aes".to_vec();
    let mut out = Vec::new();

    MessageCodec::write_framed_message(&mut out, &payload, MessageCodec::PROTOCOL_VERSION_AES_GCM).unwrap();
    let mut reader = Cursor::new(out);

    let result = MessageCodec::read_framed_message(&mut reader).unwrap().unwrap();
    assert_eq!(result.1, payload);
    assert_eq!(result.0, MessageCodec::PROTOCOL_VERSION_AES_GCM);
}

#[test]
fn test_framed_message_round_trip_rsa_oaep() {
    let payload = b"vertexcache-rsa".to_vec();
    let mut out = Vec::new();

    MessageCodec::write_framed_message(&mut out, &payload, MessageCodec::PROTOCOL_VERSION_RSA_OAEP_SHA256).unwrap();
    let mut reader = Cursor::new(out);

    let result = MessageCodec::read_framed_message(&mut reader).unwrap().unwrap();
    assert_eq!(result.1, payload);
    assert_eq!(result.0, MessageCodec::PROTOCOL_VERSION_RSA_OAEP_SHA256);
}

#[test]
fn test_empty_payload_rejected() {
    let mut out = Vec::new();
    let err = MessageCodec::write_framed_message(&mut out, b"", MessageCodec::PROTOCOL_VERSION_AES_GCM).unwrap_err();
    assert_eq!(err.message(), "Payload must be non-empty");
}

#[test]
fn test_too_large_payload_rejected() {
    let payload = vec![0xAB; MessageCodec::MAX_MESSAGE_SIZE + 1];
    let mut out = Vec::new();
    let err = MessageCodec::write_framed_message(&mut out, &payload, MessageCodec::PROTOCOL_VERSION_AES_GCM).unwrap_err();
    assert_eq!(err.message(), "Payload too large");
}

#[test]
fn test_hex_dump_output() {
    let payload = b"ping".to_vec();
    let hex = MessageCodec::hex_dump(&payload, MessageCodec::PROTOCOL_VERSION_AES_GCM).unwrap();
    assert!(hex.len() > 0);
    assert!(hex.starts_with("00000004")); // length = 4 bytes
}
