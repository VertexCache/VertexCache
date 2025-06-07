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

const MAX_MESSAGE_SIZE = 10 * 1024 * 1024; // 10MB
const PROTOCOL_VERSION = 0x01;

/**
 * Writes a framed message: [length(4)][version(1)][payload]
 * @param {Buffer} payload
 * @returns {Buffer}
 */
function writeFramedMessage(payload) {
    if (!Buffer.isBuffer(payload)) {
        throw new Error("Payload must be a Buffer");
    }
    if (payload.length > MAX_MESSAGE_SIZE) {
        throw new Error(`Message too large: ${payload.length}`);
    }

    const header = Buffer.alloc(5);
    header.writeUInt32BE(payload.length, 0);
    header.writeUInt8(PROTOCOL_VERSION, 4);
    return Buffer.concat([header, payload]);
}

/**
 * Reads a framed message from the given buffer.
 * @param {Buffer} buffer
 * @returns {{payload: Buffer, remaining: Buffer}|null}
 */
function readFramedMessage(buffer) {
    if (!Buffer.isBuffer(buffer) || buffer.length < 5) return null;

    const length = buffer.readUInt32BE(0);
    const version = buffer.readUInt8(4);

    if (version !== PROTOCOL_VERSION) {
        throw new Error(`Unsupported protocol version: ${version}`);
    }

    if (length <= 0 || length > MAX_MESSAGE_SIZE) {
        throw new Error(`Invalid message length: ${length}`);
    }

    if (buffer.length < 5 + length) return null;

    const payload = buffer.slice(5, 5 + length);
    const remaining = buffer.slice(5 + length);
    return { payload, remaining };
}

module.exports = {
    MAX_MESSAGE_SIZE,
    PROTOCOL_VERSION,
    writeFramedMessage,
    readFramedMessage,
};
