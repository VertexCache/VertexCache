# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ------------------------------------------------------------------------------

import struct

MAX_MESSAGE_SIZE = 10 * 1024 * 1024  # 10MB
PROTOCOL_VERSION = 0x01


def write_framed_message(payload: bytes) -> bytes:
    """
    Writes a framed message using the VertexCache protocol format.

    Format:
    - 4 bytes big-endian integer representing the payload length
    - 1 byte protocol version
    - N bytes payload data

    Args:
        payload (bytes): The message payload.

    Returns:
        bytes: The framed binary message.

    Raises:
        TypeError: If payload is not a bytes object.
        ValueError: If the payload exceeds the maximum allowed size.
    """
    if not isinstance(payload, bytes):
        raise TypeError("Payload must be bytes")

    if len(payload) > MAX_MESSAGE_SIZE:
        raise ValueError(f"Message too large: {len(payload)}")

    length_bytes = struct.pack(">I", len(payload))
    version_byte = struct.pack("B", PROTOCOL_VERSION)
    return length_bytes + version_byte + payload


def read_framed_message(buffer: bytes):
    """
    Reads a framed message from a byte buffer using the VertexCache protocol.

    Format:
    - 4 bytes big-endian integer representing the payload length
    - 1 byte protocol version
    - N bytes payload data

    Args:
        buffer (bytes): The raw byte buffer to decode.

    Returns:
        tuple[bytes, bytes] | None:
            - A tuple (payload, remaining) if a complete message is decoded
            - None if the buffer is too short to decode the header or full payload

    Raises:
        ValueError: If the protocol version is unsupported or the message length is invalid.
    """
    if len(buffer) < 5:
        return None

    length, version = struct.unpack(">IB", buffer[:5])

    if version != PROTOCOL_VERSION:
        raise ValueError(f"Unsupported protocol version: {version}")

    if length <= 0 or length > MAX_MESSAGE_SIZE:
        raise ValueError(f"Invalid message length: {length}")

    if len(buffer) < 5 + length:
        return None

    payload = buffer[5:5+length]
    remaining = buffer[5+length:]
    return payload, remaining
