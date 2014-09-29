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

import android.app.ListActivity
import java.text.Collator
import android.os.Bundle
import android.widget.SimpleAdapter
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.widget.ListView
import android.view.View
import android.content.Context
import java.util.Comparator
import java.util.HashMap
import java.util.ArrayList
import java.util.Collections

/**
 * @author Soichiro Kashima
 */
class MainActivity() : ListActivity() {

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListAdapter(SimpleAdapter(this, getData(), R.layout.list_item_main, array("className", "description"), intArray(R.id.className, R.id.description)))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(menu: MenuItem?): Boolean {
        if (menu!!.getItemId() == R.id.menu_about) {
            startActivity(Intent(getApplicationContext() as Context, javaClass<AboutActivity>()))
            return true
        }
        return false
    }

    private fun getData(): kotlin.List<Map<String, Any>> {
        val data = ArrayList<Map<String, Any>>()

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory("com.androidpagecontrol.samples.demos")

        val pm = getPackageManager()
        val list = pm!!.queryIntentActivities(mainIntent, 0)

        if (list == null) {
            return data
        }

        for (i in 0..list.size() - 1) {
            val info = list.get(i)
            val label = info.loadLabel(pm)?.toString() ?: info.activityInfo!!.name
            val labelPath = label!!.split("/")
            val nextLabel = labelPath[0]
            if (labelPath.size == 1) {
                addItem(data,
                        info.activityInfo!!.name!!.replace(info.activityInfo!!.packageName + ".", ""),
                        nextLabel,
                        activityIntent(info.activityInfo!!.applicationInfo!!.packageName!!,
                                info.activityInfo!!.name!!))
            }
        }

        // Android Studio(Kotlin plugin) says that this code has an error and it should implement
        //   override fun compare(o1: Map<String, Any>?, o2: Map<String, Any>?): Int
        // but it works. If I modify to code above, it results in a compile error :(
        // Note: data.sortBy(Comparator) and data.sort(Comparator) not work.
        Collections.sort(data, object : Comparator<Map<String, Any>> {
            override fun compare(lhs: Map<String, Any>, rhs: Map<String, Any>): Int {
                return Collator.getInstance()!!.compare(lhs.get("className") as String, rhs.get("className") as String)
            }
        })

        return data
    }

    protected fun activityIntent(pkg: String, componentName: String): Intent {
        val result = Intent()
        result.setClassName(pkg, componentName)
        return result
    }

    protected fun addItem(data: kotlin.MutableList<Map<String, Any>>, className: String, description: String, intent: Intent) {
        val temp = HashMap<String, Any>()
        temp.put("className", className)
        temp.put("description", description)
        temp.put("intent", intent)
        data.add(temp)
    }

    SuppressWarnings("unchecked")
    public override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        if (l!!.getItemAtPosition(position) is Map<*, *>) {
            val map = l.getItemAtPosition(position) as Map<*, *>
            startActivity(map.get("intent") as Intent)
        }
    }
}
