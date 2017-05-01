package com.app.wedding.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.wedding.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoginStatusCallback;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static android.R.attr.id;

public class FBLoginActivity extends AppCompatActivity {
    Button fb;
    LoginButton loginButton;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fblogin);
        callbackManager = CallbackManager.Factory.create();

         fb = (Button) findViewById(R.id.fb);
        loginButton = (LoginButton) findViewById(R.id.login_button);
       Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            // user has logged in
            Toast.makeText(FBLoginActivity.this,"login",Toast.LENGTH_LONG).show();
        } else {
            // user has not logged in
            Toast.makeText(FBLoginActivity.this,"log out",Toast.LENGTH_LONG).show();

        }
        List< String > permissionNeeds = Arrays.asList("user_photos", "email",
                "user_birthday", "public_profile", "AccessToken");
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {@Override
                public void onSuccess(LoginResult loginResult) {

                    System.out.println("onSuccess");

                    String accessToken = loginResult.getAccessToken()
                            .getToken();
                    Log.i("accessToken", accessToken);

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {@Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {

                                Log.i("LoginActivity",
                                        response.toString());
                               /* try {
                                    id = object.getString("id");
                                    try {
                                        URL profile_pic = new URL(
                                                "http://graph.facebook.com/" + id + "/picture?type=large");
                                        Log.i("profile_pic",
                                                profile_pic + "");

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                    name = object.getString("name");
                                    email = object.getString("email");
                                    gender = object.getString("gender");
                                    birthday = object.getString("birthday");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }*/
                            }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields",
                            "id,name,email,gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                    }

                });

        LoginManager.getInstance().retrieveLoginStatus(this, new LoginStatusCallback() {
            @Override
            public void onCompleted(AccessToken accessToken) {
                // User was previously logged in, can log them in directly here.
                // If this callback is called, a notification is shown that says
                // "Logged in as <User Name>"
                GraphRequest.newMeRequest(
                        accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    String email = me.optString("email");
                                    String id = me.optString("id");
                                    // send email and id to your web server
                                }
                            }
                        }).executeAsync();
                Log.e("token",""+accessToken.getToken()+accessToken.getUserId());
              //  fbToken = accessToken.getToken();
            }
            @Override
            public void onFailure() {
                // No access token could be retrieved for the user
                Log.e("token","fail");

            }
            @Override
            public void onError(Exception exception) {
                // An error occurred
                Log.e("token","excpt");
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }
    public void onClick(View v) {
        if (v == fb) {
            loginButton.performClick();
        }
    }
}
