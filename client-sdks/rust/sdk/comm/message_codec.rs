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

use std::io::{Read, Write};
use crate::model::vertex_cache_sdk_exception::VertexCacheSdkException;

pub struct MessageCodec;

// Active protocol versions
pub const PROTOCOL_VERSION_RSA_OAEP_SHA256: u32 = 0x00000111;
pub const PROTOCOL_VERSION_AES_GCM: u32 = 0x00000181;
pub const DEFAULT_PROTOCOL_VERSION: u32 = 0x00000001;

impl MessageCodec {

    pub const HEADER_LEN: usize = 8; // 4 bytes for length + 4 bytes for version
    pub const MAX_PAYLOAD_SIZE: usize = 10 * 1024 * 1024;
    pub const MAX_MESSAGE_SIZE: usize = 4 * 1024 * 1024;

    /// Writes a framed message with 8-byte header to the output stream.
    /// Header layout: [length (4 bytes)] + [version (4 bytes)]
    pub fn write_framed_message<W: Write>(
        writer: &mut W,
        payload: &[u8],
        version: u32,
    ) -> Result<(), VertexCacheSdkException> {
        if payload.is_empty() {
            return Err(VertexCacheSdkException::new("Payload must be non-empty"));
        }

        if payload.len() > Self::MAX_MESSAGE_SIZE {
            return Err(VertexCacheSdkException::new("Payload too large"));
        }

        let mut header = Vec::with_capacity(Self::HEADER_LEN);
        header.extend_from_slice(&(payload.len() as u32).to_be_bytes());
        header.extend_from_slice(&version.to_be_bytes());

        writer.write_all(&header)?;
        writer.write_all(payload)?;
        writer.flush()?;
        Ok(())
    }

    /// Reads a framed message from input stream. Returns payload if successful.
    pub fn read_framed_message<R: Read>(
        reader: &mut R,
    ) -> Result<Option<(u32, Vec<u8>)>, VertexCacheSdkException> {
        let mut header = [0u8; Self::HEADER_LEN];
        let bytes_read = reader.read(&mut header)?;
        if bytes_read == 0 {
            return Ok(None);
        }

        if bytes_read < Self::HEADER_LEN {
            return Ok(None);
        }

        let length = u32::from_be_bytes([header[0], header[1], header[2], header[3]]) as usize;
        let version = u32::from_be_bytes([header[4], header[5], header[6], header[7]]);

        if length == 0 || length > Self::MAX_PAYLOAD_SIZE {
            return Err(VertexCacheSdkException::new("Invalid message length"));
        }

        let mut payload = vec![0u8; length];
        reader.read_exact(&mut payload)?;
        Ok(Some((version, payload)))
    }

    /// Returns a hex dump string for a framed payload (used for inter-SDK comparison).
    pub fn hex_dump(payload: &[u8], version: u32) -> Result<String, VertexCacheSdkException> {
        let mut framed = Vec::new();
        Self::write_framed_message(&mut framed, payload, version)?;
        Ok(framed.iter().map(|b| format!("{:02X}", b)).collect::<Vec<String>>().join(""))
    }
}
