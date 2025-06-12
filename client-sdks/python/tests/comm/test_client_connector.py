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
import socket
from sdk.model.client_option import ClientOption
from sdk.comm.client_connector import ClientConnector
from sdk.model.encryption_mode import EncryptionMode


class ClientConnectorTest(unittest.TestCase):

    def test_default_options(self):
        option = ClientOption()
        self.assertEqual(option.get_client_id(), "sdk-client")
        self.assertEqual(option.get_client_token(), "")
        self.assertEqual(option.server_host, "127.0.0.1")
        self.assertEqual(option.server_port, 50505)
        self.assertFalse(option.enable_tls_encryption)
        self.assertEqual(option.get_encryption_mode(), EncryptionMode.NONE)

    def test_build_ident_command(self):
        option = ClientOption()
        option.client_id = "abc"
        option.client_token = "xyz"
        cmd = option.build_ident_command()
        self.assertEqual(cmd, 'IDENT {"client_id":"abc", "token":"xyz"}')

    def test_is_connected_flag(self):
        connector = ClientConnector(ClientOption())
        self.assertFalse(connector.is_connected())

        connector.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        connector.connected = True
        self.assertTrue(connector.is_connected())

        connector.close()
        self.assertFalse(connector.is_connected())


if __name__ == "__main__":
    unittest.main()
