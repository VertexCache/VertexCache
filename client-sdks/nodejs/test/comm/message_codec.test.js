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
    writeFramedMessage,
    readFramedMessage,
    MAX_MESSAGE_SIZE
} = require('../../sdk/comm/message_codec');

describe('MessageCodec', () => {
    it('should write then read framed message', () => {
        const payload = Buffer.from('Hello VertexCache');
        const framed = writeFramedMessage(payload);
        const result = readFramedMessage(framed);

        expect(result).to.not.be.null;
        expect(result.payload.equals(payload)).to.be.true;
        expect(result.remaining.length).to.equal(0);
    });

    it('should throw on invalid protocol version (4-byte version)', () => {
        const invalidVersion = 0xDEADBEEF;
        const length = 3;
        const header = Buffer.alloc(8);
        header.writeUInt32BE(length, 0);          // message length = 3
        header.writeUInt32BE(invalidVersion, 4);  // unsupported version

        const frame = Buffer.concat([
            header,
            Buffer.from('abc')
        ]);

        expect(() => readFramedMessage(frame)).to.throw('Unsupported protocol version');
    });

    it('should return null on too short header', () => {
        const short = Buffer.from([0x01, 0x02]);
        const result = readFramedMessage(short);
        expect(result).to.be.null;
    });

    it('should reject too large payload on write', () => {
        const big = Buffer.alloc(MAX_MESSAGE_SIZE + 1);
        expect(() => writeFramedMessage(big)).to.throw('Message too large');
    });

    it('should throw when reading empty payload', () => {
        const framed = writeFramedMessage(Buffer.alloc(0));
        expect(() => readFramedMessage(framed)).to.throw('Invalid message length');
    });

    it('should handle utf-8 multibyte payload', () => {
        const original = '你好, VertexCache 🚀';
        const payload = Buffer.from(original, 'utf8');
        const framed = writeFramedMessage(payload);
        const result = readFramedMessage(framed);

        expect(result).to.not.be.null;
        expect(result.payload.toString('utf8')).to.equal(original);
    });

    it('should output hex for cross-SDK comparison', () => {
        const framed = writeFramedMessage(Buffer.from('ping'));
        const hex = framed.toString('hex').toUpperCase();
        console.log('Framed hex:', hex);
    });
});
