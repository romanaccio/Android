/*
 * Copyright (c) 2016.
 * Created by David Mugisha during the DTY Program
 * Last Edited on 19/01/16 18:31
 */

package com.dty.gosafe.connection;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Dasha on 15/01/2016.
 */

/**
 * Manage the HTTP request using Volley Library
 */
public class SingleRequest {

    private static SingleRequest mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private String urlFinal;
    private JsonArrayRequest mJsonArrayRequest;
    private JsonObjectRequest mJsonObjectRequest;

    public JsonArrayRequest getJsonArrayRequest() {
        return mJsonArrayRequest;
    }
    public JsonObjectRequest getJsonObjectRequest() {
        return mJsonObjectRequest;
    }
    public void setJsonArrayRequest(JsonArrayRequest mJsonArrayRequest) {
        this.mJsonArrayRequest = mJsonArrayRequest;
    }
    public void setJsonObjectRequest(JsonObjectRequest mJsonObjectRequest) {
        this.mJsonObjectRequest = mJsonObjectRequest;
    }


    /**
     * SingleRequest constructor
     * @param context
     * @param url
     */
    private SingleRequest(Context context, String url) {
        mCtx = context;
        urlFinal = url;
        mRequestQueue = getRequestQueue();

        // Default get request
        mJsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, urlFinal, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(mCtx, "Fetched data successfully",
                                Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(mCtx, "Unable to fetch",
                                Toast.LENGTH_LONG).show();
                        Log.d("request get error", error.toString());

                    }
                });

        // Default post request
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlFinal, null, new Response.Listener<JSONObject>() {
            /**
             * Called when a response is received.
             *
             * @param response
             */
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(mCtx, "Send data successfully",
                        Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener(){
            /**
             * Callback method that an error has been occurred with the
             * provided error code and optional user-readable message.
             *
             * @param error
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mCtx, "Unable to send data", Toast.LENGTH_LONG).show();
                Log.d("request post error", error.toString());
            }
        });

    }


    public static synchronized SingleRequest getInstance(Context context, String url) {
        if (mInstance == null) {
            Log.d("request", "starting");
            mInstance = new SingleRequest(context,url);
        }
        return mInstance;
    }

    /**
     * getApplicationContext() is key, it keeps you from leaking the
     * Activity or BroadcastReceiver if someone passes one in.
     * @return
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Adds a req to the queue fo tasks to be fulfilled
     * @param req can be a JSONArrayRequest
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
