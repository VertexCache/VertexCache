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

const { EncryptionMode } = require('./encryption_mode');

/**
 * Configuration container for initializing the VertexCache SDK client.
 *
 * This class holds all user-specified options required to establish a connection
 * to a VertexCache server, including host, port, TLS settings, authentication tokens,
 * encryption modes (asymmetric or symmetric), and related keys or certificates.
 *
 * It provides a flexible way to customize client behavior, including security preferences.
 */
class ClientOption {
    constructor() {
        this.clientId = ClientOption.DEFAULT_CLIENT_ID;
        this.clientToken = null;

        this.serverHost = ClientOption.DEFAULT_HOST;
        this.serverPort = ClientOption.DEFAULT_PORT;

        this.enableTlsEncryption = false;
        this.tlsCertificate = null;
        this.verifyCertificate = false;

        this.encryptionMode = EncryptionMode.NONE;
        this.encryptWithPublicKey = false;
        this.encryptWithSharedKey = false;

        this.publicKey = null;
        this.sharedEncryptionKey = null;

        this.readTimeout = ClientOption.DEFAULT_READ_TIMEOUT;
        this.connectTimeout = ClientOption.DEFAULT_CONNECT_TIMEOUT;
    }

    getClientId() {
        return this.clientId || '';
    }

    getClientToken() {
        return this.clientToken || '';
    }

    buildIdentCommand() {
        return `IDENT {"client_id":"${this.getClientId()}", "token":"${this.getClientToken()}"}`;
    }
}

ClientOption.DEFAULT_CLIENT_ID = 'sdk-client';
ClientOption.DEFAULT_HOST = '127.0.0.1';
ClientOption.DEFAULT_PORT = 50505;
ClientOption.DEFAULT_READ_TIMEOUT = 3000;
ClientOption.DEFAULT_CONNECT_TIMEOUT = 3000;

module.exports = {
    ClientOption
};
