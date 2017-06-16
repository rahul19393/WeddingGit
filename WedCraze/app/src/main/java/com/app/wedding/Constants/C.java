package com.app.wedding.Constants;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import com.app.wedding.Activity.CreatePostActivity;
import com.app.wedding.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by LENOVO on 4/18/2017.
 */

public class C {

   // public static String APP_URL = "http://api.appvapp.com/auth/local/58c998f2f6471d0610661818";
   public static String APP_URL = "http://api.appvapp.com/auth/local";
    public static String APP_BASE_URL = "http://api.appvapp.com/";
    public static String GET_TOKEN = "";
    public static String APP_ID = "";
    public static String USER_IMAGE_URL = "";
    public static String USER_NAME = "";
    public static String WELCOME_IMAGE = "";
    public static String WELCOME_TITLE = "";

    public static ProgressDialog getProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        // dialog.setMessage(Message);
        return dialog;
    }
    public static void shareContentExp(Context ctx,String name,Uri imageUrl){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        //sharingIntent.setType("text/html");
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUrl);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, name);
        ctx.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static void share(Context ctx,String url){
        Intent intent = new Intent(Intent.ACTION_SEND);
       // Uri screenshotUri = Uri.parse(url);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        /*String path = null;
        try {
            path = MediaStore.Images.Media.insertImage(ctx.getContentResolver(), url, "", null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Uri screenshotUri = Uri.parse(path);*/

      //  intent.putExtra(Intent.EXTRA_STREAM, url);
        intent.setType("text/plain");
        ctx.startActivity(Intent.createChooser(intent, "Share image via..."));
    }

    public static void selectImage(Context context) {
        final CharSequence[] items = { "Take Photo", "Choose from gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                  //  cameraIntent();
                } else if (items[item].equals("Choose from gallery")) {
                  //  galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputPattern = "dd MMM, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        java.util.Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);

            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    private static String IMAGE_DIRECTORY_NAME= "PicknHop";

    public static String saveToSDCard(Bitmap bt){
        File galleryFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        if (!galleryFile.exists()) {
            if (!galleryFile.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",  Locale.getDefault()).format(new java.util.Date());

        File  mediaFile = new File(galleryFile.getPath() + File.separator + "IMG_" + timeStamp + ".png");
        FileOutputStream out = null;
        String path = "";
        try {
            out = new FileOutputStream(mediaFile);
            bt.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mediaFile.getAbsolutePath();
    }
}
