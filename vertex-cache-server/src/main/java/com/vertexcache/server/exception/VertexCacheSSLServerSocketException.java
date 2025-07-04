/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.server.exception;

/**
 * Exception thrown for errors related to SSL server socket initialization or configuration.
 *
 * Indicates issues such as invalid TLS certificates, key loading failures, or SSL context setup problems.
 */
public class VertexCacheSSLServerSocketException extends Exception {
    public VertexCacheSSLServerSocketException() {
        super();
    }

    public VertexCacheSSLServerSocketException(String message) {
        super(message);
    }

    public VertexCacheSSLServerSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public VertexCacheSSLServerSocketException(Throwable cause) {
        super(cause);
    }
}
