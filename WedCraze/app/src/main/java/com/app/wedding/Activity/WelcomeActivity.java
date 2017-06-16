package com.app.wedding.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Model.Model;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.network.Net;
import com.app.wedding.network.VolleyErrors;
import com.plattysoft.leonids.ParticleSystem;

import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<JSONObject>{

    private CircleImageView userImage;
    private ProgressDialog pd;
    private TextView title;
   // private TextureVideoView video;
    private  VideoView video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        userImage = (CircleImageView)findViewById(R.id.userimg);
        title = (TextView)findViewById(R.id.title);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Log.e("heightfull",""+height);
        Log.e("widthfull",""+width);

        title.setTypeface(Typeface.createFromAsset(this.getAssets(), "font/Sensations and Qualities.ttf"));
        //video = (TextureVideoView)findViewById(R.id.videoview);
        video = (VideoView) findViewById(R.id.surface_view);
      /*  video.getLayoutParams().height = height;
        video.getLayoutParams().width = width;
       // String uri = "android.resource://" + getPackageName() + "/" + R.raw.bubble;
       // String uri = "android.resource://" + getPackageName() + "/" + R.raw.bubble;
      //  video.setScaleType(TextureVideoView.ScaleType.CENTER_CROP);
       // video.setLooping(true);
       //video.setDataSource(this,Uri.parse(uri));
      //  video.play();
        userImage.setOnClickListener(this);
        video.setVideoURI(Uri.parse(uri));
        video.requestFocus();
        video.start();
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video.start();
            }
        });

      /* video.setListener(new TextureVideoView.MediaPlayerListener() {
           @Override
           public void onVideoPrepared() {
               video.play();
           }

           @Override
           public void onVideoEnd() {
               video.play();
           }
       });*/


      /*  new ParticleSystem(this, 80, R.drawable.stripe, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.3f, 180, 180)
                .setRotationSpeed(100)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.t1), 8);

        new ParticleSystem(this, 80, R.drawable.stripe, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.3f, 0, 0)
                .setRotationSpeed(100)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.t2), 8);*/
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
        Toast.makeText(WelcomeActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(!TextUtils.isEmpty(model.getImagePath()))

            C.WELCOME_IMAGE = model.getImagePath();
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
           // video.stop();
            Model model = new Model(jsonObject.toString());
            C.USER_IMAGE_URL = model.getProfileImage();
            C.USER_NAME = model.getName();
            startActivity(new Intent(WelcomeActivity.this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    };
}
