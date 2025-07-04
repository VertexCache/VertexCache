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
package com.vertexcache.sdk.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandTypeTest {

    @Test
    public void testKeywordValues() {
        assertEquals("PING", CommandType.PING.keyword());
        assertEquals("SET", CommandType.SET.keyword());
        assertEquals("DEL", CommandType.DEL.keyword());
        assertEquals("IDX1", CommandType.IDX1.keyword());
        assertEquals("IDX2", CommandType.IDX2.keyword());
    }

    @Test
    public void testToStringReturnsKeyword() {
        for (CommandType type : CommandType.values()) {
            assertEquals(type.keyword(), type.toString());
        }
    }

    @Test
    public void testEnumIntegrity() {
        CommandType[] expected = {
                CommandType.PING,
                CommandType.SET,
                CommandType.DEL,
                CommandType.IDX1,
                CommandType.IDX2
        };

        assertArrayEquals(expected, CommandType.values());
    }
}

