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
package com.vertexcache.tool.auth;

import java.util.UUID;

/**
 * Simple utility class to generate a random UUID token.
 *
 * Prints a newly generated token to standard output.
 * Useful for creating unique identifiers for testing or authentication purposes.
 */
public class TokenGenerator {

    public static void main(String[] args) {
        String token = UUID.randomUUID().toString();
        System.out.println("Generated Token: " + token);
    }
}

