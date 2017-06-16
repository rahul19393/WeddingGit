package com.app.wedding.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Model.Model;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.network.Net;
import com.app.wedding.network.VolleyErrors;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ShowLikesActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<JSONArray>{
    private RecyclerView likesList;
    private ArrayList<Items> lists = new ArrayList<>();
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_likes);
        likesList = (RecyclerView)findViewById(R.id.likeslist);
        ((TextView)findViewById(R.id.title)).setText("Likes");
        findViewById(R.id.gotoback).setOnClickListener(this);
        if(getIntent() != null)
        makeShowLikeRequest(getIntent().getStringExtra(Constants.POSTID));
    }

    private void setLikesAdapter(){
        LikesAdapter adapter = new LikesAdapter(lists);
        likesList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(ShowLikesActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        likesList.setLayoutManager(layoutManager);
        likesList.setAdapter(adapter);
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
        Toast.makeText(ShowLikesActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONArray jsonArray) {
        pd.dismiss();
        Model model = new Model(jsonArray.toString());
        Model modelArray[] = model.getArray(jsonArray.toString());
        for(Model info : modelArray){
            Model created = new Model(info.getCreatedBy());
            lists.add(new Items(created.getName(),created.getProfileImage()));
        }
        setLikesAdapter();
    }

    private class LikesAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<Items> source;

        public LikesAdapter(List<Items> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_likes, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Items items = source.get(position);
            holder.userName.setText(items.userName);
            PicassoCache.getPicassoInstance(ShowLikesActivity.this).load(items.userImage).placeholder(R.drawable.userplaceholder).into(holder.userImage);
        }
        @Override
        public int getItemCount() {
            return lists.size();
        }
    }

    private void makeShowLikeRequest(String postid){
            pd = C.getProgressDialog(ShowLikesActivity.this);
            Net.makeJSONArrayRequest(C.APP_BASE_URL+"api/post/"+postid+"/app/"+ Constants.APP_ID+"/like",this,this);
        }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView userImage;
        public MyViewHolder(View itemView) {
            super(itemView);
            userName = (TextView)itemView.findViewById(R.id.user_name);
            userImage = (CircleImageView)itemView.findViewById(R.id.likeuserimage);
        }
    }

    private class Items{
        public String userName,userImage;
        public Items(String userName,String userImage) {
            this.userName = userName;
            this.userImage = userImage;
        }
    }
}
