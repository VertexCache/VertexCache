const tls = require("tls");
const net = require("net");
const { constants } = require("crypto");
const { VertexCacheSdkException } = require("../model/vertex_cache_sdk_exception");
const { createVerifiedSocketFactory, createInsecureSocketFactory } = require("./ssl_helper");

async function createSecureSocket(options) {
    return new Promise((resolve, reject) => {
        try {
            const baseSocket = net.connect({
                host: options.serverHost,
                port: options.serverPort,
                timeout: options.connectTimeout,
            });

            baseSocket.on("error", () => {
                reject(new VertexCacheSdkException("Failed to create Secure Socket"));
            });

            baseSocket.on("connect", () => {
                let tlsOptions;
                try {
                    tlsOptions = options.verifyCertificate
                        ? createVerifiedSocketFactory(options.tlsCertificate)
                        : createInsecureSocketFactory();
                } catch (e) {
                    return reject(new VertexCacheSdkException("Failed to create Secure Socket"));
                }

                const tlsSocket = tls.connect({
                    socket: baseSocket,
                    servername: options.serverHost,
                    ...tlsOptions,
                    timeout: options.readTimeout,
                }, () => {
                    if (!tlsSocket.authorized && options.verifyCertificate) {
                        return reject(new VertexCacheSdkException("TLS handshake failed: certificate not authorized"));
                    }
                    resolve(tlsSocket);
                });

                tlsSocket.on("error", () => {
                    reject(new VertexCacheSdkException("Failed to create Secure Socket"));
                });
            });
        } catch (e) {
            reject(new VertexCacheSdkException("Failed to create Secure Socket"));
        }
    });
}

function createSocketNonTLS(options) {
    return new Promise((resolve, reject) => {
        try {
            const socket = new net.Socket();

            socket.setTimeout(options.readTimeout);
            socket.connect(
                {
                    host: options.serverHost,
                    port: options.serverPort,
                    timeout: options.connectTimeout,
                },
                () => {
                    resolve(socket);
                }
            );

            socket.on("error", () => {
                reject(new VertexCacheSdkException("Failed to create Non Secure Socket"));
            });

            socket.on("timeout", () => {
                socket.destroy();
                reject(new VertexCacheSdkException("Failed to create Non Secure Socket"));
            });
        } catch (err) {
            reject(new VertexCacheSdkException("Failed to create Non Secure Socket"));
        }
    });
}

module.exports = {
    createSecureSocket,
    createSocketNonTLS
};
