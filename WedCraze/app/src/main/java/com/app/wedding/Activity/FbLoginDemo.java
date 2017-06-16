package com.app.wedding.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.wedding.Constants.RadialItems;
import com.app.wedding.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoginStatusCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FbLoginDemo extends AppCompatActivity {

    private boolean mIsAdapterDirty = true;
    private List<RadialItems> itemsRadial = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_login_demo);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for (int i = 0; i < 10; i++) {
            listAdapter.add(String.format("Item %02d", i));
        }
        itemsRadial.add(new RadialItems(R.drawable.events,""));
        itemsRadial.add(new RadialItems(R.drawable.events,""));
        itemsRadial.add(new RadialItems(R.drawable.events,""));
        itemsRadial.add(new RadialItems(R.drawable.events,""));
        itemsRadial.add(new RadialItems(R.drawable.events,""));

        Display display = getWindowManager().getDefaultDisplay();

    }

}
