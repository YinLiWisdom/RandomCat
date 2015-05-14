package com.yinli.randomcat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yinli.randomcat.R;
import com.yinli.randomcat.data.CatImage;

import java.util.List;

/**
 * RandomCat
 * Created by Yin Li on 13/05/15.
 * Copyright (c) 2015 Yin Li. All rights reserved.
 */
public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<CatImage> mItems;

    public GridAdapter(Context context, List<CatImage> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return this.mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ItemHolder holder = null;
        final CatImage item = mItems.get(position);

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_grid_image, parent, false);

            holder = new ItemHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.imageView);

            view.setTag(holder);
        } else {
            holder = (ItemHolder) view.getTag();
        }

        if (item != null) {
            final ImageView image = holder.imageView;
            image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int width = image.getWidth();
                    int height = image.getHeight();

                    Picasso.with(mContext).load(item.getImgUrl()).placeholder(R.drawable.default_thumb).error(R.drawable.default_thumb).
                            resize(width, height).into(image);
                    return true;
                }
            });
        }
        return view;
    }

    private static class ItemHolder {
        ImageView imageView;
    }
}
