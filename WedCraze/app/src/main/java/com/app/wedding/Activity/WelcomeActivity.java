package com.app.wedding.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Model.Model;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.network.Net;

import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<JSONObject>{

    private CircleImageView userImage;
    private ProgressDialog pd;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        userImage = (CircleImageView)findViewById(R.id.userimg);
        title = (TextView)findViewById(R.id.title);

        userImage.setOnClickListener(this);
        makeWelcomeRequest();
    }
    private void makeWelcomeRequest(){
        pd = C.getProgressDialog(this);
        HashMap map = new HashMap();
        Net.makeRequest("http://api.appvapp.com/api/pf1/"+Constants.WELCOME_KEY+"/app/"+ Constants.APP_ID,map,this,this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.userimg:
                makeUserInfoRequest();
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(!TextUtils.isEmpty(model.getImagePath()))
        PicassoCache.getPicassoInstance(WelcomeActivity.this).load(model.getImagePath()).into(userImage);
        if(!TextUtils.isEmpty(model.getTitle()))
            title.setText(model.getTitle());
      //  PicassoCache.getPicassoInstance(AddToGroupActivity.this).load(C.ASSET_URL + items.groupImage).into(holder.groupImage);
    }

    private void makeUserInfoRequest(){
        pd.show();
        Net.makeRequest(C.APP_BASE_URL+"api/users/me",new HashMap<String, String>(),responseUser,this);
    }
    Response.Listener<JSONObject> responseUser = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject.toString());
            C.USER_IMAGE_URL = model.getProfileImage();
            C.USER_NAME = model.getName();
            startActivity(new Intent(WelcomeActivity.this,HomeActivity.class));
            finish();
        }
    };
}
