package com.app.wedding.network;


import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.app.wedding.Constants.C;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by amsyt007 on 2/2/16.
 */
public class CustomJsonRequest extends Request<JSONObject> {
    private Response.Listener<JSONObject> listener;
    private Response.Listener<JSONArray> listenerArray;
    private Map<String, String> params;
    private int method;
    private String mUrl, body;
    private boolean isParams = false;

    public CustomJsonRequest(int method, String url, Map<String, String> params, Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener, boolean isParams) {
        super(method, url, errorListener);
        HttpsTrustManager.allowAllSSL();
        this.listener = reponseListener;
        this.params = params;
        this.method = method;
        this.mUrl = url;
        this.isParams = isParams;
    }

    public CustomJsonRequest(int method, String url, Map<String, String> params, Response.Listener<JSONArray> listenerArray, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        HttpsTrustManager.allowAllSSL();
        this.listenerArray = listenerArray;
        this.params = params;
        this.method = method;
        this.mUrl = url;
        this.isParams = isParams;
    }

    public CustomJsonRequest(int method, String url, String params, Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        HttpsTrustManager.allowAllSSL();
        this.listener = reponseListener;
        this.body = params;
        this.method = method;
        this.mUrl = url;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        if (method != 1 || body != null)
            headers.put("Content-Type", "application/json");
        if(!TextUtils.isEmpty(C.GET_TOKEN)) {
          headers.put("Authorization", "Bearer "+ C.GET_TOKEN);
        }

        Log.e("headers",headers.toString());
        return headers;
    }


    @Override
    public byte[] getBody() throws AuthFailureError {
        Log.e("body-", TextUtils.isEmpty(body) ? "null" : body);
        return TextUtils.isEmpty(body) ? super.getBody() : body.getBytes();
    }

    @Override
    public String getUrl() {

        if (method == Method.GET) {
            StringBuilder stringBuilder = new StringBuilder(mUrl);
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            int i = 1;
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (i == 1) {
                    stringBuilder.append("?" + entry.getKey() + "=" + entry.getValue());
                } else {
                    stringBuilder.append("&" + entry.getKey() + "=" + entry.getValue());
                }
                iterator.remove();
                i++;
            }
            return mUrl = stringBuilder.toString();

        }
        return super.getUrl();
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return params == null ? super.getParams() : params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }


    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}
