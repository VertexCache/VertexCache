# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
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

import socket
import threading
import time
import pytest

from sdk.comm.socket_helper import SocketHelper
from sdk.model.client_option import ClientOption
from sdk.model.vertex_cache_sdk_exception import VertexCacheSdkException

ENABLE_LIVE_TLS_TESTS = True

MOCK_PORT = 18888
UNUSED_PORT = 65534
BLACKHOLE_IP = "10.255.255.1"
LIVE_TLS_PORT = 50505

VALID_PEM_CERT = """-----BEGIN CERTIFICATE-----
MIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV
BAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x
EDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv
Y2FsaG9zdDAeFw0yNTA1MTgwMzU2NDdaFw0zNTA1MTYwMzU2NDdaMG4xEDAOBgNV
BAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x
EDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv
Y2FsaG9zdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMHVT5HdQkUj
Ia3sYmLQUeOja7tKtAXi1cuhCLlrvgS2DKJa9cpkgi1dsKOjJmsTqo580e+jrpdQ
J+mTybdKoG6CZWEqfMizut48aTQoBteiLFSZ9J2/6nCXhxugA+aQ94lhkj3lJIHf
lIZeIYHaPNXH9/K4oCODJ8P6MfeQjY1ZWbrcQ9PxHQhWV/60AfTuJRJ4T/HQmOqM
6IcYz2t7iviIYvQq37A+wr1ClgxlfuT6JScEA8J34GivskB2p/MEn8E8y/durORz
aaF5RBpnsc+fzVwQuvkth993rnDemdrcvTF1bdF5t88Zt5FiPD4qDF+pKloHNMRQ
DXBYb9Wf/t8CAwEAAaMhMB8wHQYDVR0OBBYEFOYQaTvkoqgLjRhCYBMrwLqrVfJo
MA0GCSqGSIb3DQEBDAUAA4IBAQCVBHT1uqtm72g085JuWdjBoBDa6bJD3Wj3L+GH
JaKOF26wQmXtLV0KraH3t3SUxWOM865OcbOkIiSUjMIgqmmFh1quoF4NMBa0wye8
JguLk6Qpffd+YXfzddxi33jdCUWgyqcTKq7bfB5DbMP4U5yVxnlXwKB0dxkaEFSx
iAUrhcZ1+iYjelrERk8MPj9FQIzQ8FwwF4oB8ShNDhDNWCOVbSdLXwMOLH84u/ul
v/I4U/5/mqGGTtwNyyzFS0GYgrYua4H7Aqer2g4wv8PUYwkaAfQ49CWm9kFQxgD4
qwwA44GZv7zAa89WHNpbIMAA8keexZkPzJBIQNSKy2d9dhcP
-----END CERTIFICATE-----
"""


@pytest.fixture(scope="session", autouse=True)
def mock_tcp_server():
    running = True

    def server_thread():
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server:
            server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            server.bind(("localhost", MOCK_PORT))
            server.listen(5)
            while running:
                try:
                    conn, _ = server.accept()
                    conn.close()
                except socket.error:
                    pass

    thread = threading.Thread(target=server_thread, daemon=True)
    thread.start()
    time.sleep(0.2)
    yield
    try:
        socket.create_connection(("localhost", MOCK_PORT), timeout=1).close()
    except Exception:
        pass


def build_option(host="localhost", port=MOCK_PORT, cert=None, verify=False):
    option = ClientOption()
    option.server_host = host
    option.server_port = port
    option.connect_timeout = 1
    option.read_timeout = 1
    option.tls_certificate = cert
    option.verify_certificate = verify
    return option


def test_create_non_secure_socket_should_succeed():
    option = build_option()
    sock = SocketHelper.create_socket_non_tls(option)
    assert sock is not None
    assert sock.fileno() != -1
    sock.close()


def test_create_non_secure_socket_should_fail_if_port_closed():
    option = build_option(port=UNUSED_PORT)
    with pytest.raises(VertexCacheSdkException, match="Failed to create Non Secure Socket"):
        SocketHelper.create_socket_non_tls(option)


def test_create_non_secure_socket_should_fail_on_timeout():
    option = build_option(host=BLACKHOLE_IP, port=12345)
    with pytest.raises(VertexCacheSdkException, match="Failed to create Non Secure Socket"):
        SocketHelper.create_socket_non_tls(option)


def test_create_secure_socket_should_fail_due_to_missing_tls_context():
    option = build_option(verify=True, cert=None)
    with pytest.raises(VertexCacheSdkException, match="Failed to create Secure Socket"):
        SocketHelper.create_secure_socket(option)


def test_create_secure_socket_should_fail_with_bad_certificate():
    option = build_option(verify=True, cert="not a cert")
    with pytest.raises(VertexCacheSdkException, match="Failed to create Secure Socket"):
        SocketHelper.create_secure_socket(option)
