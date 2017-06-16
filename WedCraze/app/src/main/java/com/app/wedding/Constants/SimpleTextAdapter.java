package com.app.wedding.Constants;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.wedding.Activity.AlertsActivity;
import com.app.wedding.Activity.EventScheduleActivity;
import com.app.wedding.Activity.KeyContactsActivity;
import com.app.wedding.Fragment.GalerryFragment;
import com.app.wedding.Fragment.StoryFragment;
import com.app.wedding.Interface.CallBackDialog;
import com.app.wedding.Interface.StartFragmentListner;
import com.app.wedding.R;

import java.util.List;

/**
 * Created by chensuilun on 16/4/24.
 */
public class SimpleTextAdapter extends CursorWheelLayout.CycleWheelAdapter {
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

    public SimpleTextAdapter(Context context, List<MenuItemData> menuItemDatas, int gravity, CallBackDialog callBackDialog) {
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
            }
        }); im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(mContext,"home", Toast.LENGTH_LONG).show();
                mContext.startActivity(new Intent(mContext, AlertsActivity.class));
                //ctx.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,"sound", Toast.LENGTH_LONG).show();
            }
        });im4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(ctx, KeyContactsActivity.class));

                // ctx.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

                //  Toast.makeText(mContext,"star", Toast.LENGTH_LONG).show();
            }
        });
        im5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(mContext,"star", Toast.LENGTH_LONG).show();
              //  ((StartFragmentListner)mContext).startFragmentAt(new GalerryFragment(mContext));

            }
        });event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, EventScheduleActivity.class));

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