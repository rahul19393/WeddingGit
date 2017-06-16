package com.app.wedding.Constants;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.app.wedding.R;
import java.util.List;

/**
 * Created by LENOVO on 5/2/2017.
 */

public abstract class RadialAdapter extends ArrayAdapter<RadialItems> {
    private List<RadialItems> mItems;

    public RadialAdapter(Context context, List<RadialItems> mItems) {
        super(context, 0,mItems);
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_radial, parent, false);
        }

        RadialItems radialItems = mItems.get(position);
        ImageView im = (ImageView)convertView.findViewById(R.id.img);
        im.setImageResource(radialItems.getResource());
        return convertView;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    /* public RadialAdapter(List<RadialItems> items) {
        mItems = items;
    }

    public RadialItems getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }*/


}
