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
