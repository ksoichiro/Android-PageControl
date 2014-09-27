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

    TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
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
        val id = menu!!.getItemId()
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return false
    }

    private fun getVersionName(): String? {
        val manager = getPackageManager()
        val versionName: String?
        try {
            val info = manager!!.getPackageInfo(getPackageName(), PackageManager.GET_META_DATA)
            versionName = info.versionName
        } catch (e: NameNotFoundException) {
            versionName = ""
        }

        return versionName
    }

}
