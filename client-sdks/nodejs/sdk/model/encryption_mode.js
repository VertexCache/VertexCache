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

/**
 * Defines the supported encryption modes for securing communication
 * between the VertexCache SDK client and server.
 *
 * Modes:
 * - NONE: No encryption is applied; data is sent in plaintext.
 * - SYMMETRIC: Uses a shared secret key (e.g., AES-GCM) for encryption.
 * - ASYMMETRIC: Uses public/private key encryption (e.g., RSA).
 *
 * This object is used to guide how data is encrypted and decrypted throughout
 * the client's message layer.
 */

const EncryptionMode = Object.freeze({
    NONE: 'NONE',
    ASYMMETRIC: 'ASYMMETRIC',
    SYMMETRIC: 'SYMMETRIC'
});

module.exports = {
    EncryptionMode
};
