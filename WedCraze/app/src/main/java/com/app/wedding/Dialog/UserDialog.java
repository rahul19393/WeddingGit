package com.app.wedding.Dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Activity.WelcomeActivity;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Model.Model;
import com.app.wedding.R;
import com.app.wedding.network.Net;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by LENOVO on 4/19/2017.
 */

public class UserDialog extends Dialog implements View.OnClickListener, Response.Listener<JSONObject>,Response.ErrorListener{

    private ProgressDialog pd;
    private Context context;
    private EditText app_id;
    public UserDialog(@NonNull Context context) {
        super(context, R.style.Theme_Dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_dialog);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        setCanceledOnTouchOutside(false);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        app_id = (EditText)findViewById(R.id.appid);
        findViewById(R.id.submit).setOnClickListener(this);
       // findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                if(!TextUtils.isEmpty(app_id.getText().toString())){
                   // makeAppDataRequest();
                    Constants.APP_ID = app_id.getText().toString();
                    dismiss();
                }else
                    Toast.makeText(context,"please enter appid",Toast.LENGTH_LONG).show();
                break;
           /* case R.id.cancel:
                dismiss();
                break;*/
        }
    }

   /* private void makeAppDataRequest(){
        pd = C.getProgressDialog(context);
        HashMap map = new HashMap();
        Constants.APP_ID = app_id.getText().toString();
        Net.makeRequest(C.APP_BASE_URL+app_id.getText().toString(),map,this,this);
    }
*/
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Log.e("error",volleyError.toString());
        Toast.makeText(context,"something wrong",Toast.LENGTH_LONG).show();
        //context.startActivity(new Intent(context, WelcomeActivity.class));
    }

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
           dismiss();
        }

    }
}
