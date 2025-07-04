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

import com.vertexcache.core.validation.model.Validator;
import com.vertexcache.core.validation.exception.VertexCacheValidationException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * UrlValidator validates that a given string is a well-formed URL.
 * It checks for valid syntax, supported schemes (such as http or https),
 * and may optionally enforce additional constraints like presence of a host.
 *
 * This validator is typically used in configuration fields where external
 * URLs are required, such as webhook endpoints or remote service URLs,
 * ensuring reliability and correctness at system startup.
 */
public class UrlValidator implements Validator {

    private final String url;
    private final String label;

    public UrlValidator(String url, String label) {
        this.url = url;
        this.label = label != null ? label : "URL";
    }

    @Override
    public void validate() {
        if (url == null || url.isBlank()) {
            throw new VertexCacheValidationException(label + " must not be blank");
        }

        try {
            URL parsed = new URL(url);
            String protocol = parsed.getProtocol();
            if (!protocol.equalsIgnoreCase("http") && !protocol.equalsIgnoreCase("https")) {
                throw new VertexCacheValidationException(label + " must use http or https");
            }
        } catch (MalformedURLException e) {
            throw new VertexCacheValidationException(label + " is not a valid URL: " + url);
        }
    }
}
