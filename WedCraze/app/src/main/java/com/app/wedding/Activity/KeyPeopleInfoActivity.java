package com.app.wedding.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.wedding.Constants.Constants;
import com.app.wedding.Dialog.InfoDialog;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;


public class KeyPeopleInfoActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_key_people_info);
        ImageView image = (ImageView)findViewById(R.id.selectedphoto);
        TextView name = (TextView)findViewById(R.id.name);
        TextView about = (TextView)findViewById(R.id.about);
        TextView relation = (TextView)findViewById(R.id.relation);

        if(getIntent() != null){
            name.setText(getIntent().getStringExtra(Constants.NAME));
            about.setText(getIntent().getStringExtra(Constants.ABOUT));
            relation.setText(getIntent().getStringExtra(Constants.RELATION));
            if(!TextUtils.isEmpty(getIntent().getStringExtra(Constants.IMAGE)))
            PicassoCache.getPicassoInstance(KeyPeopleInfoActivity.this).load(getIntent().getStringExtra(Constants.IMAGE)).placeholder(R.drawable.placeholder).into(image);
            else
                image.setImageResource(R.drawable.placeholder);
        }

      //  new InfoDialog(this).show();
        findViewById(R.id.gotoback).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gotoback:
              //  new InfoDialog(this).show();
                finish();
                break;
        }
    }
}
