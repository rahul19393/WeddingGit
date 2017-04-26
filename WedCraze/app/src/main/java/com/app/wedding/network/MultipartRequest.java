package com.app.wedding.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.app.wedding.Constants.C;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class MultipartRequest extends Request<JSONObject> {
    private HttpEntity mHttpEntity;
    private Response.Listener mListener;
    public static final int TIMEOUT_MS = 60000;
    public MultipartRequest(String url, String filePath, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mHttpEntity = buildMultipartEntity(filePath);
    }

    public MultipartRequest(String url, File file, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mHttpEntity = buildMultipartEntity(file);
    }

    public MultipartRequest(Context context, String url, String filePath,String message, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mHttpEntity = buildMultipartEntity(filePath,message,context);
    }

    private HttpEntity buildMultipartEntity(String filePath) {
        File file = new File(filePath);
        return buildMultipartEntity(file);
    }
    private HttpEntity buildMultipartEntity(String filePath,String message,Context context) {
        File file = new File(filePath);
        return buildMultipartEntity(file,message,context);
    }
    private HttpEntity buildMultipartEntity(File file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Charset.forName("UTF-8"));
        ContentType contentType = ContentType.create("image/png");
        FileBody fileBody = new FileBody(file,contentType, file.getName());
        builder.addPart("userImage",fileBody);

        return builder.build();
    }

    private HttpEntity buildMultipartEntity(File file,String message,Context context) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Charset.forName("UTF-8"));
        ContentType contentType = ContentType.create("image/png");
        if(!TextUtils.isEmpty(file.toString()))
        {
            FileBody fileBody = new FileBody(file,contentType, file.getName());
            builder.addPart("file",fileBody);
        }

try {

    if(!TextUtils.isEmpty(message))
    builder.addPart("message", new StringBody(message,ContentType.TEXT_PLAIN));
   // builder.addPart("userid", new StringBody(Constant.getPrefrence(context).getString(USER_ID, ""), ContentType.TEXT_PLAIN));
}
catch (Exception e){
    e.printStackTrace();
}
        return builder.build();
    }

   /* @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers=new HashMap<String,String>();
        headers.put("Accept","application/json");
        return headers;
    }*/

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Authorization", "Bearer "+ C.GET_TOKEN);
        return params;
    }

    @Override
    public String getBodyContentType() {
        String enctype = mHttpEntity.getContentType().getValue();
        Log.e("enctype",enctype);
        return enctype;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
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
        mListener.onResponse(response);
    }
}