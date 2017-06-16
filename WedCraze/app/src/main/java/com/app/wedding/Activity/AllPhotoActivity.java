package com.app.wedding.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Dialog.ShowZoomImageDialog;
import com.app.wedding.Model.Model;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.network.Net;
import com.app.wedding.network.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class AllPhotoActivity extends AppCompatActivity implements View.OnClickListener,Response.Listener<JSONArray>,Response.ErrorListener{
    private ItemAdapter itemAdapter;
    private GridView photoGrid;
    private String folderId = "";
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_photo);
        ((TextView)findViewById(R.id.title)).setText("My Photo");


        if(getIntent() != null){
            folderId = getIntent().getStringExtra(Constants.FOLDER_ID);
        }

        itemAdapter = new ItemAdapter();
        photoGrid = (GridView)findViewById(R.id.galleryview);
      /*  itemAdapter.add(new Item("Wedding",R.drawable.dummypicwd));
        itemAdapter.add(new Item("Wedding Collection",R.drawable.dummywedingpic));
        itemAdapter.add(new Item("Wedding",R.drawable.dummywedingpic));
        itemAdapter.add(new Item("Wedding Collection",R.drawable.dummypicwd));
        itemAdapter.add(new Item("Wedding",R.drawable.dummywedingpic));
        itemAdapter.add(new Item("Wedding Collection",R.drawable.dummywedingpic));
        itemAdapter.add(new Item("Wedding",R.drawable.dummypicwd));
        itemAdapter.add(new Item("Wedding Collection",R.drawable.dummypicwd));
        itemAdapter.add(new Item("Wedding",R.drawable.dummywedingpic));
        itemAdapter.add(new Item("Wedding Collection",R.drawable.dummypicwd));
        photoGrid.setAdapter(itemAdapter);*/
        findViewById(R.id.gotoback).setOnClickListener(this);

        makeCollectionPhotoRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gotoback:
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(AllPhotoActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONArray jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        Model imageArray[] = model.getArray(jsonObject.toString());
        for(Model info : imageArray){
            itemAdapter.add(new Item("Wedding Collection",info.getImagePath()));
        }
        photoGrid.setAdapter(itemAdapter);
    }

    private class ItemAdapter extends ArrayAdapter<Item> {

        private LayoutInflater inflater=null;
        public ItemAdapter() {
            super(AllPhotoActivity.this, R.layout.custom_image_layout);
            inflater = (LayoutInflater)AllPhotoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            View view = convertView;
            if(view == null){
                view = inflater.inflate(R.layout.custom_image_layout,parent,false);
                holder = new Holder();
                holder.collectionImage = (ImageView)view.findViewById(R.id.singlepic);
                view.setTag(holder);
            }else {
                holder = (Holder) view.getTag();
            }
            final Item item = getItem(position);
          //  holder.collectionImage.setImageResource(item.drawable);
            PicassoCache.getPicassoInstance(AllPhotoActivity.this).load(item.drawable).placeholder(R.drawable.placeholder).into(holder.collectionImage);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  new ShowZoomImageDialog(AllPhotoActivity.this,item.drawable).show();
                    //  startActivity(new Intent(AllPhotoActivity.this,ImageZoomActivity.class).putExtra(Constants.IMAGE,item.drawable));
                }
            });

            return view;
        }

        public class Holder{
            private ImageView collectionImage;
        }
    }
    private class Item{
        public String name;
        public String drawable;

        public Item(String name,String drawable) {
            this.name = name;
            this.drawable = drawable;
        }
    }

    private void makeCollectionPhotoRequest(){
        pd = C.getProgressDialog(this);
        HashMap hashMap = new HashMap();
        Net.makeJSONArrayRequest("http://api.appvapp.com/api/pf5/"+Constants.GALLERY_KEY+"/app/"+Constants.APP_ID+"/folder/"+folderId+"/image",this,this);
    }
}
