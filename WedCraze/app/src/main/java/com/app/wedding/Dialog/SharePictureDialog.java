package com.app.wedding.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.wedding.R;

import java.io.File;
import java.util.List;

/**
 * Created by LENOVO on 4/26/2017.
 */

public class SharePictureDialog extends Dialog implements View.OnClickListener{
    private Context context;
    private String sharedPath;
    public SharePictureDialog(@NonNull Context context,String sharedPath) {
        super(context, R.style.Theme_Dialog);
        this.context = context;
        this.sharedPath = sharedPath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_dialog);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        setCanceledOnTouchOutside(true);
        LinearLayout fb_click = (LinearLayout)findViewById(R.id.fb_share_click);
        LinearLayout tw_click = (LinearLayout)findViewById(R.id.twit_share_click);
        LinearLayout linkedin = (LinearLayout)findViewById(R.id.linked_share_click);
        fb_click.setOnClickListener(this);
        tw_click.setOnClickListener(this);
        linkedin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fb_share_click:
                postToFacebook(sharedPath);
                break;
            case R.id.twit_share_click:
                postToTwitter(sharedPath);
                break;
            case R.id.linked_share_click:
                postToLinkedin(sharedPath);
                break;
        }
        dismiss();
    }
    private void postToFacebook(String path) {
        Intent fabIntent = new Intent(Intent.ACTION_SEND);
        // Set the MIME type
        fabIntent.setType("text/plain");
        File media = new File(path);
        Uri uri = Uri.fromFile(media);
        fabIntent.putExtra(Intent.EXTRA_TEXT, path);
        PackageManager packManager = context.getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(
                fabIntent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName
                    .startsWith("com.facebook.katana")) {
                fabIntent.setClassName(resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            context.startActivity(fabIntent);
        } else {
            // String url =
            // "https://twitter.com/intent/tweet?source=webclient&text=Hey%20check%20This%20Out!%20http://amsyt.com/";
          /*  String url = "https://facebook.com/intent/facebook?source=webclient";
            // String url =
            // "https://twitter.com/intent/tweet?source=webclient&text=check::"+image_url;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);*/
            Toast.makeText(context,"app not installed",Toast.LENGTH_LONG).show();
        }
    }

    private void postToTwitter(String path) {
        // TODO Auto-generated method stub
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        // Set the MIME type
        tweetIntent.setType("text/plain");
        // Create the URI from the media
        //for image
        File media = new File(path);
        Uri uri = Uri.fromFile(media);

        tweetIntent.putExtra(Intent.EXTRA_TEXT, path);
        PackageManager packManager = context.getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(
                tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName
                    .startsWith("com.twitter.android")) {
                tweetIntent.setClassName(resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            context.startActivity(tweetIntent);
            /*context.overridePendingTransition(R.anim.pull_in_right,
                    R.anim.push_out_left);*/
        } else {
           /* String url = "https://twitter.com/intent/tweet?source=webclient";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);*/
            Toast.makeText(context,"app not installed",Toast.LENGTH_LONG).show();
        }
    }

    private void postToLinkedin(String path){
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        // Set the MIME type
        tweetIntent.setType("text/plain");

        // Create the URI from the media
        File media = new File(path);
        Uri uri = Uri.fromFile(media);

        tweetIntent.putExtra(Intent.EXTRA_TEXT, path);
        PackageManager packManager = context.getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(
                tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName
                    .startsWith("com.linkedin.android")) {
                tweetIntent.setClassName(resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            context.startActivity(tweetIntent);
            /*context.overridePendingTransition(R.anim.pull_in_right,
                    R.anim.push_out_left);*/
        } else {
           /* String url = "https://linkedin.com/intent/linked?source=webclient";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);*/
            Toast.makeText(context,"app not installed",Toast.LENGTH_LONG).show();

        }
    }

}
