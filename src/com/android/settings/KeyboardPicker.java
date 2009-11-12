package com.android.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class KeyboardPicker extends ListActivity{
	int getContentView() {
        return R.layout.keyboard_picker;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(getContentView());
        int layoutId = R.layout.keyboard_picker_item;
        int fieldId = R.id.keyboard;
        List<String> mKeyboardLayout = new ArrayList<String>();
        mKeyboardLayout.add("American(Standard keyboard)");
        mKeyboardLayout.add("Finnish");
        mKeyboardLayout.add("French");
        mKeyboardLayout.add("German");
        mKeyboardLayout.add("Ireland");
        mKeyboardLayout.add("Russia");
        mKeyboardLayout.add("Spanish(Latin America)");


        ArrayAdapter<String> adapter =
            new ArrayAdapter<String>(this, layoutId, fieldId, mKeyboardLayout);
        getListView().setAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        switch(position){
		case 1:
			SystemProperties.set("persist.sys.keylayout","fn");
			break;
		case 2:
			SystemProperties.set("persist.sys.keylayout","fr");
			break;
		case 3:
			SystemProperties.set("persist.sys.keylayout","de");
			break;
		case 4:
			SystemProperties.set("persist.sys.keylayout","Ireland");
			break;
		case 5:
			SystemProperties.set("persist.sys.keylayout","ru");
			break;
		case 6:
			SystemProperties.set("persist.sys.keylayout","spanish_latin");
			break;
		default:
			SystemProperties.set("persist.sys.keylayout","qwerty");
			break;
	  }

        Toast.makeText(this, "Please reboot your machine to enable the new keyboard layout.", Toast.LENGTH_LONG).show();
    }
}
