package com.app.wedding.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Dialog.UserDialog;
import com.app.wedding.Interface.LogoutFacebookListener;
import com.app.wedding.Model.AppStore;
import com.app.wedding.Model.Model;
import com.app.wedding.R;
import com.app.wedding.network.Net;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.LoginStatusCallback;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener,Response.Listener<JSONObject>,Response.ErrorListener{

    private ProgressDialog pd;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_GET_TOKEN = 9002;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private boolean fbLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        //for friend list
      //  loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        findViewById(R.id.google).setOnClickListener(this);
       findViewById(R.id.facebook).setOnClickListener(this);
        initGoogle();
        new UserDialog(LoginActivity.this).show();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            Log.v("LoginActivity", response.toString());

                                            // Application code
                                            try {
                                                String email = object.getString("email");
                                                String birthday = object.getString("birthday"); // 01/31/1980 format

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender,birthday");
                            request.setParameters(parameters);
                            request.executeAsync();
                            Log.e("token",""+loginResult.getAccessToken().getToken());
                            JSONObject map = new JSONObject();
                            try {
                                map.put("provider","facebook");
                                map.put("access_token",loginResult.getAccessToken().getToken());
                                if(!fbLogin) {
                                    makeLoginRequest(map);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                        }
                    });

            }
        });
    }

    private void checkFacebook(){
        logoutFromFacebook(new LogoutFacebookListener() {
            @Override
            public void onLoggedOutFromFacebook() {
                loginButton.performClick();
            }
        });
    }
    public void logoutFromFacebook(final LogoutFacebookListener listener) {

        if (AccessToken.getCurrentAccessToken() == null) {
            // already logged out
            listener.onLoggedOutFromFacebook();
            return;
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {

            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();
                listener.onLoggedOutFromFacebook();
            }
        }).executeAsync();
    }

    private void initGoogle(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.serverclientid))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this).enableAutoManage(LoginActivity.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.google:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_GET_TOKEN);
                break;
            case R.id.facebook:
                callbackManager = CallbackManager.Factory.create();
               checkFacebook();
              break;

        }
    }
    private static final List<String> PERMISSIONS = Arrays.asList(
            "email","user_location");

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void makeLoginRequest(JSONObject map){
        fbLogin = true;
        pd = C.getProgressDialog(LoginActivity.this);
        Net.makeRequest(C.APP_URL+"/"+Constants.APP_ID,map.toString(),LoginActivity.this,LoginActivity.this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Log.e("error",volleyError.toString());
        fbLogin = false;
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        fbLogin = false;
        Log.e("response",jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        new AppStore(LoginActivity.this).setData(Constants.TOKEN,model.getToken());
        C.GET_TOKEN = model.getToken();
        makeAppDataRequest();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //  callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GET_TOKEN) {
            /*for google signup*/
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.e("name",acct.getDisplayName());
                Log.e("email",acct.getEmail());
                C.USER_IMAGE_URL = acct.getPhotoUrl().toString();
                Constants.SOCIAL_USER_NAME = acct.getDisplayName();
                acct.getId();
                new RetrieveGoogleTokenTask().execute(acct.getEmail());
            }
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    private class RetrieveGoogleTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";

            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
               /// socialAccessToken = token;
                Log.e("tokengoogle",token);
            } catch (IOException e) {
                Log.e("message", e.getMessage());
            } catch (UserRecoverableAuthException e) {
                Log.e("exception",e.toString());
            } catch (GoogleAuthException e) {
                Log.e("exception",e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject map = new JSONObject();
            try {
                map.put("provider","google");
                map.put("access_token",s);
                if(!fbLogin) {
                    makeLoginRequest(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
           Log.e("token value",s);
        }
    }

    /*user info*/
    private void makeAppDataRequest(){
        pd = C.getProgressDialog(LoginActivity.this);
        HashMap map = new HashMap();
        Net.makeRequest(C.APP_BASE_URL+"api/app/"+Constants.APP_ID,map,response,this);
    }

    Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject.toString());
            Model modelArray[] = model.getPagesArray();
            for(int i = 0; i<modelArray.length;i++){
                Model m = modelArray[i];
                if(i == 0){
                    Constants.WELCOME_KEY = m.getId();
                }else if(i == 1){
                    Constants.STORY_KEY = m.getId();
                }else if(i==2){
                    Constants.CONTACTS_KEY = m.getId();
                }else if(i == 3){
                    Constants.EVENT_KEY = m.getId();
                }else if(i == 4){
                    Constants.GALLERY_KEY = m.getId();
                }
                startActivity(new Intent(LoginActivity.this,WelcomeActivity.class));
            }
        }
    };


}
