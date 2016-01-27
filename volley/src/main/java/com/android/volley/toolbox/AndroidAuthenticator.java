/*
 * Copyright (c) 2016.
 * Created by David Mugisha during the DTY Program
 * Last Edited on 19/01/16 18:31
 */
package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * An Authenticator that uses {@link AccountManager} to get auth
 * tokens of a specified type for a specified account.
 */
public class AndroidAuthenticator implements Authenticator {
    private final AccountManager mAccountManager;
    private final Account mAccount;
    private final String mAuthTokenType;
    private final boolean mNotifyAuthFailure;

    /**
     * Creates a new authenticator.
     * @param context Context for accessing AccountManager
     * @param account Account to authenticate as
     * @param authTokenType Auth token type passed to AccountManager
     */
    public AndroidAuthenticator(Context context, Account account, String authTokenType) {
        this(context, account, authTokenType, false);
    }

    /**
     * Creates a new authenticator.
     * @param context Context for accessing AccountManager
     * @param account Account to authenticate as
     * @param authTokenType Auth token type passed to AccountManager
     * @param notifyAuthFailure Whether to raise a notification upon auth failure
     */
    public AndroidAuthenticator(Context context, Account account, String authTokenType,
            boolean notifyAuthFailure) {
        this(AccountManager.get(context), account, authTokenType, notifyAuthFailure);
    }

    // Visible for testing. Allows injection of a mock AccountManager.
    AndroidAuthenticator(AccountManager accountManager, Account account,
            String authTokenType, boolean notifyAuthFailure) {
        mAccountManager = accountManager;
        mAccount = account;
        mAuthTokenType = authTokenType;
        mNotifyAuthFailure = notifyAuthFailure;
    }

    /**
     * Returns the Account being used by this authenticator.
     */
    public Account getAccount() {
        return mAccount;
    }

    /**
     * Returns the Auth Token Type used by this authenticator.
     */
    public String getAuthTokenType() {
        return mAuthTokenType;
    }

    // TODO: Figure out what to do about notifyAuthFailure
    @SuppressWarnings("deprecation")
    @Override
    public String getAuthToken() throws AuthFailureError {
        AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(mAccount,
                mAuthTokenType, mNotifyAuthFailure, null, null);
        Bundle result;
        try {
            result = future.getResult();
        } catch (Exception e) {
            throw new AuthFailureError("Error while retrieving auth token", e);
        }
        String authToken = null;
        if (future.isDone() && !future.isCancelled()) {
            if (result.containsKey(AccountManager.KEY_INTENT)) {
                Intent intent = result.getParcelable(AccountManager.KEY_INTENT);
                throw new AuthFailureError(intent);
            }
            authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
        }
        if (authToken == null) {
            throw new AuthFailureError("Got null auth token for type: " + mAuthTokenType);
        }

        return authToken;
    }

    @Override
    public void invalidateAuthToken(String authToken) {
        mAccountManager.invalidateAuthToken(mAccount.type, authToken);
    }
}
