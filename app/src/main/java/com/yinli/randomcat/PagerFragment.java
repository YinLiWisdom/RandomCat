package com.yinli.randomcat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class PagerFragment extends Fragment {

    private static final String IMAGE_URL = "image_url";
    private String mImgUrl;

    public static PagerFragment create(String url) {
        PagerFragment frag = new PagerFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, url);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImgUrl = getArguments().getString(IMAGE_URL);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
        final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                int width = imageView.getWidth();
                int height = imageView.getHeight();

                Picasso.with(getActivity()).load(mImgUrl).resize(width, height).into(imageView);
                return true;
            }
        });

        return rootView;
    }


}
