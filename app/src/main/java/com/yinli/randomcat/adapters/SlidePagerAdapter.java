package com.yinli.randomcat.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yinli.randomcat.PagerFragment;
import com.yinli.randomcat.data.CatImage;

import java.util.List;

/**
 * RandomCat
 * Created by Yin Li on 15/05/15.
 * Copyright (c) 2015 Yin Li. All rights reserved.
 */
public class SlidePagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private List<CatImage> mItems;
    public SlidePagerAdapter(Context context, FragmentManager fm, List<CatImage> items) {
        super(fm);
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public Fragment getItem(int position) {
        return PagerFragment.create(mItems.get(position).getImgUrl());
    }

    @Override
    public int getCount() {
        return this.mItems.size();
    }
}
