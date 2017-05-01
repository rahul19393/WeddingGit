package com.app.wedding.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Fragment.GalerryFragment;
import com.app.wedding.Fragment.HomeFragment;
import com.app.wedding.Fragment.StoryFragment;
import com.app.wedding.Interface.CallBackDialog;
import com.app.wedding.R;
import com.app.wedding.network.Net;

import org.json.JSONObject;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private DrawerLayout drawer;
    private TextView title,headerText;
    private ImageView homeIcon,galerryIcon,storyIcon;
    private android.support.v4.app.FragmentTransaction transaction;
    private final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        title = (TextView)findViewById(R.id.title);
        headerText = (TextView)findViewById(R.id.headername);
        homeIcon = (ImageView)findViewById(R.id.homeicon);
        galerryIcon = (ImageView)findViewById(R.id.galerryicon);
        storyIcon = (ImageView)findViewById(R.id.storyicon);
        findViewById(R.id.menu).setOnClickListener(this);
        findViewById(R.id.home).setOnClickListener(this);
        findViewById(R.id.gallery).setOnClickListener(this);
        findViewById(R.id.story).setOnClickListener(this);
        findViewById(R.id.homeclick).setOnClickListener(this);
        findViewById(R.id.viewgallery).setOnClickListener(this);
        findViewById(R.id.viewstory).setOnClickListener(this);
        findViewById(R.id.viewkeycontacts).setOnClickListener(this);
        findViewById(R.id.viewschedule).setOnClickListener(this);
        findViewById(R.id.viewalerts).setOnClickListener(this);
        findViewById(R.id.signin).setOnClickListener(this);
        title.setText(C.USER_NAME);
        replaceFragment(new HomeFragment());
        headerText.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.MEDIA_CONTENT_CONTROL}, PERMISSION_REQUEST_CODE);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }


    private void setDefaultView(){
        homeIcon.setColorFilter(ContextCompat.getColor(HomeActivity.this,R.color.colorPrimary));
        galerryIcon.setColorFilter(ContextCompat.getColor(HomeActivity.this,R.color.colorPrimary));
        storyIcon.setColorFilter(ContextCompat.getColor(HomeActivity.this,R.color.colorPrimary));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home:
                setHome();
                break;
            case R.id.gallery:
                setGallery();
               // FragmentTransaction.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case R.id.story:
                setStory();
                break;
            case R.id.menu:
                drawer.openDrawer(Gravity.LEFT);
                break;
            case R.id.homeclick:
                drawer.closeDrawer(GravityCompat.START);
                setHome();
                break;
            case R.id.viewgallery:
                drawer.closeDrawer(GravityCompat.START);
                setGallery();
                break;
            case R.id.viewstory:
                drawer.closeDrawer(GravityCompat.START);
                setStory();
                break;
            case R.id.viewkeycontacts:
                startActivity(new Intent(this, KeyContactsActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case R.id.viewschedule:
                startActivity(new Intent(this, EventScheduleActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case R.id.viewalerts:
                startActivity(new Intent(this, AlertsActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case R.id.signin:
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                break;
        }
    }

    private void setHome(){
        setDefaultView();
        homeIcon.setColorFilter(ContextCompat.getColor(HomeActivity.this,R.color.lightPinkColor));
        title.setText(C.USER_NAME);
        headerText.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
        replaceFragment(new HomeFragment());
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    private void setGallery(){
        setDefaultView();
        galerryIcon.setColorFilter(ContextCompat.getColor(HomeActivity.this,R.color.lightPinkColor));
        headerText.setText("Wedding Photos");
        headerText.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
        replaceFragment(new GalerryFragment(HomeActivity.this));
    }
    private void setStory(){
        setDefaultView();
        headerText.setText("Title");
        headerText.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
        storyIcon.setColorFilter(ContextCompat.getColor(HomeActivity.this,R.color.lightPinkColor));
        replaceFragment(new StoryFragment(callBackDialog));
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    CallBackDialog callBackDialog = new CallBackDialog() {
        @Override
        public void CallBackDialog(String action) {
            headerText.setText(action);
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Do you want to close the application?")
                    .setCancelable(false)
                    .setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).setNegativeButton("NO", null).show();
        }
    }


    private void makeGetAppDataRequest(){
        JSONObject map = new JSONObject();
     //   Net.makeRequest("http://api.appvapp.com/api/app/58f6011e6cf99b725ccddfed",this,this);
    }

}
