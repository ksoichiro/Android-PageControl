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
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.androidpagecontrol.PageControl;

public class DefaultActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        SamplePagerAdapter adapter = new SamplePagerAdapter(this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        final PageControl pageControl = (PageControl) findViewById(R.id.page_control);
        pageControl.setViewPager(viewPager);
        pageControl.setPosition(1);

        findViewById(R.id.button_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                if (b.getText().equals(getString(R.string.label_enable_click))) {
                    b.setText(R.string.label_disable_click);
                    pageControl.setIndicatorsClickable(true);
                } else {
                    b.setText(R.string.label_enable_click);
                    pageControl.setIndicatorsClickable(false);
                }
            }
        });
    }

}