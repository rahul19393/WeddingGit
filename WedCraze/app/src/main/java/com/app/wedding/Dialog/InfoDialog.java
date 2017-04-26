package com.app.wedding.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.app.wedding.Interface.CallBackDialog;
import com.app.wedding.R;


/**
 * Created by LENOVO on 4/17/2017.
 */

public class InfoDialog extends Dialog implements View.OnClickListener{
    private Context context;
    private CallBackDialog callBackDialog;
    public InfoDialog(Context context, CallBackDialog callBackDialog) {
        super(context, R.style.Theme_Dialog);
        this.context = context;
        this.callBackDialog = callBackDialog;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_info);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        setCanceledOnTouchOutside(true);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        findViewById(R.id.takephoto).setOnClickListener(this);
        findViewById(R.id.galerry).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                dismiss();
                break;
            case R.id.takephoto:
                callBackDialog.CallBackDialog("camera");
                dismiss();
                break;
            case R.id.galerry:
                callBackDialog.CallBackDialog("gallery");
                dismiss();
                break;
        }
    }
}
