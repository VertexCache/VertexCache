// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache
// Licensed under the Apache License, Version 2.0 (the "License");
// You may obtain a copy at http://www.apache.org/licenses/LICENSE-2.0
// ------------------------------------------------------------------------------

const { expect } = require('chai');
const { VertexCacheSDK } = require('../sdk/vertexcache_sdk');
const { ClientOption } = require('../sdk/model/client_option');
const { EncryptionMode } = require('../sdk/model/encryption_mode');

// Config
const ENABLE_TESTS = process.env.VC_LIVE_TLS_ASYMMETRIC_TEST === 'true';

const CLIENT_ID = 'sdk-client-nodejs';
const CLIENT_TOKEN = '2c9962b5-559f-450a-a411-71859055fdc0';
const HOST = 'localhost';
const PORT = 50505;
const TEST_PUBLIC_KEY = `-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\nbw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\nUzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\nGzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\nNwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\nEwIDAQAB\n-----END PUBLIC KEY-----`;
const TLS_CERT = `-----BEGIN CERTIFICATE-----\nMIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV\nBAYTB1...`;

// Mocha-safe skip
if (!ENABLE_TESTS) {
    console.log('Skipping VertexCacheSDK live tests — set VC_LIVE_TLS_ASYMMETRIC_TEST=true to enable.');
    return;
}

describe('VertexCacheSDK Live Integration Test', function () {
    this.timeout(10000);

    let sdk;

    beforeEach(async () => {
        const opt = new ClientOption();
        opt.clientId = CLIENT_ID;
        opt.clientToken = CLIENT_TOKEN;
        opt.serverHost = HOST;
        opt.serverPort = PORT;
        opt.enableTlsEncryption = true;
        opt.verifyCertificate = false;
        opt.tlsCertificate = TLS_CERT;
        opt.encryptionMode = EncryptionMode.ASYMMETRIC;
        opt.publicKey = TEST_PUBLIC_KEY;

        sdk = new VertexCacheSDK(opt);
        await sdk.openConnection();
    });

    afterEach(async () => {
        if (sdk) {
            await sdk.close();
        }
    });

    it('01: ping should succeed', async () => {
        const result = await sdk.ping();
        expect(result.isSuccess()).to.be.true;
        expect(result.getMessage()).to.match(/^PONG/);
    });

    it('02: set should succeed', async () => {
        const result = await sdk.set('test-key', 'value-123');
        expect(result.isSuccess()).to.be.true;
        expect(result.getMessage()).to.equal('OK');
    });

    it('03: get should return previously set value', async () => {
        await sdk.set('test-key', 'value-123');
        const result = await sdk.get('test-key');
        expect(result.isSuccess()).to.be.true;
        expect(result.getValue()).to.equal('value-123');
    });

    it('04: del should remove key', async () => {
        await sdk.set('delete-key', 'to-be-deleted');
        const delResult = await sdk.del('delete-key');
        expect(delResult.isSuccess()).to.be.true;
        const getResult = await sdk.get('delete-key');
        expect(getResult.isSuccess()).to.be.true;
        expect(getResult.getValue()).to.be.null;
    });

    it('05: get on missing key should not fail', async () => {
        const result = await sdk.get('nonexistent-key');
        expect(result.isSuccess()).to.be.true;
        expect(result.getValue()).to.be.null;
    });

    it('06: set with secondary index should succeed', async () => {
        const result = await sdk.set('test-key', 'value-123', 'test-secondary-index');
        expect(result.isSuccess()).to.be.true;
        expect(result.getMessage()).to.equal('OK');
    });

    it('07: set with secondary and tertiary index should succeed', async () => {
        const result = await sdk.set('test-key', 'value-123', 'test-secondary-index', 'test-tertiary-index');
        expect(result.isSuccess()).to.be.true;
        expect(result.getMessage()).to.equal('OK');
    });

    it('08: get by secondary index should succeed', async () => {
        await sdk.set('test-key', 'value-123', 'test-secondary-index');
        const result = await sdk.getBySecondaryIndex('test-secondary-index');
        expect(result.isSuccess()).to.be.true;
        expect(result.getValue()).to.equal('value-123');
    });

    it('09: get by tertiary index should succeed', async () => {
        await sdk.set('test-key', 'value-123', 'test-secondary-index', 'test-tertiary-index');
        const result = await sdk.getByTertiaryIndex('test-tertiary-index');
        expect(result.isSuccess()).to.be.true;
        expect(result.getValue()).to.equal('value-123');
    });

    it('10: multibyte key/value should succeed', async () => {
        const key = '键🔑値🌟';
        const val = '测试🧪データ💾';
        await sdk.set(key, val);
        const result = await sdk.get(key);
        expect(result.isSuccess()).to.be.true;
        expect(result.getValue()).to.equal(val);
    });

    it('17: set with empty key should throw', async () => {
        try {
            await sdk.set('', 'value');
            throw new Error('Expected set to throw');
        } catch (err) {
            expect(err.message).to.include('Missing Primary Key');
        }
    });

    it('18: set with empty value should throw', async () => {
        try {
            await sdk.set('key', '');
            throw new Error('Expected set to throw');
        } catch (err) {
            expect(err.message).to.include('Missing Value');
        }
    });

    it('19: set with null key should throw', async () => {
        try {
            await sdk.set(null, 'value');
            throw new Error('Expected set to throw');
        } catch (err) {
            expect(err.message).to.include('Missing Primary Key');
        }
    });

    it('20: set with null value should throw', async () => {
        try {
            await sdk.set('key', null);
            throw new Error('Expected set to throw');
        } catch (err) {
            expect(err.message).to.include('Missing Value');
        }
    });

    it('21: set with empty secondary index should throw', async () => {
        try {
            await sdk.set('key', 'val', '');
            throw new Error('Expected set to throw');
        } catch (err) {
            expect(err.message).to.include("Secondary key can't be empty when used");
        }
    });

    it('22: set with empty tertiary index should throw', async () => {
        try {
            await sdk.set('key', 'val', 'sec', '');
            throw new Error('Expected set to throw');
        } catch (err) {
            expect(err.message).to.include("Tertiary key can't be empty when used");
        }
    });

});
