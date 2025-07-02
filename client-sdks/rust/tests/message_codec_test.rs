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
// ------------------------------------------------------------------------------
use std::io::Cursor;
use serial_test::serial;
use vertexcache_sdk::comm::message_codec::{
    MessageCodec, PROTOCOL_VERSION_AES_GCM, PROTOCOL_VERSION_RSA_OAEP_SHA256
};

#[test]
#[serial]
fn test_framed_message_round_trip_aes_gcm() {
    MessageCodec::switch_to_symmetric();
    let payload = b"vertexcache-aes".to_vec();
    let mut out = Vec::new();

    MessageCodec::write_framed_message(&mut out, &payload).unwrap();
    let mut reader = Cursor::new(out);

    let result = MessageCodec::read_framed_message(&mut reader).unwrap().unwrap();
    assert_eq!(result.1, payload);
    assert_eq!(result.0, PROTOCOL_VERSION_AES_GCM);
}

#[test]
#[serial]
fn test_framed_message_round_trip_rsa_oaep() {
    MessageCodec::switch_to_asymmetric();
    let payload = b"vertexcache-rsa".to_vec();
    let mut out = Vec::new();

    MessageCodec::write_framed_message(&mut out, &payload).unwrap();
    let mut reader = Cursor::new(out);

    let result = MessageCodec::read_framed_message(&mut reader).unwrap().unwrap();
    assert_eq!(result.1, payload);
    assert_eq!(result.0, PROTOCOL_VERSION_RSA_OAEP_SHA256);
}

#[test]
#[serial]
fn test_empty_payload_rejected() {
    let mut out = Vec::new();
    let err = MessageCodec::write_framed_message(&mut out, b"").unwrap_err();
    assert_eq!(err.message(), "Payload must be non-empty");
}

#[test]
#[serial]
fn test_too_large_payload_rejected() {
    let payload = vec![0xAB; MessageCodec::MAX_MESSAGE_SIZE + 1];
    let mut out = Vec::new();
    let err = MessageCodec::write_framed_message(&mut out, &payload).unwrap_err();
    assert_eq!(err.message(), "Payload too large");
}

#[test]
#[serial]
fn test_hex_dump_output() {
    MessageCodec::switch_to_symmetric();
    let payload = b"ping".to_vec();
    let hex = MessageCodec::hex_dump(&payload).unwrap();
    assert!(hex.len() > 0);
    assert!(hex.starts_with("00000004")); // 4 bytes payload length prefix
}
