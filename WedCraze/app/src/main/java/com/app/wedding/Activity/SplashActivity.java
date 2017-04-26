package com.app.wedding.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.app.wedding.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                   overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, 3000);
    }
}
