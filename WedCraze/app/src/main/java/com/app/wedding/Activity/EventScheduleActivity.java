package com.app.wedding.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Model.Model;
import com.app.wedding.R;
import com.app.wedding.network.Net;
import com.app.wedding.network.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EventScheduleActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<JSONArray>{
    private RecyclerView eventsListView;
    private ArrayList<Items> lists = new ArrayList<>();
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_schedule);
        eventsListView = (RecyclerView)findViewById(R.id.events);
        ((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.eventSchedule));
        findViewById(R.id.gotoback).setOnClickListener(this);
        makeCollectionPhotoRequest();
    }

    private void setEventsAdapter(){
        EventsAdapter adapter = new EventsAdapter(lists);
        eventsListView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(EventScheduleActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eventsListView.setLayoutManager(layoutManager);
        eventsListView.setAdapter(adapter);
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

    private void makeCollectionPhotoRequest(){
        pd = C.getProgressDialog(this);
        Net.makeJSONArrayRequest("http://api.appvapp.com/api/pf4/"+Constants.EVENT_KEY+"/app/"+ Constants.APP_ID+"/event",this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(EventScheduleActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONArray jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        Model modelArray[] = model.getArray(jsonObject.toString());
        for(Model info : modelArray){
            lists.add(new Items(info.getName(),info.getVenuee(),info.getDateTime(),info.getAddress1(),info.getAddress2(),info.getCity(),info.getState(),info.getCountry(),info.getPincode()));
        }
        setEventsAdapter();
    }

    private class EventsAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<Items> source;

        public EventsAdapter(List<Items> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_event_schedule, parent, false);
            return new MyViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            Items items = lists.get(position);
            holder.functionName.setText(items.name);
            holder.address.setText(items.address1+", "+items.address2+", "+items.city+", "+items.state+", "+items.pincode);
            holder.venue.setText(items.venue);
            //Date date = C.parseDateToddMMyyyy(items.date);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss.SSS'Z'");
            Date date = null;
            try {
                 date = format.parse(items.date);
                System.out.println(date);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
            String year = new SimpleDateFormat("YYYY").format(cal.getTime());
            String dt = new SimpleDateFormat("dd").format(cal.getTime());
            String timeset = new SimpleDateFormat("HH:mm").format(cal.getTime());
            //String year = new SimpleDateFormat("YYYY").format(cal.getTime());
            holder.month.setText(monthName);
           holder.year.setText(year);
            holder.date.setText(""+dt);
            holder.time.setText(timeset);

            holder.mapClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String map = "http://maps.google.co.in/maps?q=" + holder.address.getText().toString();

                    /// String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 28.7041, 77.1025);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return lists.size();
        }
    }

    private class Items{
        public String name,date,address1,address2,city,state,country,pincode,venue;

        public Items(String name,String venue, String date, String address1, String address2, String city, String state, String country,String pincode) {
            this.name = name;
            this.date = date;
            this.address1 = address1;
            this.address2 = address2;
            this.city = city;
            this.state = state;
            this.country = country;
            this.pincode = pincode;
            this.venue = venue;
        }
    }
    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView functionName,date,year,month,time,address,venue;
        LinearLayout mapClick;
        public MyViewHolder(View itemView) {
            super(itemView);
            functionName = (TextView)itemView.findViewById(R.id.fnname);
            year = (TextView)itemView.findViewById(R.id.year);
            month = (TextView)itemView.findViewById(R.id.month);
            date = (TextView)itemView.findViewById(R.id.date);
            time = (TextView)itemView.findViewById(R.id.time);
            venue = (TextView)itemView.findViewById(R.id.venue);
            address = (TextView)itemView.findViewById(R.id.address);
            mapClick = (LinearLayout)itemView.findViewById(R.id.map);
        }
    }
}
