package com.andrius.paysera;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class HttpToJson {

    private JSONObject jsonObject;
    private String url;
    private RequestQueue requestQueue;

    HttpToJson(Context context, String url) {
        requestQueue = Volley.newRequestQueue(context);
        this.url = url;
    }

    public void initRequest() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonObject = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void addFinishedListener(RequestQueue.RequestFinishedListener<JsonObjectRequest> listener) {
        requestQueue.addRequestFinishedListener(listener);
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
