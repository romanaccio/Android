/*
 * Copyright (c) 2016.
 * Created by David Mugisha during the DTY Program
 * Last Edited on 19/01/16 18:31
 */package com.dty.gosafe.activity;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Dasha on 18/01/2016.
 */
public class SingleRequest {
    private static SingleRequest mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private SingleRequest(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized SingleRequest getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingleRequest(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
