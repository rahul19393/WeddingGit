package com.app.wedding.Activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.app.wedding.Constants.MenuItemData;
import com.app.wedding.Constants.SimpleTextAdapter;
import com.app.wedding.Constants.SimpleTextCursorWheelLayout;
import com.app.wedding.R;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends Activity {
    private SimpleTextCursorWheelLayout mTestCircleMenuLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        params.x = 0;
        params.height = (int) (height-(height/3.1));
        params.width = width-(width/9);
        params.y = 0;
        this.getWindow().setAttributes(params);
        setFinishOnTouchOutside(true);

        mTestCircleMenuLeft = (SimpleTextCursorWheelLayout)findViewById(R.id.circlemenu);
        initRadialData();
    }

    private void initRadialData() {
        String[] res = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "OFF"};
        List<MenuItemData> menuItemDatas = new ArrayList<MenuItemData>();
        for (int i = 0; i < 1; i++) {
            menuItemDatas.add(new MenuItemData(res[i]));
        }
       // SimpleTextAdapter simpleTextAdapter = new SimpleTextAdapter(MenuActivity.this, menuItemDatas, Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
        //mTestCircleMenuLeft.setAdapter(simpleTextAdapter);
    }

}
