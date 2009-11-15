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
        mKeyboardLayouts.put("Ireland", "Ireland");
        mKeyboardLayouts.put("Japanese", "jp");
        mKeyboardLayouts.put("Russia", "ru");
        mKeyboardLayouts.put("Spanish(Latin America)", "spanish_latin");

        mKeyboardList = new ArrayList<String>(mKeyboardLayouts.keySet());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, layoutId, fieldId, mKeyboardList);
        getListView().setAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String kb = mKeyboardList.get(position);
        String name = mKeyboardLayouts.get(kb);
        SystemProperties.set("persist.sys.keylayout", name);
        Toast.makeText(this, "Set keyboard layout to " + kb +
                ".\nPlease reboot your machine to enable the new keyboard layout.", Toast.LENGTH_LONG).show();
    }
}
