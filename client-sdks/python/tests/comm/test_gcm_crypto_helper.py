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

import pytest
import binascii
from sdk.comm import gcm_crypto_helper as helper
from cryptography.hazmat.primitives.ciphers.aead import AESGCM

key = bytes([0] * 32)
message = b"VertexCache secure payload"


def test_encrypt_decrypt_round_trip():
    encrypted = helper.encrypt(message, key)
    decrypted = helper.decrypt(encrypted, key)
    assert decrypted == message


def test_decrypt_fails_on_tampered():
    encrypted = bytearray(helper.encrypt(message, key))
    encrypted[-1] ^= 0x01
    with pytest.raises(Exception):
        helper.decrypt(encrypted, key)


def test_decrypt_fails_if_too_short():
    with pytest.raises(ValueError):
        helper.decrypt(b"\x01\x02", key)


def test_base64_encode_decode_round_trip():
    b64 = helper.encode_base64_key(key)
    decoded = helper.decode_base64_key(b64)
    assert decoded == key


def test_generate_base64_key():
    b64 = helper.generate_base64_key()
    decoded = helper.decode_base64_key(b64)
    assert len(decoded) == 32


def test_reconciliation_with_fixed_iv():
    key = bytes([0] * 16)
    iv = bytes([0] * 12)
    message = b"VertexCacheGCMTest"

    aesgcm = AESGCM(key)
    ciphertext = aesgcm.encrypt(iv, message, associated_data=None)
    decrypted = aesgcm.decrypt(iv, ciphertext, associated_data=None)
    assert decrypted == message

    combined = iv + ciphertext

    print("[RECON] Plaintext:", message.decode())
    print("[RECON] Key (hex):", key.hex())
    print("[RECON] IV (hex):", iv.hex())
    print("[RECON] Encrypted (hex):", combined.hex())
