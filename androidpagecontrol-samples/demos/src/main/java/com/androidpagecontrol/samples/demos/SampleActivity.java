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

package com.androidpagecontrol.samples.demos;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidpagecontrol.PageControl;

public class SampleActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        MyPagerAdapter adapter = new MyPagerAdapter(this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        PageControl pageControl = (PageControl) findViewById(R.id.pagecontrol);
        pageControl.setViewPager(viewPager);
        pageControl.setPosition(1);
    }

    private class MyPagerAdapter extends PagerAdapter {

        /**
         * Number of views(pages).
         */
        private static final int NUM_OF_VIEWS = 5;

        private Context mContext;

        public MyPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return NUM_OF_VIEWS;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView view = new TextView(mContext);
            view.setText("This is the " + (position + 1) + "th view.");

            container.addView(view);
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

}