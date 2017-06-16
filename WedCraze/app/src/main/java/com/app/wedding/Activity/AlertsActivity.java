package com.app.wedding.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Model.Model;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.Util.MyApplication;
import com.app.wedding.network.Net;
import com.app.wedding.network.VolleyErrors;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AlertsActivity extends AppCompatActivity implements View.OnClickListener,Response.Listener<JSONArray>,Response.ErrorListener{

    private RecyclerView alertList;
    private ArrayList<Items> lists = new ArrayList<>();
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);
        alertList = (RecyclerView)findViewById(R.id.alertslist);
        ((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.announcement));
       // MyApplication.getInstance().trackScreenView("Overview Fragment");
        makeNotificationRequest();
        findViewById(R.id.gotoback).setOnClickListener(this);
    }

    private void setCommentAdapter(){
        AlertAdapter adapter = new AlertAdapter(lists);
        alertList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(AlertsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        alertList.setLayoutManager(layoutManager);
        alertList.setAdapter(adapter);
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

    private void makeNotificationRequest(){
        pd = C.getProgressDialog(AlertsActivity.this);
        Net.makeJSONArrayRequest(C.APP_BASE_URL+"api/notification/"+ Constants.APP_ID+"/0/100",this,this);

    }
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Log.e("error",volleyError.toString());
        Toast.makeText(AlertsActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONArray jsonArray) {
        pd.dismiss();
        Model model = new Model(jsonArray.toString());
        Model modelArray[] = model.getArray(jsonArray.toString());
        for(Model info : modelArray){
            lists.add(new Items(info.getUserName(),info.getCreatedOn(),info.getProfilePic(),info.getText(),info.getUrl()));
        }
        setCommentAdapter();
    }

    private class AlertAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<Items> source;
        public AlertAdapter(List<Items> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_alert, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Items items = source.get(position);
            if(!TextUtils.isEmpty(items.userImage))
           PicassoCache.getPicassoInstance(AlertsActivity.this).load(items.userImage).placeholder(R.drawable.userplaceholder).into(holder.userImage);
          else
              holder.userImage.setImageResource(R.drawable.userplaceholder);
            if(!TextUtils.isEmpty(items.eventImg))
           PicassoCache.getPicassoInstance(AlertsActivity.this).load(items.eventImg).placeholder(R.drawable.placeholder).into(holder.eventImg);
          else
              holder.eventImg.setImageResource(R.drawable.placeholder);
               // PicassoCache.getPicassoInstance(AlertsActivity.this).load(items.eventImg).placeholder(R.drawable.userplaceholder).into(holder.eventImg);

            holder.comments.setText(items.comment);
            holder.userName.setText(items.userName);
            holder.date.setText(C.parseDateToddMMyyyy(items.date));

        }

        @Override
        public int getItemCount() {
            return lists.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView comments,userName,date;
        CircleImageView userImage,eventImg;
        public MyViewHolder(View itemView) {
            super(itemView);
            comments = (TextView)itemView.findViewById(R.id.cmmnts);
            userName = (TextView)itemView.findViewById(R.id.user_name);
            date = (TextView)itemView.findViewById(R.id.day);
            userImage = (CircleImageView)itemView.findViewById(R.id.userimage);
            eventImg = (CircleImageView)itemView.findViewById(R.id.funcimg);
        }
    }


    private class Items{
        public String userName,date,userImage,comment,eventImg;

        public Items(String userName, String date, String userImage, String comment,String eventImg) {
            this.userName = userName;
            this.date = date;
            this.userImage = userImage;
            this.eventImg = eventImg;
            this.comment = comment;
        }
    }
}
