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
// ------------------------------------------------------------------------------

const { expect } = require('chai');
const { Readable } = require('stream');
const {
    writeFramedMessage,
    readFramedMessage,
    MAX_MESSAGE_SIZE
} = require('../../sdk/comm/message_codec');

function bufferToStream(buffer) {
    return Readable.from(buffer);
}

describe('MessageCodec', () => {
    it('should write then read framed message', async () => {
        const payload = Buffer.from('Hello VertexCache');
        const framed = writeFramedMessage(payload);
        const result = await readFramedMessage(bufferToStream(framed));

        expect(result).to.not.be.null;
        expect(result.equals(payload)).to.be.true;
    });

    it('should throw on invalid protocol version (4-byte version)', async () => {
        const invalidVersion = 0xDEADBEEF;
        const header = Buffer.alloc(8);
        header.writeUInt32BE(3, 0); // payload length
        header.writeUInt32BE(invalidVersion, 4);
        const framed = Buffer.concat([header, Buffer.from('abc')]);

        try {
            await readFramedMessage(bufferToStream(framed));
            throw new Error("Expected to throw");
        } catch (err) {
            expect(err.message).to.include('Unsupported protocol version');
        }
    });

    it('should return null on too short header', async () => {
        const short = Buffer.from([0x01, 0x02]);
        const result = await readFramedMessage(bufferToStream(short));
        expect(result).to.be.null;
    });

    it('should reject too large payload on write', () => {
        const big = Buffer.alloc(MAX_MESSAGE_SIZE + 1);
        expect(() => writeFramedMessage(big)).to.throw('Message too large');
    });

    it('should throw when reading empty payload', async () => {
        const framed = writeFramedMessage(Buffer.alloc(0));
        try {
            await readFramedMessage(bufferToStream(framed));
            throw new Error("Expected exception was not thrown");
        } catch (err) {
            expect(err.message).to.include('Invalid message length');
        }
    });

    it('should handle utf-8 multibyte payload', async () => {
        const original = '你好, VertexCache 🚀';
        const payload = Buffer.from(original, 'utf8');
        const framed = writeFramedMessage(payload);
        const result = await readFramedMessage(bufferToStream(framed));

        expect(result).to.not.be.null;
        expect(result.toString('utf8')).to.equal(original);
    });

    it('should output hex for cross-SDK comparison', () => {
        const framed = writeFramedMessage(Buffer.from('ping'));
        const hex = framed.toString('hex').toUpperCase();
        console.log('Framed hex:', hex);
    });
});
