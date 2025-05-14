package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;

import java.net.MalformedURLException;
import java.net.URL;

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
