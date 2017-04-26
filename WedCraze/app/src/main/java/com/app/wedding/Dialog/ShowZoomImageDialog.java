package com.app.wedding.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.imagezoom.ImageViewTouch;

/**
 * Created by LENOVO on 4/17/2017.
 */

public class ShowZoomImageDialog extends Dialog {
    private ImageViewTouch mImage;
    private String imageValue;
    public ShowZoomImageDialog(Context context, String imageValue) {
        super(context, R.style.Theme_Dialog);
        this.imageValue = imageValue;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom_image_dialog);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        setCanceledOnTouchOutside(true);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mImage = (ImageViewTouch)findViewById(R.id.touchimg);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        PicassoCache.getPicassoInstance(getContext()).load(imageValue).into(mImage);

    }




}
