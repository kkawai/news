package com.initech.news;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.initech.news.util.MLog;

/**
 * Not intended to be used wide-spread throughout the app.
 *
 * @author kkawai
 */

public final class WebViewActivity extends AppCompatActivity {

   private static final String TAG = "WebViewActivity";

   private String mUrl = "";
   private WebView mWebView;

   @SuppressLint("SetJavaScriptEnabled")
   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_webview);

      final Intent intent = getIntent();
      final String category = intent.getStringExtra("cat");
      final String image = intent.getStringExtra("image");

      final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      final CollapsingToolbarLayout collapsingToolbar =
            (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
      collapsingToolbar.setTitle(category);

      final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
      Glide.with(this).load(image).centerCrop().into(imageView);

      mWebView = (WebView) findViewById(R.id.webview);
      final WebSettings webSettings = mWebView.getSettings();
      webSettings.setJavaScriptEnabled(true);
      webSettings.setBuiltInZoomControls(true);
      webSettings.setDisplayZoomControls(false);
      webSettings.setDomStorageEnabled(true);

      mWebView.setWebViewClient(new WebViewClient() {
         @Override
         public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            MLog.i(TAG, "webview url: " + url);
            //            mProgressDialog.show();
         }

         @Override
         public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //            mProgressDialog.dismiss();
         }
      });

      mWebView.setOnKeyListener(new OnKeyListener() {

         @Override
         public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
               if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                  //                  if (mProgressDialog.isShowing()) {
                  //                     mProgressDialog.dismiss();
                  //                  }
                  mWebView.stopLoading();
                  mWebView.goBack();
                  return true;
               } else {
                  WebViewActivity.this.onBackPressed();
               }
            }
            return false;
         }
      });

      MLog.i(TAG, "onCreate()");

      onNewIntent(getIntent());
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.sample_actions, menu);
      return true;
   }

   @Override
   protected void onRestart() {
      super.onRestart();
      MLog.i(TAG, "onRestart()");
   }

   @Override
   protected void onResume() {
      super.onResume();
      MLog.i(TAG, "onResume()");
   }

   @Override
   protected void onNewIntent(final Intent intent) {
      super.onNewIntent(intent);
      final String url = intent.getDataString();
      MLog.i(TAG, "onNewIntent(): " + url + " previous: " + mUrl);
      if (url != null) {

         if (mUrl.equals(url)) {
            return; //url already loaded
         }
         mUrl = url;

         mWebView.loadUrl(url);
         MLog.i(TAG, "webview load: " + url);
      } else {
         finish();
      }
   }

   @Override
   protected void onStop() {
      mWebView.stopLoading();
      //      mProgressDialog.dismiss();
      super.onStop();
   }
}
