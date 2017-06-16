package com.app.wedding.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.wedding.Activity.AlertsActivity;
import com.app.wedding.Activity.EventScheduleActivity;
import com.app.wedding.Activity.KeyContactsActivity;
import com.app.wedding.Activity.MenuActivity;
import com.app.wedding.Activity.WelcomeActivity;
import com.app.wedding.Constants.C;
import com.app.wedding.Constants.CursorWheelLayout;
import com.app.wedding.Constants.CurvedTextView;
import com.app.wedding.Constants.MenuItemData;
import com.app.wedding.Constants.SimpleTextAdapter;
import com.app.wedding.Constants.SimpleTextCursorWheelLayout;
import com.app.wedding.Fragment.GalerryFragment;
import com.app.wedding.Interface.CallBackDialog;
import com.app.wedding.Interface.OpenFragment;
import com.app.wedding.Interface.StartFragmentListner;
import com.app.wedding.Model.PicassoCache;
import com.app.wedding.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LENOVO on 5/10/2017.
 */

public class MenuDialog extends Dialog {
    private Context context;
    private SimpleTextCursorWheelLayout mTestCircleMenuLeft;
    private CallBackDialog callBackDialog;

    public MenuDialog(@NonNull Context context, CallBackDialog callBackDialog) {
        super(context, R.style.DialogSlideAnim);
        this.context = context;
        this.callBackDialog = callBackDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        setCanceledOnTouchOutside(true);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTestCircleMenuLeft = (SimpleTextCursorWheelLayout)findViewById(R.id.circlemenu);
        CircleImageView userimg = (CircleImageView)findViewById(R.id.userimg);
        PicassoCache.getPicassoInstance(context).load(C.WELCOME_IMAGE).into(userimg);

        CurvedTextView title = (CurvedTextView)findViewById(R.id.name);
        title.setText(C.WELCOME_TITLE);
      //  title.setTypeface(Typeface.createFromAsset(context.getAssets(), "font/Sensations and Qualities.ttf"));

        LinearLayout v =(LinearLayout) findViewById(R.id.view);
        v.addView(new GraphicsView(context));
        v.setRotation(140.0f);
        initRadialData();
    }
    private void initRadialData() {
        String[] res = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "OFF"};
        List<MenuItemData> menuItemDatas = new ArrayList<MenuItemData>();
        for (int i = 0; i < 1; i++) {
            menuItemDatas.add(new MenuItemData(res[i]));
        }
        SimpleTextAdapterMenu simpleTextAdapter = new SimpleTextAdapterMenu(context, menuItemDatas, Gravity.BOTTOM | Gravity.CENTER_VERTICAL,callBackDialog);
        mTestCircleMenuLeft.setAdapter(simpleTextAdapter);
    }
    public class SimpleTextAdapterMenu extends CursorWheelLayout.CycleWheelAdapter {
        private List<MenuItemData> mMenuItemDatas;
        private LayoutInflater mLayoutInflater;
        private Context mContext;
        public static final int INDEX_SPEC = 9;
        private int mGravity;
        private Context ctx;
        private CallBackDialog callBackDialog;

   /* public SimpleTextAdapter(Context context, List<MenuItemData> menuItemDatas) {
        this(context, menuItemDatas, Gravity.CENTER);
        this.ctx = context;
    }*/

        public SimpleTextAdapterMenu(Context context, List<MenuItemData> menuItemDatas, int gravity, CallBackDialog callBackDialog) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            mMenuItemDatas = menuItemDatas;
            mGravity = gravity;
            this.callBackDialog = callBackDialog;
        }

        @Override
        public int getCount() {
            return mMenuItemDatas == null ? 0 : mMenuItemDatas.size();
        }

        @Override
        public View getView(View parent, int position) {
            MenuItemData item = getItem(position);
            View root = mLayoutInflater.inflate(R.layout.wheel_menu_item, null, false);
            // TextView textView = (TextView) root.findViewById(R.id.wheel_menu_item_tv);
            // textView.setVisibility(View.VISIBLE);
            //textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            //textView.setText(item.mTitle);
            // if (textView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            //     ((FrameLayout.LayoutParams) textView.getLayoutParams()).gravity = mGravity;
            //  }
            //  if (position == INDEX_SPEC) {
            //      textView.setTextColor(ActivityCompat.getColor(mContext, R.color.red));
            //  }
            ImageView im = (ImageView)root.findViewById(R.id.wheel_menu_item_tv);
            ImageView im1 = (ImageView)root.findViewById(R.id.wheel_menu_item_tv3);
            ImageView im2 = (ImageView)root.findViewById(R.id.wheel_menu_item_tv2);
            ImageView im4 = (ImageView)root.findViewById(R.id.wheel_menu_item_tv4);
            ImageView im5 = (ImageView)root.findViewById(R.id.wheel_menu_item_tv1);
            ImageView event = (ImageView)root.findViewById(R.id.ev);

            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(mContext,"heart", Toast.LENGTH_LONG).show();
                    //((StartFragmentListner)ctx).startFragmentAt(new StoryFragment(callBackDialog));
                    callBackDialog.CallBackDialog("STORY");
                    dismiss();
                }
            }); im1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(mContext,"home", Toast.LENGTH_LONG).show();
                   // mContext.startActivity(new Intent(mContext, AlertsActivity.class));
                    dismiss();
                    //ctx.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            });im2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    //Toast.makeText(mContext,"sound", Toast.LENGTH_LONG).show();
                }
            });im4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // mContext.startActivity(new Intent(mContext, KeyContactsActivity.class));

                    callBackDialog.CallBackDialog("GALLERY");
                    dismiss();
                    // ctx.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

                    //  Toast.makeText(mContext,"star", Toast.LENGTH_LONG).show();
                }
            });
            im5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, KeyContactsActivity.class));
                    dismiss();

                }
            });event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, EventScheduleActivity.class));
                    dismiss();
                    //  Toast.makeText(mContext,"star", Toast.LENGTH_LONG).show();
                    //  ((StartFragmentListner)mContext).startFragmentAt(new GalerryFragment(mContext));

                }
            });

            return root;
        }

        @Override
        public MenuItemData getItem(int position) {
            return mMenuItemDatas.get(position);
        }
    }
    static public class GraphicsView extends View {
        private static final String QUOTE = C.WELCOME_TITLE;
        private Path circle;
        private Paint cPaint;
        private Paint tPaint;

        public GraphicsView(Context context) {
            super(context);

            int color = Color.argb(127, 255, 0, 255);

            circle = new Path();
            circle.addCircle(380, 500, 320, Path.Direction.CW);

            cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            cPaint.setStyle(Paint.Style.STROKE);
            cPaint.setColor(Color.LTGRAY);
            cPaint.setStrokeWidth(3);

            // For Background Image
//            setBackgroundResource(R.drawable.YOUR_IMAGE);

            tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            //TextColor you want to set
            tPaint.setColor(Color.WHITE);
            //TextSize you want to set
            tPaint.setTextSize(50);}


        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawTextOnPath(QUOTE, circle, 485, 20, tPaint);}
    }
}
