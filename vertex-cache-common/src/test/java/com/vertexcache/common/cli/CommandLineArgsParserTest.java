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
package com.vertexcache.common.cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineArgsParserTest {

    @Test
    void testKeyValueArgs() throws Exception {
        String[] args = {"--env=prod", "--port=8080"};
        CommandLineArgsParser parser = new CommandLineArgsParser(args);

        assertEquals("prod", parser.getValue("--env"));
        assertEquals("8080", parser.getValue("--port"));
        assertTrue(parser.isExist("--env"));
        assertTrue(parser.isExist("--port"));
        assertFalse(parser.isExist("debug"));
    }

    @Test
    void testEmptyArgs() throws Exception {
        String[] args = {};
        CommandLineArgsParser parser = new CommandLineArgsParser(args);

        assertFalse(parser.isExist("anything"));
        assertNull(parser.getValue("anything"));
    }
}


