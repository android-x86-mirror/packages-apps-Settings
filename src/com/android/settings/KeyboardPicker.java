/*
 * Copyright (C) 2009 The Android-x86 Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class KeyboardPicker extends ListActivity {
    private List<String> mKeyboardList;
    private TreeMap<String,String> mKeyboardLayouts;

    int getContentView() {
        return R.layout.keyboard_picker;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(getContentView());
        int layoutId = R.layout.keyboard_picker_item;
        int fieldId = R.id.keyboard;
        mKeyboardLayouts = new TreeMap<String,String>();
        mKeyboardLayouts.put("American(Standard keyboard)", "qwerty");
        mKeyboardLayouts.put("Finnish", "fn");
        mKeyboardLayouts.put("French", "fr");
        mKeyboardLayouts.put("German", "de");
        mKeyboardLayouts.put("Ireland", "uk");
        mKeyboardLayouts.put("Japanese", "jp");
        mKeyboardLayouts.put("Russian", "ru");
        mKeyboardLayouts.put("Spanish(Latin America)", "es_latin");
        mKeyboardLayouts.put("Swedish", "fn");
        mKeyboardLayouts.put("United Kingdom", "uk");

        mKeyboardList = new ArrayList<String>(mKeyboardLayouts.keySet());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, layoutId, fieldId, mKeyboardList);
        getListView().setAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String kb = mKeyboardList.get(position);
        SystemProperties.set("persist.sys.keylayout", mKeyboardLayouts.get(kb));
        Toast.makeText(this, "Set keyboard layout to " + kb +
                ".\nPlease reboot your machine to enable the new keyboard layout.", Toast.LENGTH_LONG).show();
    }
}
