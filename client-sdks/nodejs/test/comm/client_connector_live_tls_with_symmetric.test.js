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
const { ClientOption } = require("../../sdk/model/client_option");
const { ClientConnector } = require("../../sdk/comm/client_connector");
const { EncryptionMode } = require("../../sdk/model/encryption_mode");

const HOST = "localhost";
const PORT = 50505;
const CLIENT_ID = "sdk-client-nodejs";
const CLIENT_TOKEN = "2c9962b5-559f-450a-a411-71859055fdc0";
const TEST_SHARED_KEY = "neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc=";
const TLS_CERT = `-----BEGIN CERTIFICATE-----\nMIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV\nBAYTB1...`;

describe("ClientConnector Live TLS with Symmetric Key Test", function () {
    if (process.env.VC_LIVE_TLS_SYMMETRIC_TEST !== "true") return;

    it("should connect and send PING", async function () {
        const opt = new ClientOption();
        opt.clientId = CLIENT_ID;
        opt.clientToken = CLIENT_TOKEN;
        opt.serverHost = HOST;
        opt.serverPort = PORT;
        opt.enableTlsEncryption = true;
        opt.verifyCertificate = false; // Set to true if verifying
        opt.tlsCertificate = TLS_CERT;
        opt.encryptionMode = EncryptionMode.SYMMETRIC;
        opt.sharedEncryptionKey = TEST_SHARED_KEY;

        const client = new ClientConnector(opt);
        await client.connect();
        expect(client.isConnected()).to.be.true;


        await new Promise(r => setTimeout(r, 200));
        const reply = await client.send("PING");
        expect(reply).to.be.a("string");
        expect(reply.startsWith("+PONG")).to.be.true;

        client.close();
        expect(client.isConnected()).to.be.false;
    });
});
