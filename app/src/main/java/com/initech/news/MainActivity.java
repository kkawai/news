package com.initech.news;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.initech.news.db.RssDb;
import com.initech.news.model.Rss;
import com.initech.news.util.MLog;
import com.initech.news.util.ThreadWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity {

   public static final String TAG = "MainActivity";

   private DrawerLayout mDrawerLayout;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      final ActionBar ab = getSupportActionBar();
      ab.setHomeAsUpIndicator(R.drawable.ic_menu);
      ab.setDisplayHomeAsUpEnabled(true);

      mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

      NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
      if (navigationView != null) {
         setupDrawerContent(navigationView);
      }

      ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
      if (viewPager != null) {
         setupViewPager(viewPager);
      }

//      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//      fab.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View view) {
//            Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                  .setAction("Action", null).show();
//         }
//      });

      TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
      tabLayout.setupWithViewPager(viewPager);

      try {
         fetchCategories();
      }catch(final Exception e) {
         MLog.e(TAG, "", e);
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.sample_actions, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
      }
      return super.onOptionsItemSelected(item);
   }

   private void setupViewPager(ViewPager viewPager) {
      Adapter adapter = new Adapter(getSupportFragmentManager());
//      adapter.addFragment(new CheeseListFragment(), "Category 1");
//      adapter.addFragment(new CheeseListFragment(), "Category 2");
//      adapter.addFragment(new CheeseListFragment(), "Category 3");
      viewPager.setAdapter(adapter);
   }

   private void setupDrawerContent(NavigationView navigationView) {
      navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
               @Override
               public boolean onNavigationItemSelected(MenuItem menuItem) {
                  menuItem.setChecked(true);
                  mDrawerLayout.closeDrawers();
                  return true;
               }
            });
   }

   private static class Adapter extends FragmentPagerAdapter {
      private final List<Fragment> mFragments = new ArrayList<>();
      private final List<String> mFragmentTitles = new ArrayList<>();

      public Adapter(FragmentManager fm) {
         super(fm);
      }

      public void addFragment(Fragment fragment, String title) {
         mFragments.add(fragment);
         mFragmentTitles.add(title);
      }

      @Override
      public Fragment getItem(int position) {
         return mFragments.get(position);
      }

      @Override
      public int getCount() {
         return mFragments.size();
      }

      @Override
      public CharSequence getPageTitle(int position) {
         return mFragmentTitles.get(position);
      }
   }

   /*
http://feeds.wired.com/wired/index
http://www.wired.com/category/business/feed/  business
http://www.wired.com/category/design/feed/    design
http://www.wired.com/category/underwire/feed/  underwire
http://www.wired.com/category/gear/feed/  tech
http://www.wired.com/category/reviews/feed/  reviews
http://www.wired.com/category/science/feed/   science
http://www.wired.com/category/threatlevel/feed/   security
http://feeds.cnevids.com/brand/wired.mrss   videos
http://www.wired.com/category/photo/feed/    photos
 */
   private void fetchCategories() throws Exception {

      ThreadWrapper.executeInWorkerThread(new Runnable() {
         @Override
         public void run() {
            RssDb.getInstance().deleteAll();

            final ArrayList<Rss> business = new RssReader().getRss("http://www.wired.com/category/business/feed/");
            RssDb.getInstance().insertRss(business);

            final ArrayList<Rss> design = new RssReader().getRss("http://www.wired.com/category/design/feed/");
            RssDb.getInstance().insertRss(design);

            final ArrayList<Rss> tech = new RssReader().getRss("http://www.wired.com/category/gear/feed/");
            RssDb.getInstance().insertRss(tech);

            final ArrayList<Rss> underwire = new RssReader().getRss("http://www.wired.com/category/underwire/feed/");
            RssDb.getInstance().insertRss(underwire);

            final ArrayList<Rss> reviews = new RssReader().getRss("http://www.wired.com/category/reviews/feed/");
            RssDb.getInstance().insertRss(reviews);

            final ArrayList<Rss> science = new RssReader().getRss("http://www.wired.com/category/science/feed/");
            RssDb.getInstance().insertRss(science);

            final ArrayList<Rss> security = new RssReader().getRss("http://www.wired.com/category/threatlevel/feed/");
            RssDb.getInstance().insertRss(security);

            final ArrayList<Rss> videos = new RssReader().getRss("http://feeds.cnevids.com/brand/wired.mrss");
            RssDb.getInstance().insertRss(videos);

            final ArrayList<Rss> photos = new RssReader().getRss("http://www.wired.com/category/photo/feed/");
            RssDb.getInstance().insertRss(photos);
         }
      });

   }
}
