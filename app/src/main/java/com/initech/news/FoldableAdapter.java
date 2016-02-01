package com.initech.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexvasilkov.android.commons.adapters.ItemsAdapter;
import com.alexvasilkov.android.commons.utils.Views;
import com.bumptech.glide.Glide;
import com.initech.news.model.Rss;

public class FoldableAdapter extends ItemsAdapter<Rss> implements View.OnClickListener {

    public FoldableAdapter(Context context) {
        super(context);
    }

    @Override
    protected View createView(Rss item, int pos, ViewGroup parent, LayoutInflater inflater) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.foldable_rss_item, parent, false);
        ViewHolder vh = new ViewHolder();
        vh.image = Views.find(view, R.id.list_item_image);
        vh.image.setOnClickListener(this);
        vh.title = Views.find(view, R.id.list_item_title);
        view.setTag(vh);

        return view;
    }

    @Override
    protected void bindView(Rss item, int pos, View convertView) {
        ViewHolder vh = (ViewHolder) convertView.getTag();

        vh.image.setTag(R.id.list_item_image, item);
        Glide.with(convertView.getContext())
                .load(item.getImageUrl())
                .dontTransform()
                .dontAnimate()
                .into(vh.image);
        vh.title.setText(item.getTitle());
    }

    @Override
    public void onClick(final View view) {
        final Rss item = (Rss) view.getTag(R.id.list_item_image);
        final Intent intent = new Intent(view.getContext(),WebViewActivity.class);
        intent.setData(Uri.parse(item.getLink()));
        intent.putExtra(Rss.KEY_CATEGORY, item.getCategory());
        intent.putExtra(Rss.KEY_ORIGINAL_CATEGORY, item.getOriginalCategory());
        intent.putExtra(Rss.KEY_IMAGE, item.getImageUrl());
        view.getContext().startActivity(intent);
    }

    private static class ViewHolder {
        ImageView image;
        TextView title;
    }

}
