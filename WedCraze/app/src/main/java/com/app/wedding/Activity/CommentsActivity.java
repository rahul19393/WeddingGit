package com.app.wedding.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<JSONArray>{
    private RecyclerView commentList;
    private ArrayList<Items> lists = new ArrayList<>();
    private ProgressDialog pd;
    private EditText comment;
    private String postId;
    private  CommentAdapter adapter;
    private boolean firstLoad = true;
    private boolean isCommented = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        commentList = (RecyclerView)findViewById(R.id.commentslist);
        comment = (EditText)findViewById(R.id.editcomment);
        ((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.comments));
        findViewById(R.id.gotoback).setOnClickListener(this);
        findViewById(R.id.sendclick).setOnClickListener(this);
        if(getIntent() != null){
            makeGetCommentsRequest(getIntent().getStringExtra(Constants.POSTID));
            postId = getIntent().getStringExtra(Constants.POSTID);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gotoback:
                if(isCommented)
                    setResult(300);
                 finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case R.id.sendclick:
                if(!TextUtils.isEmpty(comment.getText().toString()))
                addCommentRequest(postId);
                else
                    Toast.makeText(CommentsActivity.this,"please enter comment",Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void setCommentAdapter(){
         adapter = new CommentAdapter(lists);
        commentList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(CommentsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentList.setLayoutManager(layoutManager);
        commentList.setAdapter(adapter);
    }
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Log.e("error",volleyError.toString());
    }

    @Override
    public void onResponse(JSONArray jsonArray) {
        pd.dismiss();
        Model model = new Model(jsonArray.toString());
        Model commentsArray[] = model.getArray(jsonArray.toString());
        if(commentsArray != null){
            if(commentsArray.length > 0){
                for(Model comments : commentsArray){
                    Model created = new Model(comments.getCreatedBy());
                    lists.add(new Items(created.getName(),comments.getCreatedOn(),created.getProfileImage(),comments.getMessage()));
                }
                if(firstLoad)
                setCommentAdapter();
                else
                    adapter.notifyDataSetChanged();
            }
        }

    }

    private class CommentAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<Items> source;

        public CommentAdapter(List<Items> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_comments, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Items items = source.get(position);

            if(!TextUtils.isEmpty(items.userImage))
            PicassoCache.getPicassoInstance(CommentsActivity.this).load(items.userImage).placeholder(R.drawable.placeholder).into(holder.userImage);
           else
               holder.userImage.setImageResource(R.drawable.userplaceholder);
            holder.userName.setText(items.userName);
            holder.comments.setText(items.comment);
            holder.date.setText(C.parseDateToddMMyyyy(items.date));
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView comments,userName,date;
        CircleImageView userImage;
        public MyViewHolder(View itemView) {
            super(itemView);
            comments = (TextView)itemView.findViewById(R.id.cmmnts);
            userName = (TextView)itemView.findViewById(R.id.user_name);
            date = (TextView)itemView.findViewById(R.id.day);
            userImage = (CircleImageView)itemView.findViewById(R.id.userimage);
        }
    }


    private class Items{
        public String userName,date,userImage,comment;

        public Items(String userName, String date, String userImage, String comment) {
            this.userName = userName;
            this.date = date;
            this.userImage = userImage;
            this.comment = comment;
        }
    }

    private void makeGetCommentsRequest(String postid){
        pd = C.getProgressDialog(CommentsActivity.this);
        Net.makeJSONArrayRequest(C.APP_BASE_URL+"api/post/"+postid+"/app/"+ Constants.APP_ID+"/comment",this,this);
    }

    private void addCommentRequest(String postid){
        pd = C.getProgressDialog(CommentsActivity.this);
        JSONObject hashMap = new JSONObject();
        try {
            hashMap.put("message",comment.getText().toString());
            Net.makeRequest(C.APP_BASE_URL+"api/post/"+postid+"/app/"+ Constants.APP_ID+"/comment",hashMap.toString(),responseComment,this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Response.Listener<JSONObject> responseComment = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            if(lists.size()>0){
                lists.clear();
                firstLoad = false;
            }
            isCommented = true;
            comment.setText("");
            makeGetCommentsRequest(getIntent().getStringExtra(Constants.POSTID));
          //  setCommentAdapter();
        }
    };
}
