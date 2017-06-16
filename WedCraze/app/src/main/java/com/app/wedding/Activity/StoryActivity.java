package com.app.wedding.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Interface.CallBackDialog;
import com.app.wedding.Model.Model;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.network.Net;
import com.app.wedding.network.VolleyErrors;

import org.json.JSONObject;

import java.util.HashMap;

public class StoryActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener{
    private WebView description;
    private ProgressDialog pd;
    private ImageView img;
    private CallBackDialog callBackDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        description = (WebView)findViewById(R.id.desp);
        img = (ImageView)findViewById(R.id.userimg);
        makeStoryRequest();
    }

    private void makeStoryRequest(){
        pd = C.getProgressDialog(StoryActivity.this);
        HashMap map = new HashMap();
        Net.makeRequest("http://api.appvapp.com/api/pf2/"+ Constants.STORY_KEY+"/app/"+ Constants.APP_ID,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(StoryActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
        Log.e("error",volleyError.toString());
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        PicassoCache.getPicassoInstance(StoryActivity.this).load(model.getImagePath()).placeholder(R.drawable.placeholder).into(img);
        String htmlText = "<html><body style=\"text-align:justify\"><font size=\"2\"> "+model.getAbout()+"</font> </body></Html>";
        callBackDialog.CallBackDialog(model.getTitle());
        description.loadData(String.format(htmlText, model.getAbout()), "text/html", "utf-8");

    }
}
