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
use vertexcache_sdk::model::vertex_cache_sdk_exception::VertexCacheSdkException;

#[test]
fn test_write_then_read_framed_message() {
    let payload = b"ping".to_vec();
    let mut out = Vec::new();

    MessageCodec::write_framed_message(&mut out, &payload).unwrap();

    let mut reader = Cursor::new(out);
    let result = MessageCodec::read_framed_message(&mut reader).unwrap().unwrap();

    assert_eq!(result, payload);
}

#[test]
fn test_invalid_version_byte() {
    let mut invalid = vec![0x00, 0x00, 0x00, 0x04, 0x00];
    let mut reader = Cursor::new(invalid);

    let result = MessageCodec::read_framed_message(&mut reader);
    assert!(result.is_err());
    assert_eq!(result.unwrap_err().message(), "Invalid version byte");
}

#[test]
fn test_too_short_header_returns_none() {
    let short = vec![0x01, 0x00];
    let mut reader = Cursor::new(short);

    let result = MessageCodec::read_framed_message(&mut reader).unwrap();
    assert!(result.is_none());
}

#[test]
fn test_too_large_payload_rejected() {
    let payload = vec![0xAB; MessageCodec::MAX_MESSAGE_SIZE + 1];
    let mut out = Vec::new();

    let result = MessageCodec::write_framed_message(&mut out, &payload);
    assert!(result.is_err());
    assert_eq!(result.unwrap_err().message(), "Payload too large");
}

#[test]
fn test_write_empty_payload_then_read_should_fail() {
    let mut out = Vec::new();
    let result = MessageCodec::write_framed_message(&mut out, b"");

    assert!(result.is_err());
    assert_eq!(result.unwrap_err().message(), "Payload must be non-empty");
}

#[test]
fn test_utf8_multibyte_payload() {
    let original = "你好, VertexCache 🚀".as_bytes().to_vec();
    let mut out = Vec::new();

    MessageCodec::write_framed_message(&mut out, &original).unwrap();
    let mut reader = Cursor::new(out);
    let result = MessageCodec::read_framed_message(&mut reader).unwrap().unwrap();

    assert_eq!(String::from_utf8(result).unwrap(), "你好, VertexCache 🚀");
}

#[test]
fn test_hex_dump_for_inter_sdk_comparison() {
    let payload = b"ping".to_vec();
    let mut out = Vec::new();

    MessageCodec::write_framed_message(&mut out, &payload).unwrap();

    print!("Framed hex: ");
    for byte in &out {
        print!("{:02X}", byte);
    }
    println!();
}
