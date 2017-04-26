package com.app.wedding.Activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Model.Model;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.network.Net;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class KeyContactsActivity extends AppCompatActivity implements View.OnClickListener,Response.Listener<JSONArray>,Response.ErrorListener{

    private ViewPager viewPager;
    private ContactAdapter contactAdapter;
    private ArrayList<ImageView> dots = new ArrayList<>();
    private ProgressDialog pd;
    private TextView gpName;
    private ArrayList<adapterItem> adapterItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_contacts);
        viewPager = (ViewPager)findViewById(R.id.keycontactpager);
        ((TextView)findViewById(R.id.title)).setText("Contacts");
        gpName  = (TextView)findViewById(R.id.groupname);

        findViewById(R.id.gotoback).setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                    setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
       // initDots(5);
        makeKeyContactsRequest();
    }
    private void initDots(int size) {
        LinearLayout lv = (LinearLayout) findViewById(R.id.pro_dots);
        lv.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView dotimg = new ImageView(KeyContactsActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(15, 15);
            layoutParams.leftMargin = 3;
            dotimg.setLayoutParams(layoutParams);
            lv.addView(dotimg);
        }

        dots.clear();
        for (int i = 0; i < lv.getChildCount(); i++) {
            ImageView img = (ImageView) lv.getChildAt(i);
            dots.add(img);
        }
        setIndicator(0);
    }

    private void setIndicator(int position) {
        for (int i = 0; i < dots.size(); i++) {
            ImageView img = dots.get(i);
            img.setImageResource(i == position ? R.drawable.yellow_indicator : R.drawable.grey_indicator);
        }
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
    }

    @Override
    public void onResponse(JSONArray jsonArray) {
        pd.dismiss();
        Model model = new Model(jsonArray.toString());
        Model modelArray[] = model.getArray(jsonArray.toString());
        for(Model info : modelArray){
            adapterItems.add(new adapterItem(info.getPeopleModel(),info.getName()));
        }
        initDots(modelArray.length);
        contactAdapter = new ContactAdapter(adapterItems);
        viewPager.setAdapter(contactAdapter);
        contactAdapter.notifyDataSetChanged();
      //  viewPager.setCurrentItem(0);

    }


    private class ContactAdapter extends PagerAdapter {

        Context context;
        RecyclerView contactList;
        private ArrayList<adapterItem> pagerItemList;
        private ArrayList<Items> lists = new ArrayList<>();

        public ContactAdapter(ArrayList<adapterItem> pagerItemList) {
            this.pagerItemList = pagerItemList;
        }
//int[] imageId = {R.drawable.image1, R.drawable.image1, R.drawable.image1, R.drawable.image1};
       // ArrayList<String> imageUrls = new ArrayList<>();

       /* public ContactAdapter(Context context){
            this.context = context;

            //this.imageUrls = imageUrls;
        }*/

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = KeyContactsActivity.this.getLayoutInflater();

            final View viewItem = inflater.inflate(R.layout.key_contact_list, container, false);
            //final ImageView imageView = (ImageView) viewItem.findViewById(R.id.imageView3);

             contactList = (RecyclerView) viewItem.findViewById(R.id.contactlist);
            gpName.setText(pagerItemList.get(position).groupName);
            Model peopleArray[] = pagerItemList.get(position).groupArray;
            if(lists.size()>0)
                lists.clear();
            for(Model info : peopleArray){
                lists.add(new Items(info.getName(),info.getRelation(),info.getImagePath(),info.getAbout()));
            }
            setCommentAdapter();
            ((ViewPager)container).addView(viewItem);
            return viewItem;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return adapterItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            // TODO Auto-generated method stub

            return view == ((View)object);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ((ViewPager) container).removeView((View) object);
        }


        private void setCommentAdapter(){
            CommentAdapter adapter = new CommentAdapter(lists);
            contactList.setItemAnimator(new DefaultItemAnimator());
            LinearLayoutManager layoutManager = new LinearLayoutManager(KeyContactsActivity.this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            contactList.setLayoutManager(layoutManager);
            contactList.setAdapter(adapter);
           // adapter.notifyDataSetChanged();
        }
        private class CommentAdapter extends RecyclerView.Adapter<MyViewHolder>{
            private List<Items> source;

            public CommentAdapter(List<Items> source) {
                this.source = source;
            }

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_key_contacts, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {

                final Items items = source.get(position);
                holder.name.setText(items.userName);
                holder.relation.setText(items.relation);
                if(!TextUtils.isEmpty(items.image))
                PicassoCache.getPicassoInstance(KeyContactsActivity.this).load(items.image).placeholder(R.drawable.userplaceholder).into(holder.userPic);
              else
                  holder.userPic.setImageResource(R.drawable.userplaceholder);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(KeyContactsActivity.this,KeyPeopleInfoActivity.class).putExtra(Constants.NAME,items.userName).putExtra(Constants.RELATION,items.relation).putExtra(Constants.IMAGE,items.image).putExtra(Constants.ABOUT,items.about));
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return lists.size();
            }
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            TextView name,relation;
            ImageView userPic;
            public MyViewHolder(View itemView) {
                super(itemView);
               name = (TextView)itemView.findViewById(R.id.user_name);
                relation = (TextView)itemView.findViewById(R.id.relation);
                userPic = (ImageView) itemView.findViewById(R.id.usercommentImg);
            }
        }

        private class Items{
            public String userName,relation,image,about;

            public Items(String userName, String relation, String image,String about) {
                this.userName = userName;
                this.relation = relation;
                this.image = image;
                this.about = about;
            }
        }
    }


    private class adapterItem{
        public Model groupArray[];
        public String groupName;

        public adapterItem(Model[] groupArray, String groupName) {
            this.groupArray = groupArray;
            this.groupName = groupName;
        }
    }

    private void makeKeyContactsRequest(){
        pd = C.getProgressDialog(this);
        Net.makeJSONArrayRequest("http://api.appvapp.com/api/pf3/"+Constants.CONTACTS_KEY+"/app/"+ Constants.APP_ID+"/group",this,this);

    }
}
