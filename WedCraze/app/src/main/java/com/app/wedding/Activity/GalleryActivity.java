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
import com.app.wedding.Fragment.GalerryFragment;
import com.app.wedding.Model.Model;
import com.app.wedding.R;
import com.app.wedding.network.Net;
import com.app.wedding.network.VolleyErrors;

import org.json.JSONArray;

import java.util.HashMap;

public class GalleryActivity extends AppCompatActivity {
    private GridView productGrid;
    private Context ctx;
    private ItemAdapter itemAdapter;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        productGrid = (GridView)findViewById(R.id.galleryview);
        itemAdapter = new ItemAdapter();
        makeGalleryRequest();
    }


    private class ItemAdapter extends ArrayAdapter<Item> {

        private LayoutInflater inflater=null;
        public ItemAdapter() {
            super(ctx, R.layout.custom_grid_layout);
            inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            View view = convertView;
            if(view == null){
                view = inflater.inflate(R.layout.custom_grid_layout,parent,false);
                holder = new ItemAdapter.Holder();
                holder.productName = (TextView) view.findViewById(R.id.name);
                holder.collectionImage = (ImageView)view.findViewById(R.id.collectionpic);
                view.setTag(holder);
            }else {
                holder = (Holder) view.getTag();
            }
            final Item item = getItem(position);
            holder.productName.setText(item.name);
            holder.collectionImage.setImageResource(item.drawable);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(GalleryActivity.this, AllPhotoActivity.class).putExtra(Constants.FOLDER_ID,item.id));
                }
            });
            return view;
        }

        public class Holder{
            private ImageView collectionImage;
            private TextView productName;
        }
    }
    private class Item{
        public String name,id;
        public int drawable;

        public Item(String id,String name,int drawable) {
            this.name = name;
            this.drawable = drawable;
            this.id = id;
        }
    }

    private void makeGalleryRequest(){
        pd = C.getProgressDialog(GalleryActivity.this);
        HashMap hashMap = new HashMap();
        Net.makeJSONArrayRequest("http://api.appvapp.com/api/pf5/"+Constants.GALLERY_KEY+"/app/"+ Constants.APP_ID+"/folder",response,errorListener);
    }

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            Toast.makeText(GalleryActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();

        }
    };
    Response.Listener<JSONArray> response = new Response.Listener<JSONArray>() {

        @Override
        public void onResponse(JSONArray jsonArray) {
            pd.dismiss();
            Model model = new Model(jsonArray.toString());
            Model imgArray[] = model.getArray(jsonArray.toString());
            for(Model info : imgArray){
                itemAdapter.add(new Item(info.getId(),info.getName(),R.drawable.folder));
            }
            productGrid.setAdapter(itemAdapter);
        }
    };
}
