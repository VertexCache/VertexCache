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

const { expect } = require('chai');
const { ClientOption } = require('../../sdk/model/client_option');
const { EncryptionMode } = require('../../sdk/model/encryption_mode');

describe('ClientOption', () => {
    it('testDefaults', () => {
        const option = new ClientOption();

        expect(option.getClientId()).to.equal('sdk-client');
        expect(option.getClientToken()).to.equal('');
        expect(option.serverHost).to.equal('127.0.0.1');
        expect(option.serverPort).to.equal(50505);
        expect(option.enableTlsEncryption).to.be.false;
        expect(option.verifyCertificate).to.be.false;
        expect(option.readTimeout).to.equal(3000);
        expect(option.connectTimeout).to.equal(3000);
        expect(option.encryptionMode).to.equal(EncryptionMode.NONE);
        expect(option.buildIdentCommand()).to.include('IDENT');
    });

    it('testSetValues', () => {
        const option = new ClientOption();
        option.clientId = 'test-client';
        option.clientToken = 'token123';
        option.serverHost = '192.168.1.100';
        option.serverPort = 9999;
        option.enableTlsEncryption = true;
        option.verifyCertificate = true;
        option.tlsCertificate = 'cert';
        option.connectTimeout = 1234;
        option.readTimeout = 5678;
        option.encryptionMode = EncryptionMode.SYMMETRIC;

        expect(option.getClientId()).to.equal('test-client');
        expect(option.getClientToken()).to.equal('token123');
        expect(option.serverHost).to.equal('192.168.1.100');
        expect(option.serverPort).to.equal(9999);
        expect(option.enableTlsEncryption).to.be.true;
        expect(option.verifyCertificate).to.be.true;
        expect(option.tlsCertificate).to.equal('cert');
        expect(option.connectTimeout).to.equal(1234);
        expect(option.readTimeout).to.equal(5678);
        expect(option.encryptionMode).to.equal(EncryptionMode.SYMMETRIC);
    });

    it('testIdentCommandGeneration', () => {
        const option = new ClientOption();
        option.clientId = 'my-id';
        option.clientToken = 'my-token';
        const expected = 'IDENT {"client_id":"my-id", "token":"my-token"}';
        expect(option.buildIdentCommand()).to.equal(expected);
    });

    it('testNullTokenAndIdFallback', () => {
        const option = new ClientOption();
        option.clientId = null;
        option.clientToken = null;
        const ident = option.buildIdentCommand();
        expect(ident).to.include('"client_id":""');
        expect(ident).to.include('"token":""');
    });
});
