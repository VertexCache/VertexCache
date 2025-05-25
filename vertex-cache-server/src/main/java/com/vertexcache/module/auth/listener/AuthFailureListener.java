package com.vertexcache.module.auth.listener;

import com.vertexcache.module.auth.model.AuthFailureContext;

/**
 * Listener interface for components that want to react to authentication failures,
 * such as unauthorized access attempts due to invalid credentials or forbidden roles.
 */
public interface AuthFailureListener {

    /**
     * Called when an authentication attempt fails.
     *
     * @param context context about the failed authentication attempt
     */
    void onAuthFailure(AuthFailureContext context);
    void onInvalidToken(String token);
}
