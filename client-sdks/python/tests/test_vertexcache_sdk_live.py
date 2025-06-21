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

import os
import unittest
from sdk.vertexcache_sdk import VertexCacheSDK
from sdk.model.client_option import ClientOption
from sdk.model.command_result import CommandResult
from sdk.model.get_result import GetResult
from sdk.model.encryption_mode import EncryptionMode
from sdk.model.vertex_cache_sdk_exception import VertexCacheSdkException

@unittest.skipUnless(os.getenv("VC_LIVE_TLS_ASYMMETRIC_TEST") == "true", "Live TLS asymmetric test not enabled")
class VertexCacheSDKLiveTest(unittest.TestCase):

    CLIENT_ID = "sdk-client-python"
    CLIENT_TOKEN = "21d88d95-7c82-48c6-95a9-830648f3c28c"
    HOST = "localhost"
    PORT = 50505
    ENABLE_TLS = True
    TEST_TLS_CERT = os.getenv("VC_LIVE_TLS_CERT", "")
    TEST_PUBLIC_KEY = """-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\nbw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\nUzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\nGzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\nNwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\nEwIDAQAB\n-----END PUBLIC KEY-----"""

    def setUp(self):
        option = ClientOption()
        option.client_id = self.CLIENT_ID
        option.client_token = self.CLIENT_TOKEN
        option.server_host = self.HOST
        option.server_port = self.PORT
        option.enable_tls_encryption = self.ENABLE_TLS
        option.tls_certificate = self.TEST_TLS_CERT
        option.encryption_mode = EncryptionMode.ASYMMETRIC
        option.public_key = self.TEST_PUBLIC_KEY
        self.sdk = VertexCacheSDK(option)
        self.sdk.open_connection()

    def tearDown(self):
        if self.sdk:
            self.sdk.close()

    def test_01_ping_should_succeed(self):
        result = self.sdk.ping()
        self.assertTrue(result.success)
        self.assertTrue(result.message.startswith("PONG"))

    def test_02_set_should_succeed(self):
        result = self.sdk.set("test-key", "value-123")
        self.assertTrue(result.success)
        self.assertEqual(result.message, "OK")

    def test_03_get_should_return_value(self):
        self.sdk.set("test-key", "value-123")
        result = self.sdk.get("test-key")
        self.assertTrue(result.success)
        self.assertEqual(result.value, "value-123")

    def test_04_del_should_remove_key(self):
        self.sdk.set("delete-key", "to-be-deleted")
        result = self.sdk.delete("delete-key")
        self.assertTrue(result.success)
        get_result = self.sdk.get("delete-key")
        self.assertTrue(get_result.success)
        self.assertIsNone(get_result.value)

    def test_05_get_on_missing_key_should_succeed(self):
        result = self.sdk.get("nonexistent-key")
        self.assertTrue(result.success)
        self.assertIsNone(result.value)

    def test_06_set_secondary_index_should_succeed(self):
        result = self.sdk.set("test-key", "value-123", "test-secondary-index")
        self.assertTrue(result.success)
        self.assertEqual(result.message, "OK")

    def test_07_set_secondary_and_tertiary_should_succeed(self):
        result = self.sdk.set("test-key", "value-123", "test-secondary-index", "test-tertiary-index")
        self.assertTrue(result.success)
        self.assertEqual(result.message, "OK")

    def test_08_get_by_secondary_index(self):
        self.sdk.set("test-key", "value-123", "test-secondary-index")
        result = self.sdk.get_by_secondary_index("test-secondary-index")
        self.assertTrue(result.success)
        self.assertEqual(result.value, "value-123")

    def test_09_get_by_tertiary_index(self):
        self.sdk.set("test-key", "value-123", "test-secondary-index", "test-tertiary-index")
        result = self.sdk.get_by_tertiary_index("test-tertiary-index")
        self.assertTrue(result.success)
        self.assertEqual(result.value, "value-123")

    def test_10_multibyte_key_and_value(self):
        k = "键🔑値🌟"
        v = "测试🧪データ💾"
        set_result = self.sdk.set(k, v)
        self.assertTrue(set_result.success)
        self.assertEqual(set_result.message, "OK")
        get_result = self.sdk.get(k)
        self.assertTrue(get_result.success)
        self.assertEqual(get_result.value, v)

    def test_11_failed_host(self):
        bad_option = ClientOption()
        bad_option.client_id = self.CLIENT_ID
        bad_option.client_token = self.CLIENT_TOKEN
        bad_option.server_host = "bad-host"
        bad_option.server_port = self.PORT
        bad_option.enable_tls_encryption = self.ENABLE_TLS
        bad_option.tls_certificate = self.TEST_TLS_CERT
        bad_option.encryption_mode = EncryptionMode.ASYMMETRIC
        bad_option.public_key = self.TEST_PUBLIC_KEY
        with self.assertRaises(VertexCacheSdkException):
            VertexCacheSDK(bad_option).open_connection()

    def test_12_failed_port(self):
        bad_option = ClientOption()
        bad_option.client_id = self.CLIENT_ID
        bad_option.client_token = self.CLIENT_TOKEN
        bad_option.server_host = self.HOST
        bad_option.server_port = 0
        bad_option.enable_tls_encryption = self.ENABLE_TLS
        bad_option.tls_certificate = self.TEST_TLS_CERT
        bad_option.encryption_mode = EncryptionMode.ASYMMETRIC
        bad_option.public_key = self.TEST_PUBLIC_KEY
        with self.assertRaises(VertexCacheSdkException):
            VertexCacheSDK(bad_option).open_connection()

    def test_13_failed_tls_verification(self):
        bad_option = ClientOption()
        bad_option.client_id = self.CLIENT_ID
        bad_option.client_token = self.CLIENT_TOKEN
        bad_option.server_host = self.HOST
        bad_option.server_port = self.PORT
        bad_option.enable_tls_encryption = self.ENABLE_TLS
        bad_option.verify_certificate = True
        bad_option.tls_certificate = self.TEST_TLS_CERT
        bad_option.encryption_mode = EncryptionMode.ASYMMETRIC
        bad_option.public_key = self.TEST_PUBLIC_KEY
        with self.assertRaises(VertexCacheSdkException) as ctx:
            VertexCacheSDK(bad_option).open_connection()
        self.assertIn("Failed to create Secure Socket", str(ctx.exception))

    def test_14_non_secure_tls_skips_validation(self):
        option = ClientOption()
        option.client_id = self.CLIENT_ID
        option.client_token = self.CLIENT_TOKEN
        option.server_host = self.HOST
        option.server_port = self.PORT
        option.enable_tls_encryption = self.ENABLE_TLS
        option.verify_certificate = False
        option.tls_certificate = None
        option.encryption_mode = EncryptionMode.ASYMMETRIC
        option.public_key = self.TEST_PUBLIC_KEY
        VertexCacheSDK(option)  # should not raise

    def test_15_invalid_public_key(self):
        option = ClientOption()
        option.client_id = self.CLIENT_ID
        option.client_token = self.CLIENT_TOKEN
        option.server_host = self.HOST
        option.server_port = self.PORT
        option.enable_tls_encryption = self.ENABLE_TLS
        option.verify_certificate = False
        option.tls_certificate = self.TEST_TLS_CERT
        option.encryption_mode = EncryptionMode.ASYMMETRIC

        option.set_public_key("-----BEGIN PUBLIC KEY-----THIS_IS_NOT_A_KEY-----END PUBLIC KEY-----")

        with self.assertRaises(VertexCacheSdkException):
            option.get_public_key_as_object()

    def test_16_invalid_shared_key(self):
        option = ClientOption()
        option.client_id = self.CLIENT_ID
        option.client_token = self.CLIENT_TOKEN
        option.server_host = self.HOST
        option.server_port = self.PORT
        option.enable_tls_encryption = self.ENABLE_TLS
        option.verify_certificate = False
        option.tls_certificate = self.TEST_TLS_CERT
        option.encryption_mode = EncryptionMode.SYMMETRIC
        option.set_shared_encryption_key("_BAD_SHARED_KEY")
        with self.assertRaises(VertexCacheSdkException):
            option.get_shared_encryption_key_as_bytes()

    def test_17_set_with_empty_key_should_fail(self):
        with self.assertRaises(VertexCacheSdkException):
            self.sdk.set("", "value-123")

    def test_18_set_with_empty_value_should_fail(self):
        with self.assertRaises(VertexCacheSdkException):
            self.sdk.set("empty-value-key", "")

    def test_19_set_with_null_key_should_throw(self):
        with self.assertRaises(VertexCacheSdkException):
            self.sdk.set(None, "value-123")

    def test_20_set_with_null_value_should_throw(self):
        with self.assertRaises(VertexCacheSdkException):
            self.sdk.set("empty-value-key", None)

    def test_21_set_with_empty_secondary_index_should_throw(self):
        with self.assertRaises(VertexCacheSdkException):
            self.sdk.set("key", "value", "")

    def test_22_set_with_empty_tertiary_index_should_throw(self):
        with self.assertRaises(VertexCacheSdkException):
            self.sdk.set("key", "value", "sec-key", "")
