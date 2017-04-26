package com.app.wedding.Model;

import android.content.Context;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by amsyt005 on 8/11/16.
 */
public class PicassoCache {
    private static PicassoCache ourInstance = new PicassoCache();
    private static Picasso picassoInstance = null;

    private PicassoCache (Context context) {
        Downloader downloader   = new OkHttpDownloader(context, Integer.MAX_VALUE);
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(downloader);
        picassoInstance = builder.build();
    }

    /**
     * Get Singleton Picasso Instance
     *
     * @param context application Context
     * @return Picasso instance
     */
    public static Picasso getPicassoInstance (Context context) {

        if (picassoInstance == null) {

            new PicassoCache(context);
            return picassoInstance;
        }

        return picassoInstance;
    }


    public static PicassoCache getInstance() {
        return ourInstance;
    }

    private PicassoCache() {
    }
}
