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
import android.graphics.drawable.shapes.Shape
import android.graphics.drawable.shapes.RectShape

/**
 * View which has circle-formed page indicator.
 *
 * @author Soichiro Kashima
 */
open class PageControl(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs, 0), View.OnClickListener {
    private var mNumOfViews: Int
    private var mViewPager: ViewPager?
    private var mIndicatorSize: Float
    private var mCurrentIndicatorSize: Float
    private var mIndicatorDistance: Float
    private var mIndicatorShape: Int
    private var mColorCurrentDefault: Int
    private var mColorCurrentPressed: Int
    private var mColorNormalDefault: Int
    private var mColorNormalPressed: Int
    private var mIndicatorsClickable: Boolean
    private var mIndicatorsEnabled: Boolean

    class object {
        private val DEFAULT_INDICATOR_SIZE = 4.0.toFloat()
        private val DEFAULT_INDICATOR_DISTANCE = 4.0.toFloat()
        private val INDICATOR_SHAPE_CIRCLE = 0
        private val INDICATOR_SHAPE_RECTANGLE = 1
    }

    {
        mNumOfViews = 3
        mViewPager = null
        mIndicatorSize = 0.toFloat()
        mIndicatorDistance = 0.toFloat()
        mCurrentIndicatorSize = 0.toFloat()
        mIndicatorShape = INDICATOR_SHAPE_CIRCLE
        mColorCurrentDefault = 0
        mColorCurrentPressed = 0
        mColorNormalDefault = 0
        mColorNormalPressed = 0
        mIndicatorsClickable = true
        mIndicatorsEnabled = true

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

                val currentIndicatorSizeDefault = indicatorSize
                var currentIndicatorSize = a.getDimension(R.styleable.AndroidPageControl_apc_currentIndicatorSize, currentIndicatorSizeDefault)
                if (currentIndicatorSize <= 0) {
                    currentIndicatorSize = currentIndicatorSizeDefault
                }
                mCurrentIndicatorSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, currentIndicatorSize, r.getDisplayMetrics())

                mIndicatorShape = a.getInt(R.styleable.AndroidPageControl_apc_indicatorShape, INDICATOR_SHAPE_CIRCLE)
                if (mIndicatorShape < INDICATOR_SHAPE_CIRCLE || INDICATOR_SHAPE_RECTANGLE < mIndicatorShape) {
                    mIndicatorShape = INDICATOR_SHAPE_CIRCLE
                }

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

    public override fun setClickable(indicatorsClickable: Boolean) {
        mIndicatorsClickable = indicatorsClickable
        for (i in 0..getChildCount() - 1) {
            getChildAt(i)?.setClickable(indicatorsClickable)
        }
    }

    public override fun setEnabled(indicatorsEnabled: Boolean) {
        mIndicatorsEnabled = indicatorsEnabled
        for (i in 0..getChildCount() - 1) {
            getChildAt(i)?.setEnabled(indicatorsEnabled)
        }
    }

    private fun updateNumOfViews() {
        mNumOfViews = mViewPager?.getAdapter()?.getCount() ?: 0
        removeAllViews()
        for (i in 0..mNumOfViews - 1) {
            val b = Button(getContext() as Context)
            setIndicatorBackground(b, i == mViewPager?.getCurrentItem() ?: 0)
            var isCurrent: Boolean = (i == mViewPager?.getCurrentItem() ?: 0)
            val lp = if (isCurrent) {
                LinearLayout.LayoutParams(mCurrentIndicatorSize.toInt(), mCurrentIndicatorSize.toInt())
            } else {
                LinearLayout.LayoutParams(mIndicatorSize.toInt(), mIndicatorSize.toInt())
            }
            var margin: Int
            if (isCurrent) {
                margin = ((mIndicatorDistance - (mCurrentIndicatorSize - mIndicatorSize)) / 2).toInt()
            } else {
                margin = (mIndicatorDistance / 2).toInt()
            }
            if (getOrientation() == LinearLayout.HORIZONTAL) {
                lp.leftMargin = margin
                lp.rightMargin = margin
            } else {
                lp.topMargin = margin
                lp.bottomMargin = margin
            }
            b.setTag(i)
            b.setOnClickListener(this)
            addView(b, lp)
        }
        // Set current clickable state to new children
        setClickable(mIndicatorsClickable)
        setEnabled(mIndicatorsEnabled)
        requestLayout()
    }

    private fun setIndicatorBackground(b: Button, isCurrent: Boolean) {
        val drawableDefault = ShapeDrawable()
        drawableDefault.setShape(getIndicatorShape())
        drawableDefault.getPaint()?.setColor(if (isCurrent) mColorCurrentDefault else mColorNormalDefault)
        val drawablePressed = ShapeDrawable()
        drawablePressed.setShape(getIndicatorShape())
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

    private fun getIndicatorShape(): Shape {
        when (mIndicatorShape) {
            INDICATOR_SHAPE_RECTANGLE -> return RectShape()
            else -> return OvalShape()
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
