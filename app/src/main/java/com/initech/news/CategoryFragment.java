package com.initech.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.initech.news.db.RssDb;
import com.initech.news.model.Rss;
import com.initech.news.util.MLog;
import com.initech.news.util.ThreadWrapper;

import java.util.ArrayList;

/**
 * Created by kevin on 1/26/2016.
 */
public class CategoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

   private String mOriginalCategory;
   private RecyclerView mRecyclerView;
   private ArrayList<Rss> mRss;
   private SwipeRefreshLayout mSwipeRefreshLayout;
   private Adapter mAdapter;

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      final View view = inflater.inflate(R.layout.fragment_category,container,false);
      mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
      mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
      mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
      return view;
   }

   @Override
   public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      mOriginalCategory = getArguments().getString(Rss.KEY_ORIGINAL_CATEGORY);
      mRss = RssDb.getInstance().getRss(mOriginalCategory);
      MLog.i("CategoryFrag", "category: " + mOriginalCategory + " rss size: " + mRss.size());
      mAdapter = new Adapter();
      mRecyclerView.setAdapter(mAdapter);
      mSwipeRefreshLayout.setOnRefreshListener(this);
      //mSwipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.black, R.color.pink);
   }

   private final class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
      @Override
      public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rss, parent, false);
         return new ViewHolder(view);
      }

      @Override
      public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
         final Rss rss = mRss.get(position);
         final ViewHolder viewHolder = (ViewHolder)holder;
         viewHolder.title.setText(rss.getTitle());
         viewHolder.descr.setText(Html.fromHtml(rss.getDescr()));
         Glide.with(CategoryFragment.this).load(rss.getImageUrl()).into(viewHolder.imageView);
         MLog.i("CategoryFrag", rss.getImageUrl());
         setOnClickListener(rss,viewHolder);
      }

      @Override
      public int getItemCount() {
         return mRss.size();
      }

   }

   private void setOnClickListener(final Rss rss, final ViewHolder viewHolder) {
      final View.OnClickListener clickListener = new View.OnClickListener() {
         @Override
         public void onClick(final View v) {
            final Intent intent = new Intent(getActivity(),FoldableActivity.class);
            intent.setData(Uri.parse(rss.getLink()));
            intent.putExtra(Rss.KEY_CATEGORY, rss.getCategory());
            intent.putExtra(Rss.KEY_ORIGINAL_CATEGORY, rss.getOriginalCategory());
            intent.putExtra(Rss.KEY_TITLE, rss.getTitle());
            startActivity(intent);
         }
      };
      viewHolder.descr.setOnClickListener(clickListener);
      viewHolder.title.setOnClickListener(clickListener);
      viewHolder.imageView.setOnClickListener(clickListener);
   }

   private static final class ViewHolder extends RecyclerView.ViewHolder {
      private TextView title, descr;
      private ImageView imageView;
      public ViewHolder(final View itemView) {
         super(itemView);
         title = (TextView)itemView.findViewById(R.id.title);
         descr = (TextView)itemView.findViewById(R.id.descr);
         imageView = (ImageView)itemView.findViewById(R.id.image);
      }
   }

   @Override
   public void onRefresh() {

      showProgress();
      ThreadWrapper.executeInWorkerThread(new Runnable() {
         @Override
         public void run() {
            final ArrayList<Rss> rss = new RssReader().getRss(NewsApplication.ORIGINAL_CATEGORY_MAP.get(mOriginalCategory));
            if (rss.size() == 0) return;
            RssDb.getInstance().deleteRss(mOriginalCategory);
            RssDb.getInstance().insertRss(mOriginalCategory, rss);
            mRss.clear();
            mRss.addAll(rss);
            ThreadWrapper.executeInUiThread(new Runnable() {
               @Override
               public void run() {
                  if (getActivity() == null) return;
                  hideProgress();
                  mAdapter.notifyDataSetChanged();
               }
            });
         }
      });
   }

   private void showProgress() {
      mSwipeRefreshLayout.post(new Runnable() {
         @Override
         public void run() {
            mSwipeRefreshLayout.setRefreshing(true);
         }
      });
   }

   public void hideProgress() {
      mSwipeRefreshLayout.post(new Runnable() {
         @Override
         public void run() {
            mSwipeRefreshLayout.setRefreshing(false);
         }
      });
   }
}
