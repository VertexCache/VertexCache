// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ------------------------------------------------------------------------------

const { describe, it } = require("mocha");
const { expect } = require("chai");
const net = require("net");
const { ClientOption } = require("../../sdk/model/client_option");
const { ClientConnector } = require("../../sdk/comm/client_connector");
const { EncryptionMode } = require("../../sdk/model/encryption_mode");
const { VertexCacheSdkException } = require("../../sdk/model/vertex_cache_sdk_exception");
const { writeFramedMessage, readFramedMessage } = require("../../sdk/comm/message_codec");
const { configSharedKeyIfEnabled, configPublicKeyIfEnabled } = require("../../sdk/comm/key_parser_helper");

const TEST_SHARED_KEY = "neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc=";
const TEST_PUBLIC_KEY = `-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\nbw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\nUzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\nGzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\nNwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\nEwIDAQAB\n-----END PUBLIC KEY-----`;

describe("ClientConnector Functional Test", function () {
    it("should fail with invalid symmetric key", function () {
        const opt = new ClientOption();
        opt.sharedEncryptionKey = "short";
        expect(() => configSharedKeyIfEnabled(opt.sharedEncryptionKey)).to.throw("Invalid shared key");
    });

    it("should fail with invalid public key", function () {
        const opt = new ClientOption();
        opt.publicKey = "-----BEGIN PUBLIC KEY-----\\nINVALIDBASE64==\\n-----END PUBLIC KEY-----";
        expect(() => configPublicKeyIfEnabled(opt.publicKey)).to.throw("Invalid public key");
    });

    it("should fail to connect to wrong port", async function () {
        const opt = new ClientOption();
        opt.serverPort = 65530;
        const client = new ClientConnector(opt);
        try {
            await client.connect();
        } catch (err) {
            expect(err).to.be.instanceOf(VertexCacheSdkException);
        }
    });

    it("should fail IDENT handshake if server replies with error", function (done) {
        const server = net.createServer(socket => {
            readFramedMessage(socket).then(() => {
                const reply = writeFramedMessage(Buffer.from("-ERR Not authorized", "utf-8"));
                socket.write(reply);
                socket.end();
            });
        });

        server.listen(0, async () => { // Use port 0 to auto-assign
            const assignedPort = server.address().port;
            const opt = new ClientOption();
            opt.serverPort = assignedPort;
            const client = new ClientConnector(opt);

            try {
                await client.connect();
                done(new Error("Expected connect to fail with Authorization error"));
            } catch (err) {
                try {
                    expect(err.message).to.include("Authorization failed");
                    done();
                } catch (assertErr) {
                    done(assertErr);
                } finally {
                    server.close();
                }
            }
        });
    });

});
