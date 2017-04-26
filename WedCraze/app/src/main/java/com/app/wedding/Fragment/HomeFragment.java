package com.app.wedding.Fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Activity.AlertsActivity;
import com.app.wedding.Activity.CommentsActivity;
import com.app.wedding.Activity.CreatePostActivity;
import com.app.wedding.Activity.EventScheduleActivity;
import com.app.wedding.Activity.HomeActivity;
import com.app.wedding.Activity.KeyContactsActivity;
import com.app.wedding.Activity.ShowLikesActivity;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Dialog.SharePictureDialog;
import com.app.wedding.Dialog.ShowZoomImageDialog;
import com.app.wedding.Model.Model;
import com.app.wedding.Model.NamePair;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.network.Net;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LENOVO on 4/12/2017.
 */

public class HomeFragment extends android.support.v4.app.Fragment implements View.OnClickListener,Response.ErrorListener,Response.Listener<JSONArray>{

    private RecyclerView weddingList;
    private ArrayList<Items> lists = new ArrayList<>();
    private ProgressDialog pd;
    private int selectedPosition = 0;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private  WedingAdapter adapter;
    private boolean onFirstLoad = false;
    private SwipeRefreshLayout swipeContainer;
    private boolean isReferesh,isPagination;
    private LinearLayoutManager mLayoutManager;
    private int page = 0;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(R.layout.home_fragment,container,false);
        weddingList = (RecyclerView)view.findViewById(R.id.weddinglist);

        view.findViewById(R.id.events).setOnClickListener(this);
        view.findViewById(R.id.alert).setOnClickListener(this);
        view.findViewById(R.id.keycontact).setOnClickListener(this);
        view.findViewById(R.id.createpost).setOnClickListener(this);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if(lists.size()>0)
                    lists.clear();
                isReferesh = true;
                onFirstLoad = true;
                makeGetPostRequest(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        weddingList.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (!isPagination)
                    {
                        //  if(totalItemCount > 40)
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            isPagination = true;
                            Log.e("totalItemCount",""+totalItemCount);
                            Log.v("...", "Last Item Wow !");
                            onFirstLoad = true;
                            makeGetPostRequest(++page);
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        });
        makeGetPostRequest(0);
        return view;
    }

    private void setWeddingAdapter(){
         adapter = new WedingAdapter(lists);
        weddingList.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        weddingList.setLayoutManager(mLayoutManager);
        weddingList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.events:
                startActivity(new Intent(getActivity(), EventScheduleActivity.class));
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case  R.id.alert:
                startActivity(new Intent(getActivity(), AlertsActivity.class));
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case  R.id.keycontact:
                startActivity(new Intent(getActivity(), KeyContactsActivity.class));
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case R.id.createpost:
                startActivityForResult(new Intent(getActivity(), CreatePostActivity.class),100);
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if(!isReferesh)
            pd.dismiss();
        else
            isReferesh = false;
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Log.e("error",volleyError.toString());
    }

    @Override
    public void onResponse(JSONArray jsonObject) {
        //isPagination = false;
        swipeContainer.setRefreshing(false);
        Log.e("response",jsonObject.toString());
        Model model  = new Model(jsonObject.toString());
        Model infoArray[] = model.getArray(jsonObject.toString());
        isPagination = infoArray.length > 0 ? false : true;
        for(Model info : infoArray){
            Model created = new Model(info.getCreatedBy());
            if(!info.isKeyNull(NamePair.URL))
            lists.add(new Items(info.getId(),created.getName(),info.getCreatedOn(),info.getMessage(),info.getUrl(),created.getProfileImage(),""+info.getComments(),""+info.getLikes(),!info.isKeyNull(NamePair.COMMENT)? new Model(info.getComment()) : null,info.isLike()));
            else
                lists.add(new Items(info.getId(),created.getName(),info.getCreatedOn(),info.getMessage(),"",created.getProfileImage(),""+info.getComments(),""+info.getLikes(),!info.isKeyNull(NamePair.COMMENT)? new Model(info.getComment()) : null,info.isLike()));
        }
        if(!onFirstLoad)
        setWeddingAdapter();
        else{
            if(adapter.getItemCount() > 0)
                adapter.notifyDataSetChanged();
        }
        if(!isReferesh)
            pd.dismiss();
        else {
            page = 0;
            isReferesh = false;
            isPagination = false;
        }
    }

    private class WedingAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<Items> source;

        public WedingAdapter(List<Items> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_custom_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            final Items items = source.get(position);
            holder.postDescription.setText(items.functionName);
            if(!TextUtils.isEmpty(items.userImage))
                PicassoCache.getPicassoInstance(getActivity()).load(items.userImage).placeholder(R.drawable.userplaceholder).into(holder.userImage);
            else
                holder.userImage.setImageResource(R.drawable.placeholder);

            if(!TextUtils.isEmpty(items.functionImage)) {
                holder.eventImageLayout.setVisibility(View.VISIBLE);
                PicassoCache.getPicassoInstance(getActivity()).load(items.functionImage).noFade().placeholder(R.drawable.placeholder).into(holder.imageEvent);
            }
            else
            holder.eventImageLayout.setVisibility(View.GONE);

/*for comment section*/
            Model comment = items.commentModel;
            if(comment != null){
                Model info = new Model(comment.getCreatedBy());
                holder.commentSection.setVisibility(View.VISIBLE);
                holder.commentDate.setText(C.parseDateToddMMyyyy(comment.getCreatedOn()));
                holder.userCommentName.setText(info.getName());
                holder.userComment.setText(comment.getMessage());
                if(!TextUtils.isEmpty(info.getProfileImage()))
                    PicassoCache.getPicassoInstance(getActivity()).load(info.getProfileImage()).into(holder.userCommentImage);
                else
                    holder.userCommentImage.setImageResource(R.drawable.userplaceholder);
                holder.userName.setText(items.userName);
            }else
                holder.commentSection.setVisibility(View.GONE);

            holder.postDate.setText(C.parseDateToddMMyyyy(items.date));
            holder.commentsCount.setText(items.commentCounts+" Comments");
            holder.likeTextView.setText(items.likeCounts+" people like this");


            holder.likeIcon.setColorFilter(items.postLike ? ContextCompat.getColor(getActivity(),R.color.lightPinkColor) : ContextCompat.getColor(getActivity(),R.color.regularTextColor));

            holder.comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getActivity(), CommentsActivity.class).putExtra(Constants.POSTID,items.postId),200);
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            });
            holder.commentsCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getActivity(),CommentsActivity.class).putExtra(Constants.POSTID,items.postId),200);
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            });
            holder.likeClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ShowLikesActivity.class).putExtra(Constants.POSTID,items.postId));
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            });
            holder.sharePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Bitmap productBitmap = ((BitmapDrawable) holder.imageEvent.getDrawable()).getBitmap();
                    if (productBitmap != null) {
                      //  C.shareContentExp(getActivity(), "demo", C.getImageUri(getActivity(), productBitmap));
                        if(!TextUtils.isEmpty(items.functionImage))
                            C.share(getActivity(), items.functionImage);
                       // C.shareContentExp(getActivity(), "demo", items.functionImage);
                    }*/
                  //  if(!TextUtils.isEmpty(items.functionImage))
               //         new SharePictureDialog(getActivity(),items.functionImage).show();
                        //onShareClick(items.functionImage);
                }
            });
            holder.likePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!items.postLike){

                        selectedPosition = position;
                        likeApiRequest(items.postId);
                    }
                }
            });

            holder.imageEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 //   startActivity(new Intent(getActivity(), ImageZoomActivity.class).putExtra(Constants.IMAGE,items.functionImage));
                    new ShowZoomImageDialog(getActivity(),items.functionImage).show();
                }
            });

            holder.allComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(),CommentsActivity.class).putExtra(Constants.POSTID,items.postId));
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName,date,postDescription,commentsCount,postDate,likeTextView,userCommentName,userComment,commentDate,allComment;
        LinearLayout comments,likeClick,sharePost,likePost;
        ImageView imageEvent,userImage,userCommentImage,likeIcon;
        RelativeLayout eventImageLayout;
        LinearLayout commentSection;

        public MyViewHolder(View itemView) {
            super(itemView);
            comments = (LinearLayout)itemView.findViewById(R.id.comments);
            commentsCount = (TextView)itemView.findViewById(R.id.commentscount);
            likeClick = (LinearLayout)itemView.findViewById(R.id.likeclick);
            sharePost = (LinearLayout)itemView.findViewById(R.id.share);
            imageEvent = (ImageView)itemView.findViewById(R.id.eventimg);
            likePost = (LinearLayout)itemView.findViewById(R.id.likesection);
            userName = (TextView)itemView.findViewById(R.id.username);
            postDate = (TextView)itemView.findViewById(R.id.postdate);
            userImage = (ImageView)itemView.findViewById(R.id.userimage);
            likeIcon = (ImageView)itemView.findViewById(R.id.likesrc);
            likeTextView = (TextView)itemView.findViewById(R.id.liketext);
            userCommentImage = (ImageView)itemView.findViewById(R.id.userimagecmnt);
            postDescription = (TextView)itemView.findViewById(R.id.postdescription);
            userCommentName = (TextView)itemView.findViewById(R.id.user_name);
            userComment = (TextView)itemView.findViewById(R.id.cmmnts);
            commentDate = (TextView)itemView.findViewById(R.id.comntdate);
            allComment = (TextView)itemView.findViewById(R.id.vewallcomment);
            commentSection = (LinearLayout) itemView.findViewById(R.id.commentsection);
            eventImageLayout = (RelativeLayout)itemView.findViewById(R.id.eventimagelayout);
        }
    }

    private class Items{
        public String userName,date,functionName,functionImage,userImage,postId,commentCounts,likeCounts,message;
        Model commentModel;
        boolean postLike;

        public Items(String postId,String userName, String date, String functionName, String functionImage, String userImage,String commentCounts,String likeCounts,Model commentModel,boolean postLike) {
            this.userName = userName;
            this.date = date;
            this.functionName = functionName;
            this.functionImage = functionImage;
            this.postId = postId;
            this.userImage = userImage;
            this.commentCounts = commentCounts;
            this.likeCounts = likeCounts;
            this.commentModel = commentModel;
            this.postLike = postLike;
        }
    }
/*api/post/app/appid*/
private void makeGetPostRequest(int pageCount){
    if(!isReferesh)
    pd = C.getProgressDialog(getActivity());
    Net.makeJSONArrayRequest(C.APP_BASE_URL+"api/post/app/"+Constants.APP_ID+"/"+""+pageCount+"/10",this,this);
}
    private void likeApiRequest(String postid){
        pd = C.getProgressDialog(getActivity());
        HashMap hashMap = new HashMap();
        Net.makeRequest(C.APP_BASE_URL+"api/post/"+postid+"/app/"+ Constants.APP_ID+"/like",hashMap.toString(),likeResponse,this);
    }

    Response.Listener<JSONObject> likeResponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
                pd.dismiss();
            Model model = new Model(jsonObject.toString());
            lists.get(selectedPosition).postLike = !lists.get(selectedPosition).postLike;
            lists.get(selectedPosition).likeCounts = ""+model.getLikes();
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 200){
            if(lists.size()>0)
                lists.clear();
            page = 0;
            isPagination = false;
            makeGetPostRequest(0);
        }else if(resultCode == 300){
            onFirstLoad = true;
            if(lists.size()>0)
                lists.clear();
            page = 0;
            isPagination = false;
            makeGetPostRequest(0);
        }
    }


    public void onShareClick(String sharedMessage) {
        Resources resources = getResources();

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(sharedMessage));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "testing");
        emailIntent.setType("message/rfc822");

        PackageManager pm = getContext().getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        Intent openInChooser = Intent.createChooser(emailIntent, "sharing...");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if(packageName.contains("twitter")) {
                    intent.putExtra(Intent.EXTRA_TEXT, sharedMessage);
                } else if(packageName.contains("facebook")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
                    intent.putExtra(Intent.EXTRA_TEXT, sharedMessage);
                } else if(packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(sharedMessage));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "gmail");
                    intent.setType("message/rfc822");
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }
}
