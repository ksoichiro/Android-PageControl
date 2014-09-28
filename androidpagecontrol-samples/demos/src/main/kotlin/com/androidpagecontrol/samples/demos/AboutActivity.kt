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

package com.androidpagecontrol.samples.demos

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.view.MenuItem
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.annotation.TargetApi

/**
 * @author Soichiro Kashima
 */
class AboutActivity() : Activity() {

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)

        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
            getActionBar()!!.setDisplayHomeAsUpEnabled(true)
            if (Build.VERSION_CODES.ICE_CREAM_SANDWICH <= Build.VERSION.SDK_INT) {
                getActionBar()!!.setHomeButtonEnabled(true)
            }
        }

        (findViewById(R.id.version_name) as TextView).setText(getVersionName())
    }

    override fun onOptionsItemSelected(menu: MenuItem?): Boolean {
        if (menu!!.getItemId() == android.R.id.home) {
            finish()
            return true
        }
        return false
    }

    private fun getVersionName(): String? {
        try {
            return getPackageManager()!!.getPackageInfo(getPackageName(), PackageManager.GET_META_DATA).versionName
        } catch (e: NameNotFoundException) {
            return ""
        }
    }

}
