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

const crypto = require("crypto");
const { VertexCacheSdkException } = require("../model/vertex_cache_sdk_exception");

const GCM_IV_LENGTH = 12; // 96 bits
const GCM_TAG_LENGTH = 16; // bytes

function encrypt(plaintext, key) {
    const iv = crypto.randomBytes(GCM_IV_LENGTH);
    return encryptWithFixedIv(plaintext, key, iv);
}

function encryptWithFixedIv(plaintext, key, iv) {
    if (!Buffer.isBuffer(iv) || iv.length !== GCM_IV_LENGTH) {
        throw new VertexCacheSdkException("Invalid IV length");
    }

    try {
        const cipher = crypto.createCipheriv("aes-256-gcm", key, iv);
        const encrypted = Buffer.concat([cipher.update(plaintext), cipher.final()]);
        const tag = cipher.getAuthTag();
        return Buffer.concat([iv, encrypted, tag]);
    } catch {
        throw new VertexCacheSdkException("Encryption failed");
    }
}

function decrypt(encrypted, key) {
    if (encrypted.length < GCM_IV_LENGTH + GCM_TAG_LENGTH) {
        throw new VertexCacheSdkException("Invalid encrypted data: too short");
    }

    try {
        const iv = encrypted.subarray(0, GCM_IV_LENGTH);
        const tag = encrypted.subarray(encrypted.length - GCM_TAG_LENGTH);
        const ciphertext = encrypted.subarray(GCM_IV_LENGTH, encrypted.length - GCM_TAG_LENGTH);

        const decipher = crypto.createDecipheriv("aes-256-gcm", key, iv);
        decipher.setAuthTag(tag);

        return Buffer.concat([decipher.update(ciphertext), decipher.final()]);
    } catch {
        throw new VertexCacheSdkException("Decryption failed");
    }
}

function encodeBase64Key(key) {
    return key.toString("base64");
}

function decodeBase64Key(b64) {
    return Buffer.from(b64.trim(), "base64");
}

function generateBase64Key() {
    return encodeBase64Key(crypto.randomBytes(32));
}

module.exports = {
    encrypt,
    encryptWithFixedIv,
    decrypt,
    encodeBase64Key,
    decodeBase64Key,
    generateBase64Key,
};
