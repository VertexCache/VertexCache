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
 * SSLHelper provides SSL/TLS socket configuration utilities for the VertexCache SDK.
 */
const SSLHelper = {
    /**
     * Creates a TLS configuration that verifies the server certificate against a provided PEM certificate.
     *
     * @param {string} pemCert - PEM-encoded certificate to trust
     * @returns {object} TLS connection options
     * @throws {VertexCacheSdkException} if the cert is invalid
     */
    createVerifiedSocketOptions(pemCert) {
        try {
            if (!pemCert || typeof pemCert !== 'string' || !pemCert.includes('-----BEGIN CERTIFICATE-----')) {
                throw new VertexCacheSdkException('Failed to create secure socket connection');
            }

            return {
                ca: pemCert,
                rejectUnauthorized: true
            };
        } catch (e) {
            throw new VertexCacheSdkException('Failed to create secure socket connection');
        }
    },

    /**
     * Creates a TLS configuration that disables certificate verification (insecure).
     *
     * @returns {object} Insecure TLS connection options
     * @throws {VertexCacheSdkException}
     */
    createInsecureSocketOptions() {
        try {
            return {
                rejectUnauthorized: false
            };
        } catch (e) {
            throw new VertexCacheSdkException('Failed to create non secure socket connection');
        }
    }
};

module.exports = { SSLHelper };
