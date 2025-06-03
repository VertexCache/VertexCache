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
package com.vertexcache.sdk.comm;

import com.vertexcache.sdk.comm.EncryptionMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionModeTest {

    @Test
    public void testEnumValuesAreStable() {
        assertEquals("NONE", EncryptionMode.NONE.name());
        assertEquals("ASYMMETRIC", EncryptionMode.ASYMMETRIC.name());
        assertEquals("SYMMETRIC", EncryptionMode.SYMMETRIC.name());
    }

    @Test
    public void testEnumOrdinalValues() {
        assertEquals(0, EncryptionMode.NONE.ordinal());
        assertEquals(1, EncryptionMode.ASYMMETRIC.ordinal());
        assertEquals(2, EncryptionMode.SYMMETRIC.ordinal());
    }

    @Test
    public void testValueOfIsCaseSensitive() {
        assertEquals(EncryptionMode.SYMMETRIC, EncryptionMode.valueOf("SYMMETRIC"));
        assertThrows(IllegalArgumentException.class, () -> EncryptionMode.valueOf("symmetric")); // lowercase
    }

    @Test
    public void testValuesContainsAllExpected() {
        EncryptionMode[] modes = EncryptionMode.values();
        assertEquals(3, modes.length);
        assertArrayEquals(new EncryptionMode[]{
                EncryptionMode.NONE,
                EncryptionMode.ASYMMETRIC,
                EncryptionMode.SYMMETRIC
        }, modes);
    }
}


