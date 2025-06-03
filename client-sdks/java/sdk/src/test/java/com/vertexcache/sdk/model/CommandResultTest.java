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
package com.vertexcache.sdk.model;

import com.vertexcache.sdk.model.CommandResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandResultTest {

    @Test
    public void testSuccessResult() {
        CommandResult result = new CommandResult(true, "Operation completed");
        assertTrue(result.isSuccess());
        assertEquals("Operation completed", result.getMessage());
    }

    @Test
    public void testFailureResult() {
        CommandResult result = new CommandResult(false, "Something went wrong");
        assertFalse(result.isSuccess());
        assertEquals("Something went wrong", result.getMessage());
    }

    @Test
    public void testNullMessage() {
        CommandResult result = new CommandResult(true, null);
        assertTrue(result.isSuccess());
        assertNull(result.getMessage());
    }
}

