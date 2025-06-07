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

import unittest
from sdk.model.client_option import ClientOption
from sdk.model.encryption_mode import EncryptionMode


class ClientOptionTest(unittest.TestCase):

    def test_defaults(self):
        option = ClientOption()
        self.assertEqual("sdk-client", option.get_client_id())
        self.assertEqual("", option.get_client_token())
        self.assertEqual("127.0.0.1", option.server_host)
        self.assertEqual(50505, option.server_port)
        self.assertFalse(option.enable_tls_encryption)
        self.assertFalse(option.verify_certificate)
        self.assertEqual(3000, option.read_timeout)
        self.assertEqual(3000, option.connect_timeout)
        self.assertEqual(EncryptionMode.NONE, option.encryption_mode)
        self.assertIn("IDENT", option.build_ident_command())

    def test_set_values(self):
        option = ClientOption()
        option.client_id = "test-client"
        option.client_token = "token123"
        option.server_host = "192.168.1.100"
        option.server_port = 9999
        option.enable_tls_encryption = True
        option.verify_certificate = True
        option.tls_certificate = "cert"
        option.connect_timeout = 1234
        option.read_timeout = 5678
        option.encryption_mode = EncryptionMode.SYMMETRIC

        self.assertEqual("test-client", option.get_client_id())
        self.assertEqual("token123", option.get_client_token())
        self.assertEqual("192.168.1.100", option.server_host)
        self.assertEqual(9999, option.server_port)
        self.assertTrue(option.enable_tls_encryption)
        self.assertTrue(option.verify_certificate)
        self.assertEqual("cert", option.tls_certificate)
        self.assertEqual(1234, option.connect_timeout)
        self.assertEqual(5678, option.read_timeout)
        self.assertEqual(EncryptionMode.SYMMETRIC, option.encryption_mode)

    def test_ident_command_generation(self):
        option = ClientOption()
        option.client_id = "my-id"
        option.client_token = "my-token"
        expected = 'IDENT {"client_id":"my-id", "token":"my-token"}'
        self.assertEqual(expected, option.build_ident_command())

    def test_null_token_and_id_fallback(self):
        option = ClientOption()
        option.client_id = None
        option.client_token = None
        ident = option.build_ident_command()
        self.assertIn('"client_id":""', ident)
        self.assertIn('"token":""', ident)


if __name__ == '__main__':
    unittest.main()
