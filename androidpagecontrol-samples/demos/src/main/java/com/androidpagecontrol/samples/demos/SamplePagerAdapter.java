package com.androidpagecontrol.samples.demos;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SamplePagerAdapter extends PagerAdapter {

    /**
     * Number of views(pages).
     */
    private static final int NUM_OF_VIEWS = 5;

    private Context mContext;

    public SamplePagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return NUM_OF_VIEWS;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextView view = new TextView(mContext);
        view.setText(String.valueOf(position + 1));
        view.setGravity(Gravity.CENTER);
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 72);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(view, lp);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((TextView) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}