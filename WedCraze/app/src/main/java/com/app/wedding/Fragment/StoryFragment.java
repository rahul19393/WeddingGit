package com.app.wedding.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Activity.WelcomeActivity;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Interface.CallBackDialog;
import com.app.wedding.Model.Model;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.network.Net;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by LENOVO on 4/12/2017.
 */

public class StoryFragment extends android.support.v4.app.Fragment implements Response.Listener<JSONObject>,Response.ErrorListener{
    private WebView description;
    private ProgressDialog pd;
    private ImageView img;
    private CallBackDialog callBackDialog;

    public StoryFragment(CallBackDialog callBackDialog) {
        this.callBackDialog = callBackDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(R.layout.story_fragment,container,false);
        description = (WebView)view.findViewById(R.id.desp);
        img = (ImageView)view.findViewById(R.id.userimg);
        makeStoryRequest();
        return view;
    }

    private void makeStoryRequest(){
        pd = C.getProgressDialog(getActivity());
        HashMap map = new HashMap();
        Net.makeRequest("http://api.appvapp.com/api/pf2/"+Constants.STORY_KEY+"/app/"+ Constants.APP_ID,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Log.e("error",volleyError.toString());
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        PicassoCache.getPicassoInstance(getActivity()).load(model.getImagePath()).placeholder(R.drawable.placeholder).into(img);
        String htmlText = "<html><body style=\"text-align:justify\"><font size=\"2\"> "+model.getAbout()+"</font> </body></Html>";
        callBackDialog.CallBackDialog(model.getTitle());
        description.loadData(String.format(htmlText, model.getAbout()), "text/html", "utf-8");

    }
}
