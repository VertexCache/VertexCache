# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
# Licensed under the Apache License, Version 2.0
# ------------------------------------------------------------------------------

import pytest
from sdk.comm.message_codec import (
    write_framed_message,
    read_framed_message,
    MAX_MESSAGE_SIZE
)


def test_write_then_read_framed_message():
    payload = b"Hello VertexCache"
    framed = write_framed_message(payload)
    result = read_framed_message(framed)
    assert result is not None
    decoded, remaining = result
    assert decoded == payload
    assert remaining == b""


def test_invalid_version_byte():
    frame = b"\x00\x00\x00\x03" + b"\x02" + b"abc"
    with pytest.raises(ValueError, match="Unsupported protocol version"):
        read_framed_message(frame)


def test_too_short_header_returns_none():
    assert read_framed_message(b"\x01\x02") is None


def test_too_large_payload_rejected():
    with pytest.raises(ValueError, match="Message too large"):
        write_framed_message(b"A" * (MAX_MESSAGE_SIZE + 1))


def test_write_empty_payload_then_read_should_fail():
    framed = write_framed_message(b"")
    with pytest.raises(ValueError, match="Invalid message length"):
        read_framed_message(framed)


def test_utf8_multibyte_payload():
    original = "你好, VertexCache 🚀"
    payload = original.encode("utf-8")
    framed = write_framed_message(payload)
    result = read_framed_message(framed)
    assert result is not None
    decoded, remaining = result
    assert decoded.decode("utf-8") == original
    assert remaining == b""


def test_hex_dump_for_inter_sdk_comparison():
    framed = write_framed_message(b"ping")
    print("Framed hex:", framed.hex().upper())
    assert framed  # prevent Pytest warning about no assertion
