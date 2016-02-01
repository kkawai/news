package com.initech.news;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.alexvasilkov.android.commons.utils.Views;
import com.alexvasilkov.foldablelayout.FoldableListLayout;
import com.initech.news.db.RssDb;
import com.initech.news.model.Rss;

import java.util.ArrayList;

/**
 * Created by kevin on 1/31/2016.
 */
public class FoldableActivity extends AppCompatActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_foldable_list);

      final FoldableListLayout foldableListLayout = Views.find(this, R.id.foldable_list);
      final FoldableAdapter adapter = new FoldableAdapter(this);
      final String originalCategory = getIntent().getStringExtra(Rss.KEY_ORIGINAL_CATEGORY);
      final String category = getIntent().getStringExtra(Rss.KEY_CATEGORY);
      final String title = getIntent().getStringExtra(Rss.KEY_TITLE);
      final ArrayList<Rss> rss = RssDb.getInstance().getRss(originalCategory);
      int i =0;
      for (;i<rss.size();i++) {
         final Rss r = rss.get(i);
         if (!r.getTitle().equals(title))
            continue;
         break;
      }
      final Rss removed = rss.remove(i);
      if (removed != null)
         rss.add(0,removed);  //move this article to top of foldable list
      adapter.setItemsList(rss);
      foldableListLayout.setAdapter(adapter);

      final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(category);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
      }
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:
            onBackPressed();
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }
}
