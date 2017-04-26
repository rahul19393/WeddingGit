package com.app.wedding.network;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by amsyt007 on 18/4/16.
 */
public class VolleyErrors {

    public static String setError(VolleyError error) {
        try {


         //   Model model = null;

            if (error.getMessage() != null) {
          //      model = new Model(error.getMessage());
            }


            //handle volley error response
            if (error.networkResponse != null && error.networkResponse.data != null) {
                switch (error.networkResponse.statusCode) {
                    case 401:
                        try {
                            JSONObject obj = new JSONObject(new String(error.networkResponse.data));
                            if (obj.has("message")) {
                                if (obj.getString("message") != null) {
                                    return obj.getString("message");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return null;
                        }
                        break;
                    case 500:
                    case 404:
                    case 400:
                        try {
                            if (error instanceof ServerError)
                                return "ServerError";

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            } /*else if (model.getError() != null && !TextUtils.isEmpty(model.getError())) {
                return model.getError();
            } else if (model.getMessage() != null && !TextUtils.isEmpty(model.getMessage())) {
                return model.getMessage();
            } else if (error instanceof TimeoutError) {

                return "time out error.";

            }*/ else if (error instanceof NoConnectionError) {
             //   Log.e("errorMessage",error.toString());
                return "Please check your internet connection.";

            } else if (error instanceof AuthFailureError) {
                //TODO
                return "AuthFailureError";

            } else if (error instanceof ServerError) {
                //TODO
                return "ServerError";

            } else if (error instanceof NetworkError) {
                //TODO
                return "NetworkError";

            } else if (error instanceof ParseError) {
                //TODO
                return "ParseError";

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //return "NetworkError";
        return "ServerError";
    }
}