package com.initech.news;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.initech.news.util.MLog;
import com.initech.news.util.ThreadWrapper;
import com.initech.news.util.VolleyHelper;

/**
 * Created by kevin on 1/10/2016.
 */
public class NewsApplication extends Application {

   private static final String TAG = "NewsApp";

   private static RequestQueue sRequestQueue;
   private static ImageLoader sImageLoader;
   private static NewsApplication sInstance;
   public static String sVersionName = "";
   public static int sVersionCode;
   public static String sFullVersionName = "";

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
   }

   @Override
   public void onCreate() {
      super.onCreate();
      sInstance = this;
      ThreadWrapper.init();
      MLog.setEnabled(getPackageName(), getResources().getBoolean(R.bool.is_logging_enabled));
      sRequestQueue = VolleyHelper.newRequestQueue(this, getResources().getInteger(R.integer.volley_disk_cache_size_mb));
      try {
         final PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
         sVersionName = info.versionName;
         sVersionCode = info.versionCode;
         sFullVersionName = sVersionName + '_' + sVersionCode;
      } catch (final Exception ex) {
      }
   }

   public static NewsApplication getInstance() {
      return sInstance;
   }

   @Override
   public void onTrimMemory(final int level) {
      super.onTrimMemory(level);
      //Glide.with(this).onTrimMemory(level);
      Glide.get(this).clearMemory();
   }

   @Override
   public void onLowMemory() {
      super.onLowMemory();
      Glide.get(this).clearMemory();
   }
}
