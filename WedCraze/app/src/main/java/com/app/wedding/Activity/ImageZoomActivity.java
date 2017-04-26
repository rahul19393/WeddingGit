package com.app.wedding.Activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.app.wedding.Constants.Constants;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.imagezoom.ImageViewTouch;

public class ImageZoomActivity extends Activity {

    private ImageViewTouch mImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);
        mImage = (ImageViewTouch)findViewById(R.id.touchimg);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        PicassoCache.getPicassoInstance(ImageZoomActivity.this).load(getIntent().getStringExtra(Constants.IMAGE)).into(mImage);
    }
}
