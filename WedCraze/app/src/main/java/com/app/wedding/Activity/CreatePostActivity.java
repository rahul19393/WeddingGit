package com.app.wedding.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Dialog.InfoDialog;
import com.app.wedding.Interface.CallBackDialog;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.network.MultipartRequest;
import com.app.wedding.network.Net;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePostActivity extends Activity implements Response.ErrorListener{
    private int REQUEST_CAMERA = 1;
    private int SELECT_FILE = 2;
    private LinearLayout addPost;
    //AlertDialog.Builder builder;
    private ProgressDialog pd;
    private EditText comment;
    private ImageView postImage;
    private String imagePath = "";
    private final int PERMISSION_REQUEST_CODE = 100;
    private ArrayList<String> permissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        comment = (EditText)findViewById(R.id.comment);
        postImage = (ImageView)findViewById(R.id.selectedimage);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        params.x = 0;
        params.height = (int) (height-(height/3.1));
        params.width = width-(width/9);
        params.y = 0;
        this.getWindow().setAttributes(params);
        setFinishOnTouchOutside(true);
        ((TextView)findViewById(R.id.name)).setText(C.USER_NAME);
        ImageView userImg = (ImageView)findViewById(R.id.userimagepost);
        addPost = (LinearLayout)findViewById(R.id.addphoto);

        if(!TextUtils.isEmpty(C.USER_IMAGE_URL))
        PicassoCache.getPicassoInstance(CreatePostActivity.this).load(C.USER_IMAGE_URL).placeholder(R.drawable.userplaceholder).into(userImg);
        else
            userImg.setImageResource(R.drawable.userplaceholder);
        findViewById(R.id.addph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!checkPermission()) {
                        ActivityCompat.requestPermissions(CreatePostActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.MEDIA_CONTENT_CONTROL}, PERMISSION_REQUEST_CODE);
                    }else
                        new InfoDialog(CreatePostActivity.this,callBackDialog).show();
                }

            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  if(!(TextUtils.isEmpty(imagePath) && TextUtils.isEmpty(comment.getText().toString())))
                if((TextUtils.isEmpty(imagePath) && TextUtils.isEmpty(comment.getText().toString()))){
                }else
                makePostRequest(imagePath);
            }
        });
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.CAMERA);
        permissionList.add(Manifest.permission.MEDIA_CONTENT_CONTROL);
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
    private CallBackDialog callBackDialog = new CallBackDialog() {
        @Override
        public void CallBackDialog(String action) {
            if(action.equals("camera")){
               // cameraIntent();
                cameraPics();
            }else{
                galleryIntent();
            }
        }
    };

    /*for photo*/
    private void selectImage() {

    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);//
       startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }


    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(CreatePostActivity.this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri selectedImage = data.getData();
        // ProductImagePathArray.add(getPath(AddProductActivity.this,selectedImage));
        //setTag = setTag + 1;
        imagePath = getPath(CreatePostActivity.this,selectedImage);
        setImageInLayout(bm,false,null);
    }
    private void setImageInLayout(Bitmap bitmap, boolean isVideo, Uri uri){
       /* View productMediaView = getLayoutInflater().inflate(R.layout.add_product_image, null);
        final ImageView prdImg = (ImageView) productMediaView.findViewById(R.id.pdimg);
dthtr
        prdImg.setImageBitmap(bitmap);
        // prdImg.setTag(setTag);
        addPost.addView(productMediaView);*/
        postImage.setImageBitmap(bitmap);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ProductImagePathArray.add(destination.getPath());
        imagePath = destination.getPath();
        setImageInLayout(thumbnail,false,null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CAMERA){
            onCaptureImageResult(data);
        }else if(requestCode == SELECT_FILE){
            onSelectFromGalleryResult(data);
        }else if(requestCode == 333){
                if (resultCode == Activity.RESULT_OK) {
                    try {
                       Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                        postImage.setImageBitmap(thumbnail);
                        imagePath = getRealPathFromURI(CreatePostActivity.this,imageUri);
                        Log.e("uri",imagePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
        }else if(requestCode == PERMISSION_REQUEST_CODE){
            if(checkPermission())
                new InfoDialog(CreatePostActivity.this,callBackDialog).show();
        }
    }

    //Mulitpart request for image upload
    private void makePostRequest(final String path) {
        new AsyncTask<Void,Void,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = C.getProgressDialog(CreatePostActivity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                return path;
            }

            @Override
            protected void onPostExecute(String image) {
                super.onPostExecute(image);
               // if (!TextUtils.isEmpty(image)) {
                    MultipartRequest multipartRequest = new MultipartRequest(CreatePostActivity.this, C.APP_BASE_URL+"api/post/app/"+Constants.APP_ID,image,comment.getText().toString(), r_upload, CreatePostActivity.this);
                    RequestQueue requestQueue = Volley.newRequestQueue(CreatePostActivity.this);
                    multipartRequest.setShouldCache(false);
                    requestQueue.add(multipartRequest);
                    Log.e("image===",multipartRequest.toString());
               // }else {
               //     pd.dismiss();
               // }
            }
        }.execute();
    }
    Response.Listener<JSONObject> r_upload = new Response.Listener<JSONObject>(){
        @Override
        public void onResponse(JSONObject response) {
            pd.dismiss();
            Toast.makeText(CreatePostActivity.this,"post created",Toast.LENGTH_LONG).show();
            setResult(200);
            finish();
        }
    };

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Log.e("error",volleyError.toString());
    }
    private Uri imageUri;
    private void cameraPics(){
       ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
         imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 333);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
