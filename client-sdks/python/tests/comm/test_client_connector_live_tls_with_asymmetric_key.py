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
from sdk.model.client_option import ClientOption
from sdk.model.encryption_mode import EncryptionMode
from sdk.comm.client_connector import ClientConnector

@unittest.skipUnless(os.getenv("VC_LIVE_TLS_ASYMMETRIC_TEST") == "true", "Live test skipped")
class ClientConnectorLiveTest(unittest.TestCase):

    TEST_PUBLIC_KEY = """-----BEGIN PUBLIC KEY-----
    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
    bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
    UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
    GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
    NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
    6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
    EwIDAQAB
    -----END PUBLIC KEY-----"""

    def setUp(self):
        self.option = ClientOption()
        self.option.server_host = "127.0.0.1"
        self.option.server_port = 50505
        self.option.client_id = "sdk-client-python"
        self.option.client_token = "21d88d95-7c82-48c6-95a9-830648f3c28c"
        self.option.enable_tls_encryption = True
        self.option.verify_certificate = False
        self.option.connect_timeout = 3000
        self.option.read_timeout = 3000
        self.option.set_public_key(self.TEST_PUBLIC_KEY)
        self.option.encryption_mode = EncryptionMode.ASYMMETRIC
        self.connector = ClientConnector(self.option)

    def test_connect_and_ping(self):
        self.connector.connect()
        self.assertTrue(self.connector.is_connected())
        reply = self.connector.send("PING")
        self.assertTrue(reply.startswith("+PONG"))
        self.connector.close()
        self.assertFalse(self.connector.is_connected())


if __name__ == "__main__":
    unittest.main()
