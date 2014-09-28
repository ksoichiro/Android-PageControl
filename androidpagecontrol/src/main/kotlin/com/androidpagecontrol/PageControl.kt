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

package com.androidpagecontrol

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.content.res.Resources
import android.content.res.TypedArray

/**
 * View which has circle-formed page indicator.
 *
 * @author Soichiro Kashima
 */
open class PageControl(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs, 0), View.OnClickListener {
    private var mNumOfViews: Int
    private var mViewPager: ViewPager?
    private var mIndicatorSize: Float
    private var mIndicatorDistance: Float
    private var mColorCurrentDefault: Int
    private var mColorCurrentPressed: Int
    private var mColorNormalDefault: Int
    private var mColorNormalPressed: Int
    private var mIndicatorsClickable: Boolean

    class object {
        private val DEFAULT_INDICATOR_SIZE = 4.0.toFloat()
        private val DEFAULT_INDICATOR_DISTANCE = 4.0.toFloat()
    }

    {
        mNumOfViews = 3
        mViewPager = null
        mIndicatorSize = 0.toFloat()
        mIndicatorDistance = 0.toFloat()
        mColorCurrentDefault = 0
        mColorCurrentPressed = 0
        mColorNormalDefault = 0
        mColorNormalPressed = 0
        mIndicatorsClickable = true

        // Horizontal layout by default
        if (getOrientation() != LinearLayout.VERTICAL) {
            setOrientation(LinearLayout.HORIZONTAL)
        }
        setGravity(Gravity.CENTER)

        val r: Resources? = getResources()
        if (r != null) {
            val a: TypedArray? = context.getTheme()?.obtainStyledAttributes(attrs, R.styleable.AndroidPageControl, R.attr.apcStyles, 0)
            if (a != null) {
                val indicatorSizeDefault = DEFAULT_INDICATOR_SIZE * r.getDisplayMetrics().density
                var indicatorSize = a.getDimension(R.styleable.AndroidPageControl_apc_indicatorSize, indicatorSizeDefault)
                if (indicatorSize <= 0) {
                    indicatorSize = indicatorSizeDefault
                }
                mIndicatorSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorSize, r.getDisplayMetrics())

                val indicatorDistanceDefault = DEFAULT_INDICATOR_DISTANCE * r.getDisplayMetrics().density
                var indicatorDistance = a.getDimension(R.styleable.AndroidPageControl_apc_indicatorDistance, indicatorDistanceDefault)
                if (indicatorDistance <= 0) {
                    indicatorDistance = indicatorDistanceDefault
                }
                mIndicatorDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorDistance, r.getDisplayMetrics())

                mColorCurrentDefault = a.getColor(R.styleable.AndroidPageControl_apc_colorCurrentDefault, r.getColor(R.color.apc_indicator_current_default))
                mColorCurrentPressed = a.getColor(R.styleable.AndroidPageControl_apc_colorCurrentPressed, r.getColor(R.color.apc_indicator_current_pressed))
                mColorNormalDefault = a.getColor(R.styleable.AndroidPageControl_apc_colorNormalDefault, r.getColor(R.color.apc_indicator_normal_default))
                mColorNormalPressed = a.getColor(R.styleable.AndroidPageControl_apc_colorNormalPressed, r.getColor(R.color.apc_indicator_normal_pressed))

                a.recycle()
            }
        }
    }

    public fun setPosition(position: Int) {
        if (0 <= position && position < mNumOfViews) {
            mViewPager?.setCurrentItem(position)
            invalidate()
        }
    }

    public fun setViewPager(viewPager: ViewPager) {
        mViewPager = viewPager
        updateNumOfViews()
        mViewPager?.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                updateNumOfViews()
                setPosition(position)
            }
        })
    }

    public fun setIndicatorsClickable(indicatorsClickable: Boolean) {
        mIndicatorsClickable = indicatorsClickable
        for (i in 0..getChildCount() - 1) {
            getChildAt(i)?.setClickable(indicatorsClickable)
        }
    }

    private fun updateNumOfViews() {
        mNumOfViews = mViewPager?.getAdapter()?.getCount() ?: 0
        removeAllViews()
        for (i in 0..mNumOfViews - 1) {
            val b = Button(getContext() as Context)
            setIndicatorBackground(b, i == mViewPager?.getCurrentItem() ?: 0)
            val lp = LinearLayout.LayoutParams(mIndicatorSize.toInt(), mIndicatorSize.toInt())
            if (getOrientation() == LinearLayout.HORIZONTAL) {
                lp.leftMargin = (mIndicatorDistance / 2).toInt()
                lp.rightMargin = (mIndicatorDistance / 2).toInt()
            } else {
                lp.topMargin = (mIndicatorDistance / 2).toInt()
                lp.bottomMargin = (mIndicatorDistance / 2).toInt()
            }
            b.setTag(i)
            b.setOnClickListener(this)
            addView(b, lp)
        }
        // Set current clickable state to new children
        setIndicatorsClickable(mIndicatorsClickable)
        requestLayout()
    }

    private fun setIndicatorBackground(b: Button, isCurrent: Boolean) {
        val drawableDefault = ShapeDrawable()
        drawableDefault.setShape(OvalShape())
        drawableDefault.getPaint()?.setColor(if (isCurrent) mColorCurrentDefault else mColorNormalDefault)
        val drawablePressed = ShapeDrawable()
        drawablePressed.setShape(OvalShape())
        drawablePressed.getPaint()?.setColor(if (isCurrent) mColorCurrentPressed else mColorNormalPressed)

        val sld = StateListDrawable()
        var statesPressed = IntArray(1)
        statesPressed.set(0, android.R.attr.state_pressed)
        sld.addState(statesPressed, drawablePressed)
        var statesDefault = IntArray(1)
        statesDefault.set(0, -android.R.attr.state_pressed)
        sld.addState(statesDefault, drawableDefault)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            b.setBackgroundDrawable(sld)
        } else {
            b.setBackground(sld)
        }
    }

    override fun onClick(view: View) {
        if (view !is Button) {
            return
        }
        val b = view as Button
        if (b.getTag() is Int) {
            val position = b.getTag() as Int
            setPosition(position)
        }
    }
}
