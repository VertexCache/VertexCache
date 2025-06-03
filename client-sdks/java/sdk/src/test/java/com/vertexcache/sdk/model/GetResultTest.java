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

import com.vertexcache.sdk.model.GetResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetResultTest {

    @Test
    public void testSuccessResultWithValue() {
        GetResult result = new GetResult(true, "found", "hello");
        assertTrue(result.isSuccess());
        assertEquals("found", result.getMessage());
        assertEquals("hello", result.getValue());
    }

    @Test
    public void testFailureResultWithNullValue() {
        GetResult result = new GetResult(false, "not found", null);
        assertFalse(result.isSuccess());
        assertEquals("not found", result.getMessage());
        assertNull(result.getValue());
    }

    @Test
    public void testEmptyValueIsAllowed() {
        GetResult result = new GetResult(true, "empty value", "");
        assertTrue(result.isSuccess());
        assertEquals("", result.getValue());
    }
}

