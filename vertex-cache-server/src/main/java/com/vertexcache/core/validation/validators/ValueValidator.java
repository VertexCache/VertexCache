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
package com.vertexcache.core.validation.validators;

import com.vertexcache.core.cache.model.DataType;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ValueValidator {

    private final String fieldName;
    private final String value;
    private final DataType dataType;

    public ValueValidator(String fieldName, String value, DataType dataType) {
        this.fieldName = fieldName;
        this.value = value;
        this.dataType = dataType;
    }

    public void validate() {
        if (value == null || value.isBlank()) {
            throw new VertexCacheValidationException(fieldName + " must not be blank");
        }

        switch (dataType) {
            case JSON:
                try {
                    JsonParser.parseString(value);
                } catch (JsonSyntaxException e) {
                    throw new VertexCacheValidationException(fieldName + " must be valid JSON");
                }
                break;

            case XML:
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    builder.parse(new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)));
                } catch (Exception e) {
                    throw new VertexCacheValidationException(fieldName + " must be valid XML");
                }
                break;

            case BASE64:
                try {
                    Base64.getDecoder().decode(value);
                } catch (IllegalArgumentException e) {
                    throw new VertexCacheValidationException(fieldName + " must be valid Base64");
                }
                break;

            case STRING:
            default:
                // No specific format checks
                break;
        }
    }
}
