const net = require("net");
const tls = require("tls");
const { expect } = require("chai");
const { createSocketNonTLS, createSecureSocket } = require("../../sdk/comm/socket_helper");
const { ClientOption } = require("../../sdk/model/client_option");
const { VertexCacheSdkException } = require("../../sdk/model/vertex_cache_sdk_exception");

const MOCK_PORT = 18888;
const UNUSED_PORT = 65534; // adjust if needed
const ENABLE_LIVE_TLS_TESTS = true;

let server;
let serverRunning = false;

const VALID_PEM_CERT = `-----BEGIN CERTIFICATE-----
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
-----END CERTIFICATE-----`;

before(() => {
    serverRunning = true;
    server = net.createServer((socket) => {
        socket.destroy();
    });
    server.listen(MOCK_PORT);
});

after(() => {
    serverRunning = false;
    server.close();
});

describe("SocketHelper", () => {
    it("createSocketNonTLS should succeed", async () => {
        const opt = new ClientOption("localhost", MOCK_PORT, 1000, 1000);
        const sock = await createSocketNonTLS(opt);
        expect(sock).to.be.an("object");
        sock.end(); // <-- gracefully close
        sock.destroy(); // <-- force close just in case
    });

    it("createSocketNonTLS should fail on unused port", async () => {
        const opt = new ClientOption("localhost", UNUSED_PORT, 500, 500);
        try {
            await createSocketNonTLS(opt);
        } catch (e) {
            expect(e).to.be.instanceOf(VertexCacheSdkException);
            expect(e.message).to.equal("Failed to create Non Secure Socket");
        }
    });

    it("createSocketNonTLS should fail on timeout", async () => {
        const opt = new ClientOption("10.255.255.1", 12345, 300, 500);
        try {
            await createSocketNonTLS(opt);
        } catch (e) {
            expect(e).to.be.instanceOf(VertexCacheSdkException);
            expect(e.message).to.equal("Failed to create Non Secure Socket");
        }
    });

    it("createSecureSocket should fail due to missing TLS cert", async () => {
        const opt = new ClientOption("localhost", MOCK_PORT, 1000, 1000);
        opt.verifyCertificate = true;
        opt.tlsCertificate = null;

        try {
            await createSecureSocket(opt);
        } catch (e) {
            expect(e).to.be.instanceOf(VertexCacheSdkException);
            expect(e.message).to.equal("Failed to create Secure Socket");
        }
    });

    it("createSecureSocket should fail with bad cert", async () => {
        const opt = new ClientOption("localhost", MOCK_PORT, 1000, 1000);
        opt.verifyCertificate = true;
        opt.tlsCertificate = "not a cert";

        try {
            await createSecureSocket(opt);
        } catch (e) {
            expect(e).to.be.instanceOf(VertexCacheSdkException);
            expect(e.message).to.equal("Failed to create Secure Socket");
        }
    });
});
