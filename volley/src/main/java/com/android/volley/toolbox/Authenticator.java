/*
 * Copyright (c) 2016.
 * Created by David Mugisha during the DTY Program
 * Last Edited on 19/01/16 18:31
 */

package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;

/**
 * An interface for interacting with auth tokens.
 */
public interface Authenticator {
    /**
     * Synchronously retrieves an auth token.
     *
     * @throws AuthFailureError If authentication did not succeed
     */
    public String getAuthToken() throws AuthFailureError;

    /**
     * Invalidates the provided auth token.
     */
    public void invalidateAuthToken(String authToken);
}
