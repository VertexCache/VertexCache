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

const tls = require('tls');
const fs = require('fs');
const crypto = require('crypto');
const { VertexCacheSdkException } = require('../model/vertex_cache_sdk_exception');

/**
 * Creates a verified TLS context from a PEM certificate.
 *
 * @param {string} pemCert - The PEM-encoded certificate string.
 * @returns {tls.SecureContext} - A configured secure context.
 * @throws {VertexCacheSdkException}
 */
function createVerifiedSocketFactory(pemCert) {
    try {
        if (!pemCert || typeof pemCert !== "string" || !pemCert.includes("BEGIN CERTIFICATE")) {
            throw new Error("Invalid PEM certificate");
        }

        return {
            ca: Buffer.from(pemCert, "utf-8"),
            rejectUnauthorized: true,
        };
    } catch (e) {
        throw new Error("Failed to create secure socket connection");
    }
}

function createInsecureSocketFactory() {
    return {
        rejectUnauthorized: false,
    };
}

module.exports = {
    createVerifiedSocketFactory,
    createInsecureSocketFactory
};
