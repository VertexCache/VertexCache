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

const tls = require("tls");
const net = require("net");
const { VertexCacheSdkException } = require("../model/vertex_cache_sdk_exception");
const { createVerifiedSocketFactory, createInsecureSocketFactory } = require("./ssl_helper");

/**
 * Promise helper to enforce timeout on a given operation.
 */
function withTimeout(promise, ms, label) {
    return Promise.race([
        promise,
        new Promise((_, reject) =>
            setTimeout(() => reject(new VertexCacheSdkException(`${label} timeout`)), ms)
        )
    ]);
}

/**
 * Establishes a TLS-secured socket using either verified or insecure settings.
 */
function createSecureSocket(options) {
    return withTimeout(new Promise((resolve, reject) => {
        try {
            const tlsOptions = options.verifyCertificate
                ? createVerifiedSocketFactory(options.tlsCertificate)
                : createInsecureSocketFactory();

            const tlsSocket = tls.connect({
                host: options.serverHost,
                port: options.serverPort,
                ...tlsOptions,
                servername: options.serverHost,
            });

            const onConnect = () => {
                tlsSocket.setKeepAlive(true);
                tlsSocket.setNoDelay(true);
                resolve(tlsSocket);
            };

            const onError = (err) => {
                reject(new VertexCacheSdkException("Failed to create Secure Socket"));
            };

            tlsSocket.once("secureConnect", onConnect);
            tlsSocket.once("error", onError);
        } catch (e) {
            reject(new VertexCacheSdkException("Failed to create Secure Socket"));
        }
    }), options.connectTimeout, "TLS connect");
}

/**
 * Establishes a plain TCP socket with enforced connect timeout.
 */
function createSocketNonTLS(options) {
    return withTimeout(new Promise((resolve, reject) => {
        try {
            const socket = new net.Socket();

            const onConnect = () => {
                socket.setKeepAlive(true);
                socket.setNoDelay(true);
                resolve(socket);
            };

            const onError = () => {
                socket.destroy();
                reject(new VertexCacheSdkException("Failed to create Non Secure Socket"));
            };

            socket.once("connect", onConnect);
            socket.once("error", onError);

            socket.connect({
                host: options.serverHost,
                port: options.serverPort,
            });
        } catch (err) {
            reject(new VertexCacheSdkException("Failed to create Non Secure Socket"));
        }
    }), options.connectTimeout, "TCP connect");
}

module.exports = {
    createSecureSocket,
    createSocketNonTLS
};
