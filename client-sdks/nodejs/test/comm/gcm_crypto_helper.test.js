// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
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

const { expect } = require("chai");
const crypto = require("crypto");
const helper = require("../../sdk/comm/gcm_crypto_helper");

const key = Buffer.alloc(32); // 256-bit zero key
const message = Buffer.from("VertexCache secure payload");

describe("GcmCryptoHelper", () => {
    it("encrypt and decrypt round-trip", () => {
        const encrypted = helper.encrypt(message, key);
        const decrypted = helper.decrypt(encrypted, key);
        expect(decrypted.equals(message)).to.be.true;
    });

    it("decrypt should fail on tampered ciphertext", () => {
        const encrypted = helper.encrypt(message, key);
        encrypted[encrypted.length - 1] ^= 0x01;

        expect(() => {
            helper.decrypt(encrypted, key);
        }).to.throw();
    });

    it("decrypt should fail if too short", () => {
        expect(() => {
            helper.decrypt(Buffer.from([1, 2, 3]), key);
        }).to.throw();
    });

    it("base64 encode/decode round-trip", () => {
        const encoded = helper.encodeBase64Key(key);
        const decoded = helper.decodeBase64Key(encoded);
        expect(decoded.equals(key)).to.be.true;
    });

    it("generate base64 key should decode to 32 bytes", () => {
        const b64 = helper.generateBase64Key();
        const decoded = helper.decodeBase64Key(b64);
        expect(decoded.length).to.equal(32);
    });

    it("reconciliation test with fixed key and iv", () => {
        const key = Buffer.alloc(16);
        const iv = Buffer.alloc(12);
        const message = Buffer.from("VertexCacheGCMTest");

        const cipher = crypto.createCipheriv("aes-128-gcm", key, iv);
        const encrypted = Buffer.concat([cipher.update(message), cipher.final()]);
        const tag = cipher.getAuthTag();
        const combined = Buffer.concat([iv, encrypted, tag]);

        const decipher = crypto.createDecipheriv("aes-128-gcm", key, iv);
        decipher.setAuthTag(tag);
        const decrypted = Buffer.concat([decipher.update(encrypted), decipher.final()]);

        expect(decrypted.equals(message)).to.be.true;

        console.log("[RECON] Plaintext:", message.toString());
        console.log("[RECON] Key (hex):", key.toString("hex"));
        console.log("[RECON] IV (hex):", iv.toString("hex"));
        console.log("[RECON] Encrypted (hex):", combined.toString("hex"));
    });
});
