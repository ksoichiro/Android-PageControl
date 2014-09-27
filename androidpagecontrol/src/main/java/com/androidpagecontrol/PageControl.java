/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androidpagecontrol;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * View which has circle-formed page indicator.
 *
 * @author Soichiro Kashima
 */
public class PageControl extends LinearLayout implements View.OnClickListener {

    private static final float DEFAULT_RADIUS = 2.0f;
    private static final float DEFAULT_DISTANCE = 4.0f;

    private int mNumOfViews;
    private ViewPager mViewPager;
    private float mRadius;
    private float mDistance;

    public PageControl(Context context) {
        super(context);
        init(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public PageControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public PageControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setPosition(final int position) {
        if (0 <= position && position < mNumOfViews) {
            if (mViewPager != null) {
                mViewPager.setCurrentItem(position);
            }
            invalidate();
        }
    }

    public void setViewPager(final ViewPager viewPager) {
        mViewPager = viewPager;
        updateNumOfViews();
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateNumOfViews();
                setPosition(position);
            }
        });
    }

    private void updateNumOfViews() {
        if (mViewPager.getAdapter() == null) {
            mNumOfViews = 0;
        } else {
            mNumOfViews = mViewPager.getAdapter().getCount();
        }
        removeAllViews();
        for (int i = 0; i < mNumOfViews; i++) {
            Button b = new Button(getContext());
            if (i == mViewPager.getCurrentItem()) {
                b.setBackgroundResource(R.drawable.apc_indicator_current);
            } else {
                b.setBackgroundResource(R.drawable.apc_indicator_normal);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (mRadius * 2), (int) (mRadius * 2));
            if (getOrientation() == LinearLayout.HORIZONTAL) {
                lp.leftMargin = (int) (mDistance / 2);
                lp.rightMargin = (int) (mDistance / 2);
            } else {
                lp.topMargin = (int) (mDistance / 2);
                lp.bottomMargin = (int) (mDistance / 2);
            }
            b.setTag(i);
            b.setOnClickListener(this);
            addView(b, lp);
        }
        requestLayout();
    }

    private void init(Context context) {
        // Horizontal layout by default
        if (getOrientation() != LinearLayout.VERTICAL) {
            setOrientation(LinearLayout.HORIZONTAL);
        }
        setGravity(Gravity.CENTER);

        TypedArray a = context.getTheme().obtainStyledAttributes(null,
                R.styleable.AndroidPageControl,
                R.attr.apcStyles, 0);
        float radiusDefault = DEFAULT_RADIUS * getResources().getDisplayMetrics().density;
        float radius = a.getDimension(R.styleable.AndroidPageControl_apc_radius, radiusDefault);
        if (radius <= 0) {
            radius = radiusDefault;
        }
        mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, getResources().getDisplayMetrics());

        float distanceDefault = DEFAULT_DISTANCE * getResources().getDisplayMetrics().density;
        float distance = a.getDimension(R.styleable.AndroidPageControl_apc_distance, distanceDefault);
        if (distance <= 0) {
            distance = distanceDefault;
        }
        mDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, distance, getResources().getDisplayMetrics());
        a.recycle();
    }

    @Override
    public void onClick(final View view) {
        if (view == null) {
            return;
        }
        if (!(view instanceof Button)) {
            return;
        }
        Button b = (Button) view;
        try {
            int position = (Integer) b.getTag();
            setPosition(position);
        } catch (ClassCastException ignore) {
            // For casting tag object to Integer
        }
    }
}
