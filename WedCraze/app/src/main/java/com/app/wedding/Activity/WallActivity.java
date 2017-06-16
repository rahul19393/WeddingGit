package com.app.wedding.Activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.wedding.Constants.BuilderManager;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.Constants;
import com.app.wedding.Constants.MenuItemData;
import com.app.wedding.Constants.RadialItems;
import com.app.wedding.Constants.SimpleTextAdapter;
import com.app.wedding.Constants.SimpleTextCursorWheelLayout;
import com.app.wedding.Dialog.MenuDialog;
import com.app.wedding.Dialog.SharePictureDialog;
import com.app.wedding.Dialog.ShowZoomImageDialog;
import com.app.wedding.Fragment.HomeFragment;
import com.app.wedding.Interface.CallBackDialog;
import com.app.wedding.Model.Model;
import com.app.wedding.Model.NamePair;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;
import com.app.wedding.network.Net;
import com.app.wedding.network.VolleyErrors;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WallActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<JSONArray>{
    private RecyclerView weddingList;
    private ArrayList<Items> lists = new ArrayList<>();
    private ProgressDialog pd;
    private int selectedPosition = 0;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private WedingAdapter adapter;
    private boolean onFirstLoad = false;
    private SwipeRefreshLayout swipeContainer;
    private boolean isReferesh,isPagination;
    private LinearLayoutManager mLayoutManager;
    private int page = 0;
    private boolean mIsAdapterDirty = true;
    private List<RadialItems> itemsRadial = new ArrayList<>();
    private BoomMenuButton bmb,boommenu;
    private ArrayList<Pair> piecesAndButtons = new ArrayList<>();
    private SimpleTextCursorWheelLayout mTestCircleMenuLeft;
    private CallBackDialog callBackDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        findViewById(R.id.gotoback).setVisibility(View.GONE);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(C.USER_NAME);
        weddingList = (RecyclerView)findViewById(R.id.weddinglist);
        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        boommenu = (BoomMenuButton) findViewById(R.id.boommnus);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_1 );
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_1 );
        bmb.addBuilder(BuilderManager.getHamButtonBuilder());
        mTestCircleMenuLeft = (SimpleTextCursorWheelLayout)findViewById(R.id.test_circle_menu_left);
        //showing in grid
        assert boommenu != null;
        boommenu.setButtonEnum(ButtonEnum.SimpleCircle);
        boommenu.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_1);
        boommenu.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_1);
        for (int i = 0; i < boommenu.getPiecePlaceEnum().pieceNumber(); i++)
            boommenu.addBuilder(BuilderManager.getSimpleCircleButtonBuilder());
        boommenu.setBoomEnum(BoomEnum.values()[0]);

        getData();
        bmb.setPiecePlaceEnum((PiecePlaceEnum) piecesAndButtons.get(12).first);
        bmb.setButtonPlaceEnum((ButtonPlaceEnum) piecesAndButtons.get(12).second);
        bmb.clearBuilders();
        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++)
            bmb.addBuilder(BuilderManager.getHamButtonBuilder());


        findViewById(R.id.events).setOnClickListener(this);
        findViewById(R.id.keycontact).setOnClickListener(this);
        findViewById(R.id.createpost).setOnClickListener(this);
       findViewById(R.id.openmenu).setOnClickListener(this);
        itemsRadial.add(new RadialItems(R.drawable.keypeople,""));
        itemsRadial.add(new RadialItems(R.drawable.events,""));
        itemsRadial.add(new RadialItems(R.drawable.alerts,""));
        // itemsRadial.add(new RadialItems(R.drawable.home,""));
        // itemsRadial.add(new RadialItems(R.drawable.story,""));

        Display display = getWindowManager().getDefaultDisplay();
        // mCircularListView.setRadius(Math.min(400, display.getWidth() / 3));

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        swipeContainer.stopNestedScroll();
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
        initRadialData();
        makeGetPostRequest(0);
    }

    private List<String> getData() {
        List<String> data = new ArrayList<>();
        for (int p = 0; p < PiecePlaceEnum.values().length - 1; p++) {
            for (int b = 0; b < ButtonPlaceEnum.values().length - 1; b++) {
                PiecePlaceEnum piecePlaceEnum = PiecePlaceEnum.getEnum(p);
                ButtonPlaceEnum buttonPlaceEnum = ButtonPlaceEnum.getEnum(b);
                if (piecePlaceEnum.pieceNumber() == buttonPlaceEnum.buttonNumber()
                        || buttonPlaceEnum == ButtonPlaceEnum.Horizontal
                        || buttonPlaceEnum == ButtonPlaceEnum.Vertical) {
                    piecesAndButtons.add(new Pair<>(piecePlaceEnum, buttonPlaceEnum));
                    data.add(piecePlaceEnum + " " + buttonPlaceEnum);
                    if (piecePlaceEnum.getValue() < PiecePlaceEnum.HAM_1.getValue()
                            || piecePlaceEnum == PiecePlaceEnum.Share
                            || buttonPlaceEnum.getValue() < ButtonPlaceEnum.HAM_1.getValue()) {
                        piecesAndButtons.remove(piecesAndButtons.size() - 1);
                        data.remove(data.size() - 1);
                    }
                }
            }
        }
        return data;
    }



    private void initRadialData() {
        String[] res = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "OFF"};
        List<MenuItemData> menuItemDatas = new ArrayList<MenuItemData>();
        for (int i = 0; i < 1; i++) {
            menuItemDatas.add(new MenuItemData(res[i]));
        }
        SimpleTextAdapter simpleTextAdapter = new SimpleTextAdapter(WallActivity.this, menuItemDatas, Gravity.BOTTOM | Gravity.CENTER_VERTICAL,callBackDialog);
        mTestCircleMenuLeft.setAdapter(simpleTextAdapter);
    }

    private void setWeddingAdapter(){
        adapter = new WedingAdapter(lists);
        weddingList.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(WallActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        weddingList.setLayoutManager(mLayoutManager);
        weddingList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.events:
                startActivity(new Intent(WallActivity.this, EventScheduleActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case  R.id.keycontact:
                startActivity(new Intent(WallActivity.this, KeyContactsActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case R.id.createpost:
                startActivityForResult(new Intent(WallActivity.this, CreatePostActivity.class),100);
                break;
            case R.id.openmenu:
                new MenuDialog(WallActivity.this,callBackDialog).show();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
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
        Toast.makeText(WallActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
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
                PicassoCache.getPicassoInstance(WallActivity.this).load(items.userImage).placeholder(R.drawable.userplaceholder).into(holder.userImage);
            else
                holder.userImage.setImageResource(R.drawable.placeholder);

            if(!TextUtils.isEmpty(items.functionImage)) {
                holder.eventImageLayout.setVisibility(View.VISIBLE);
               /* Glide.with(getActivity())
                        .load(items.functionImage)
                        //.centerCrop()
                      //  .placeholder(R.drawable.placeholder)
                       // .crossFade()
                       // .override(720,300)

                        .into(holder.imageEvent); */
                PicassoCache.getPicassoInstance(WallActivity.this).load(items.functionImage).placeholder(R.drawable.placeholder).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // holder.imageEvent.setwbitmap.getWidth();
                        holder.imageEvent.getLayoutParams().width = bitmap.getWidth();
                        holder.imageEvent.getLayoutParams().height = bitmap.getHeight();
                        Log.e("widthevent",""+bitmap.getWidth());
                        holder.imageEvent.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        BitmapDrawable bd=(BitmapDrawable) WallActivity.this.getDrawable(R.drawable.placeholder);
                        holder.imageEvent.getLayoutParams().width = bd.getBitmap().getWidth();
                        holder.imageEvent.getLayoutParams().height = bd.getBitmap().getHeight();
                        holder.imageEvent.setImageDrawable(placeHolderDrawable);
                    }
                });
            }
            else
                holder.eventImageLayout.setVisibility(View.GONE);
            holder.userName.setText(items.userName);

/*for comment section*/
            Model comment = items.commentModel;
            if(comment != null){
                Model info = new Model(comment.getCreatedBy());
                holder.commentSection.setVisibility(View.VISIBLE);
                holder.commentDate.setText(C.parseDateToddMMyyyy(comment.getCreatedOn()));
                holder.userCommentName.setText(info.getName());
                holder.userComment.setText(comment.getMessage());
                if(!TextUtils.isEmpty(info.getProfileImage()))
                    PicassoCache.getPicassoInstance(WallActivity.this).load(info.getProfileImage()).into(holder.userCommentImage);
                else
                    holder.userCommentImage.setImageResource(R.drawable.userplaceholder);
            }else
                holder.commentSection.setVisibility(View.GONE);

            holder.postDate.setText(C.parseDateToddMMyyyy(items.date));
            holder.commentsCount.setText(items.commentCounts+" Comments");
            holder.likeTextView.setText(items.likeCounts+" people like this");


            holder.likeIcon.setColorFilter(items.postLike ? ContextCompat.getColor(WallActivity.this,R.color.lightPinkColor) : ContextCompat.getColor(WallActivity.this,R.color.regularTextColor));

            holder.comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(WallActivity.this, CommentsActivity.class).putExtra(Constants.POSTID,items.postId),200);
                    WallActivity.this.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            });
            holder.commentsCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(WallActivity.this,CommentsActivity.class).putExtra(Constants.POSTID,items.postId),200);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            });
            holder.likeClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(WallActivity.this, ShowLikesActivity.class).putExtra(Constants.POSTID,items.postId));
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
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
                    if(!TextUtils.isEmpty(items.functionImage))
                        new SharePictureDialog(WallActivity.this,items.functionImage).show();
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
                    new ShowZoomImageDialog(WallActivity.this,items.functionImage).show();
                }
            });

            holder.allComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(WallActivity.this,CommentsActivity.class).putExtra(Constants.POSTID,items.postId));
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
            pd = C.getProgressDialog(WallActivity.this);
        Net.makeJSONArrayRequest(C.APP_BASE_URL+"api/post/app/"+Constants.APP_ID+"/"+""+pageCount+"/10",this,this);
    }
    private void likeApiRequest(String postid){
        pd = C.getProgressDialog(WallActivity.this);
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

        PackageManager pm = getPackageManager();
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
