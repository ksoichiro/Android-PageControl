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
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
    private int mColorCurrentDefault;
    private int mColorCurrentPressed;
    private int mColorNormalDefault;
    private int mColorNormalPressed;
    private boolean mIndicatorsClickable;

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

    public void setIndicatorsClickable(boolean indicatorsClickable) {
        mIndicatorsClickable = indicatorsClickable;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null) {
                child.setClickable(indicatorsClickable);
            }
        }
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
            setIndicatorBackground(b, i == mViewPager.getCurrentItem());
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
        // Set current clickable state to new children
        setIndicatorsClickable(mIndicatorsClickable);
        requestLayout();
    }

    private void setIndicatorBackground(Button b, boolean isCurrent) {
        ShapeDrawable drawableDefault = new ShapeDrawable();
        drawableDefault.setShape(new OvalShape());
        drawableDefault.getPaint().setColor(isCurrent ? mColorCurrentDefault : mColorNormalDefault);
        ShapeDrawable drawablePressed = new ShapeDrawable();
        drawablePressed.setShape(new OvalShape());
        drawablePressed.getPaint().setColor(isCurrent ? mColorCurrentPressed : mColorNormalPressed);

        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{android.R.attr.state_pressed}, drawablePressed);
        sld.addState(new int[]{-android.R.attr.state_pressed}, drawableDefault);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            b.setBackgroundDrawable(sld);
        } else {
            b.setBackground(sld);
        }
    }

    private void init(Context context) {
        // Horizontal layout by default
        if (getOrientation() != LinearLayout.VERTICAL) {
            setOrientation(LinearLayout.HORIZONTAL);
        }
        setGravity(Gravity.CENTER);
        mIndicatorsClickable = true;

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

        mColorCurrentDefault = a.getColor(R.styleable.AndroidPageControl_apc_colorCurrentDefault, getResources().getColor(R.color.apc_indicator_current_default));
        mColorCurrentPressed = a.getColor(R.styleable.AndroidPageControl_apc_colorCurrentPressed, getResources().getColor(R.color.apc_indicator_current_pressed));
        mColorNormalDefault = a.getColor(R.styleable.AndroidPageControl_apc_colorNormalDefault, getResources().getColor(R.color.apc_indicator_normal_default));
        mColorNormalPressed = a.getColor(R.styleable.AndroidPageControl_apc_colorNormalPressed, getResources().getColor(R.color.apc_indicator_normal_pressed));

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
