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
const { createSecureSocket, createSocketNonTLS } = require("./socket_helper");
const {
    writeFramedMessage,
    readFramedMessage,
    useRsaProtocol,
    useSymmetricProtocol,
    useRawProtocol,
    resolveProtocolVersion,
} = require("./message_codec");
const { encrypt } = require("./gcm_crypto_helper");
const {
    configPublicKeyIfEnabled,
    configSharedKeyIfEnabled,
    encryptWithRsa
} = require("./key_parser_helper");
const { VertexCacheSdkException } = require("../model/vertex_cache_sdk_exception");
const { EncryptionMode } = require("../model/encryption_mode");

class ClientConnector {
    constructor(options) {
        this.options = options;
        this.socket = null;
        this.writer = null;
        this.reader = null;
        this.connected = false;
    }

    async connect() {
        try {
            this.socket = this.options.enableTlsEncryption
                ? await createSecureSocket(this.options)
                : await createSocketNonTLS(this.options);

            this.writer = this.socket;
            this.reader = this.socket;

            const identCommand = this.options.buildIdentCommand();
            const payload = this._encryptIfEnabled(Buffer.from(identCommand));
            this._setProtocolVersion();
            const version = resolveProtocolVersion(this.options.encryptionMode);
            const framed = writeFramedMessage(payload, version);
            await this._writeSocketFully(framed);
            const responseBuffer = await readFramedMessage(this.reader);
            const response = responseBuffer?.toString("utf-8").trim() || "";
            if (!response.startsWith("+OK")) {
                this.close();
                throw new VertexCacheSdkException("Authorization failed: " + response);
            }
            this.connected = true;
        } catch (err) {
            this.close();
            throw new VertexCacheSdkException("Connection failed: " + err.message);
        }
    }

    async send(message) {
        try {
            const payload = this._encryptIfEnabled(Buffer.from(message));
            this._setProtocolVersion();
            const version = resolveProtocolVersion();
            const framed = writeFramedMessage(payload, version);

            await this._writeSocketFully(framed);

            const responseBuffer = await readFramedMessage(this.reader);
            if (!responseBuffer) {
                throw new VertexCacheSdkException("Connection closed by server");
            }

            return responseBuffer.toString("utf-8").trim();
        } catch (err) {
            throw new VertexCacheSdkException("Send failed: " + err.message);
        }
    }

    async _writeSocketFully(data) {
        return new Promise((resolve, reject) => {
            const onError = err => {
                this.writer.removeListener("drain", onDrain);
                reject(new VertexCacheSdkException("Socket write failed: " + err.message));
            };

            const onDrain = () => {
                this.writer.removeListener("error", onError);
                resolve();
            };

            this.writer.once("error", onError);

            const ok = this.writer.write(data, err => {
                if (err) return onError(err);
                if (ok) {
                    this.writer.removeListener("error", onError);
                    resolve();
                }
            });

            if (!ok) {
                this.writer.once("drain", onDrain);
            }
        });
    }

    _encryptIfEnabled(plainText) {
        try {
            switch (this.options.encryptionMode) {
                case EncryptionMode.ASYMMETRIC:
                    const pemKey = this.options.publicKey;
                    if (!pemKey || !pemKey.includes("BEGIN PUBLIC KEY")) {
                        throw new VertexCacheSdkException("Invalid PEM public key for encryption");
                    }
                    return encryptWithRsa(pemKey, plainText);
                case EncryptionMode.SYMMETRIC:
                    const sharedKey = configSharedKeyIfEnabled(this.options.sharedEncryptionKey);
                    return encrypt(plainText, sharedKey);
                case EncryptionMode.NONE:
                default:
                    return Buffer.from(plainText);
            }
        } catch (err) {
            throw new VertexCacheSdkException("Encryption failed for plaintext");
        }
    }

    _setProtocolVersion() {
        switch (this.options.encryptionMode) {
            case EncryptionMode.ASYMMETRIC:
                useRsaProtocol();
                break;
            case EncryptionMode.SYMMETRIC:
                useSymmetricProtocol();
                break;
            case EncryptionMode.NONE:
            default:
                useRawProtocol?.();
                break;
        }
    }

    isConnected() {
        return this.connected && this.socket?.writable && this.socket?.readable;
    }

    close() {
        try {
            this.socket?.end();
            this.socket?.destroy();
        } catch {}
        this.connected = false;
    }
}

module.exports = {
    ClientConnector,
};
