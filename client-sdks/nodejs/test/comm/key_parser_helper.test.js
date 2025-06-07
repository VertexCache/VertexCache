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

const { expect } = require('chai');
const {
    configPublicKeyIfEnabled,
    configSharedKeyIfEnabled,
} = require('../../sdk/comm/key_parser_helper');
const { VertexCacheSdkException } = require('../../sdk/model/vertex_cache_sdk_exception');

describe('KeyParserHelper', () => {
    const validPem = `
  -----BEGIN PUBLIC KEY-----
  MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
  bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
  UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
  GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
  NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
  6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
  EwIDAQAB
  -----END PUBLIC KEY-----
  `;

    const invalidPem = '-----BEGIN PUBLIC KEY-----INVALID-----END PUBLIC KEY-----';
    const validBase64 = 'YWJjZGVmZ2hpamtsbW5vcA=='; // "abcdefghijklmnop"
    const invalidBase64 = '%%%INVALID%%%';

    it('configPublicKeyIfEnabled should succeed with valid PEM', () => {
        const result = configPublicKeyIfEnabled(validPem);
        expect(result).to.be.instanceOf(Buffer);
        expect(result.length).to.be.greaterThan(0);
    });

    it('configPublicKeyIfEnabled should fail with invalid PEM', () => {
        expect(() => configPublicKeyIfEnabled(invalidPem)).to.throw('Invalid public key');
    });

    it('configSharedKeyIfEnabled should succeed with valid base64', () => {
        const result = configSharedKeyIfEnabled(validBase64);
        expect(result).to.be.instanceOf(Buffer);
        expect(result.length).to.equal(16);
        expect(result.toString('utf-8')).to.equal('abcdefghijklmnop');
    });

    it('configSharedKeyIfEnabled should fail with invalid base64', () => {
        try {
            configSharedKeyIfEnabled(invalidBase64);
            throw new Error('Expected exception not thrown');
        } catch (err) {
            expect(err).to.be.instanceOf(VertexCacheSdkException);
            expect(err.message).to.equal('Invalid shared key');
        }
    });
});
