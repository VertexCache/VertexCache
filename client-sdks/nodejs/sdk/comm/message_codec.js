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

const { EncryptionMode } = require("../model/encryption_mode");

const PROTOCOL_VERSION_RSA_OAEP_SHA256 = 0x00000201;
const PROTOCOL_VERSION_AES_GCM = 0x00000801;
const DEFAULT_PROTOCOL_VERSION = 0x00000001;

const MAX_MESSAGE_SIZE = 10 * 1024 * 1024; // 10MB

let currentProtocolVersion = DEFAULT_PROTOCOL_VERSION;

/**
 * Writes a framed message: [length(4)][version(4)][payload]
 */
function writeFramedMessage(payload, version = currentProtocolVersion) {
    if (!Buffer.isBuffer(payload)) {
        throw new Error("Payload must be a Buffer");
    }
    if (payload.length > MAX_MESSAGE_SIZE) {
        throw new Error(`Message too large: ${payload.length}`);
    }

    const header = Buffer.alloc(8);
    header.writeUInt32BE(payload.length, 0);
    header.writeUInt32BE(version, 4);
    return Buffer.concat([header, payload]);
}

/**
 * Reads a framed message from the stream (async)
 */
function readFramedMessage(stream) {
    return new Promise((resolve, reject) => {
        let buffer = Buffer.alloc(0);

        const tryRead = () => {
            let chunk;
            while ((chunk = stream.read())) {
                buffer = Buffer.concat([buffer, chunk]);

                // Not enough for header
                if (buffer.length < 8) continue;

                const length = buffer.readUInt32BE(0);
                const version = buffer.readUInt32BE(4);

                if (![PROTOCOL_VERSION_RSA_OAEP_SHA256, PROTOCOL_VERSION_AES_GCM, DEFAULT_PROTOCOL_VERSION].includes(version)) {
                    cleanup();
                    return reject(new Error(`Unsupported protocol version: ${version}`));
                }

                if (length <= 0 || length > MAX_MESSAGE_SIZE) {
                    cleanup();
                    return reject(new Error(`Invalid message length: ${length}`));
                }

                if (buffer.length >= 8 + length) {
                    const payload = buffer.slice(8, 8 + length);
                    cleanup();
                    return resolve(payload);
                }
            }
        };

        const onEnd = () => {
            cleanup();
            resolve(null); // stream ended before enough bytes
        };

        const onError = (err) => {
            cleanup();
            reject(err);
        };

        const cleanup = () => {
            stream.removeListener("readable", tryRead);
            stream.removeListener("end", onEnd);
            stream.removeListener("error", onError);
        };

        stream.on("readable", tryRead);
        stream.once("end", onEnd);
        stream.once("error", onError);
    });
}

// Dynamic switching helpers
function useRsaProtocol() {
    currentProtocolVersion = PROTOCOL_VERSION_RSA_OAEP_SHA256;
}

function useSymmetricProtocol() {
    currentProtocolVersion = PROTOCOL_VERSION_AES_GCM;
}

function useRawProtocol() {
    currentProtocolVersion = DEFAULT_PROTOCOL_VERSION;
}

function resolveProtocolVersion(mode) {
    switch (mode) {
        case EncryptionMode.ASYMMETRIC:
            return PROTOCOL_VERSION_RSA_OAEP_SHA256;
        case EncryptionMode.SYMMETRIC:
            return PROTOCOL_VERSION_AES_GCM;
        case EncryptionMode.NONE:
        default:
            return DEFAULT_PROTOCOL_VERSION;
    }
}

module.exports = {
    MAX_MESSAGE_SIZE,
    PROTOCOL_VERSION_RSA_OAEP_SHA256,
    PROTOCOL_VERSION_AES_GCM,
    writeFramedMessage,
    readFramedMessage,
    useRsaProtocol,
    useSymmetricProtocol,
    useRawProtocol,
    resolveProtocolVersion
};
