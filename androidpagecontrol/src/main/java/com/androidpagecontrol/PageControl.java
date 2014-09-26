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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * View which has circle-formed page indicator.
 *
 * @author Soichiro Kashima
 */
public class PageControl extends View {

    private static final float DEFAULT_RADIUS = 5.0f;
    private static final float DEFAULT_DISTANCE = 30.0f;

    private int mNumOfViews;
    private int mPosition;
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
        if (position < mNumOfViews) {
            mPosition = position;
            if (mViewPager != null) {
                mViewPager.setCurrentItem(mPosition);
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
            public void onPageScrolled(int position, float positionOffest, int positionOffestPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateNumOfViews();
                setPosition(position);
            }
        });
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

        for (int i = 0; i < mNumOfViews; i++) {
            float cx = (getWidth() - (mNumOfViews - 1) * mDistance) / 2 + i * mDistance;
            float cy = getHeight() / 2.0f;
            if (mPosition == i) {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
            } else {
                paint.setStyle(Paint.Style.STROKE);
            }
            canvas.drawCircle(cx, cy, mRadius, paint);
        }
    }

    private void updateNumOfViews() {
        if (mViewPager.getAdapter() == null) {
            mNumOfViews = 0;
        } else {
            mNumOfViews = mViewPager.getAdapter().getCount();
        }
    }

    private void init(Context context) {
        TypedArray a = context.getTheme().obtainStyledAttributes(null,
                R.styleable.AndroidPageControl,
                R.attr.apcStyles, 0);
        float radiusDp = a.getDimension(R.styleable.AndroidPageControl_apc_radius, DEFAULT_RADIUS);
        mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusDp, getResources().getDisplayMetrics());
        if (mRadius <= 0) {
            mRadius = DEFAULT_RADIUS;
        }
        float distanceDp = a.getDimension(R.styleable.AndroidPageControl_apc_distance, DEFAULT_DISTANCE);
        mDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, distanceDp, getResources().getDisplayMetrics());
        if (mDistance <= 0) {
            mDistance = DEFAULT_DISTANCE;
        }
        a.recycle();
    }

}
