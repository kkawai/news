package com.initech.news.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

/**
 * Created by kkawai on 6/3/15.
 */
public final class VolleyHelper {

   private static RequestQueue newRequestQueue(final Context context, HttpStack stack, final int diskCacheMB) {
      final File cacheDir = new File(context.getCacheDir(), "volley");
      String userAgent = "volley/0";

      try {
         final String network = context.getPackageName();
         final PackageInfo queue = context.getPackageManager().getPackageInfo(network, 0);
         userAgent = network + "/" + queue.versionCode;
      } catch (PackageManager.NameNotFoundException e) {
      }

      if(stack == null) {
         if(Build.VERSION.SDK_INT >= 9) {
            stack = new HurlStack();
         } else {
            stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
         }
      }

      final BasicNetwork network = new BasicNetwork((HttpStack)stack);
      final RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir,diskCacheMB*1000000), network);
      queue.start();
      return queue;
   }

   public static RequestQueue newRequestQueue(final Context context,final int diskCacheMB) {
      return newRequestQueue(context, (HttpStack)null, diskCacheMB);
   }
}
