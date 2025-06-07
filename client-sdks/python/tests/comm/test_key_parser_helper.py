# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
# ------------------------------------------------------------------------------

import pytest
from sdk.comm.key_parser_helper import config_public_key_if_enabled, config_shared_key_if_enabled
from sdk.model.vertex_cache_sdk_exception import VertexCacheSdkException

valid_pem = """
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
EwIDAQAB
-----END PUBLIC KEY-----
"""

invalid_pem = "-----BEGIN PUBLIC KEY-----INVALID-----END PUBLIC KEY-----"
valid_base64 = "YWJjZGVmZ2hpamtsbW5vcA=="
invalid_base64 = "%%%INVALID%%%"


def test_config_public_key_if_enabled_valid():
    result = config_public_key_if_enabled(valid_pem)
    assert isinstance(result, bytes)
    assert len(result) > 0


def test_config_public_key_if_enabled_invalid():
    with pytest.raises(VertexCacheSdkException) as ex:
        config_public_key_if_enabled(invalid_pem)
    assert str(ex.value) == "Invalid public key"


def test_config_shared_key_if_enabled_valid():
    result = config_shared_key_if_enabled(valid_base64)
    assert isinstance(result, bytes)
    assert result == b"abcdefghijklmnop"


def test_config_shared_key_if_enabled_invalid():
    with pytest.raises(VertexCacheSdkException) as ex:
        config_shared_key_if_enabled(invalid_base64)
    assert str(ex.value) == "Invalid shared key"
