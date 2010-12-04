/*
 * Copyright (C) 2010 The Android-x86 Open Source Project
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

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.ContentResolver;
import android.preference.CheckBoxPreference;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;

import android.provider.Settings;

import android.util.Config;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedSettings extends PreferenceActivity {

    private static final String TAG = "AdvancedSettings";
    private CheckBoxPreference mRemovePowerOffDialog;
    private CheckBoxPreference mDisablePowerOffConfirmation ;
    private ContentResolver m_cr;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.advanced_settings);

        m_cr = this.getContentResolver();
        mRemovePowerOffDialog = (CheckBoxPreference) getPreferenceManager().findPreference("removepoweroffdialog");
        mDisablePowerOffConfirmation = (CheckBoxPreference) getPreferenceManager().findPreference("disablepoweroffconfirmation");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( mRemovePowerOffDialog != null ) {
             mRemovePowerOffDialog.setChecked(Settings.System.getInt(m_cr, Settings.System.REMOVE_POWER_OFF_DIALOG, 0) == 1);
        }
        if ( mDisablePowerOffConfirmation != null ) {
            mDisablePowerOffConfirmation.setChecked(Settings.System.getInt(m_cr, Settings.System.DISABLE_CONFIRMATION, 0) == 1);
        }
    }

    @Override
    protected void onDestroy() {
        if ( mRemovePowerOffDialog != null ) {
            Settings.System.putInt(m_cr, Settings.System.REMOVE_POWER_OFF_DIALOG, mRemovePowerOffDialog.isChecked() ? 1 : 0);
        }
        if ( mDisablePowerOffConfirmation != null ) {
            Settings.System.putInt(m_cr, Settings.System.DISABLE_CONFIRMATION, mDisablePowerOffConfirmation.isChecked() ? 1 : 0);
        }
        super.onDestroy();
     }

}
