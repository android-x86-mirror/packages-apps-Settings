/*
 * Copyright (C) 2018 The LineageOS Project
 * Copyright (C) 2019 The Android-x86 Open Source Project
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

package com.android.settings.development;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings;
import androidx.annotation.VisibleForTesting;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

import java.io.File;

public class RootAccessPreferenceController extends DeveloperOptionsPreferenceController
        implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {

    private static final String TAG = "RootAccessPreferenceController";
    private static final String PREF_KEY = "root_access";

    private static final String ROOT_ACCESS_PROPERTY = "persist.sys.root_access";

    private final DevelopmentSettingsDashboardFragment mFragment;
    private Object mPendingRootAccessValue;

    public RootAccessPreferenceController(Context context,
            DevelopmentSettingsDashboardFragment fragment) {
        super(context);

        mFragment = fragment;
    }

    @Override
    public boolean isAvailable() {
        // User builds don't get root, and eng always gets root
        return Build.IS_DEBUGGABLE || "eng".equals(Build.TYPE);
    }

    @Override
    public String getPreferenceKey() {
        return PREF_KEY;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        final File file = new File("/system/xbin/su");
        if (file.exists()) {
            ((ListPreference) mPreference).setEntries(R.array.root_access_entries);
            ((ListPreference) mPreference).setEntryValues(R.array.root_access_values);
        } else {
            ((ListPreference) mPreference).setEntries(R.array.root_access_entries_adb);
            ((ListPreference) mPreference).setEntryValues(R.array.root_access_values_adb);
        }

        updatePreference();

        if (!isAdminUser()) {
            mPreference.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if ("0".equals(newValue.toString())) {
            writeRootAccessOptions(newValue);
        } else {
            mPendingRootAccessValue = newValue;
            RootAccessWarningDialog.show(mFragment, this);
        }
        return true;
    }

    @Override
    protected void onDeveloperOptionsSwitchEnabled() {
        if (isAdminUser()) {
            mPreference.setEnabled(true);
        }
    }

    public void onRootAccessDialogConfirmed() {
        writeRootAccessOptions(mPendingRootAccessValue);
    }

    public void onRootAccessDialogDismissed() {
        updatePreference();
    }

    private void writeRootAccessOptions(Object newValue) {
        String oldValue = SystemProperties.get(ROOT_ACCESS_PROPERTY, "0");
        SystemProperties.set(ROOT_ACCESS_PROPERTY, newValue.toString());
        if (Integer.valueOf(newValue.toString()) < 2 && !oldValue.equals(newValue)
                && SystemProperties.getInt("service.adb.root", 0) == 1) {
            SystemProperties.set("service.adb.root", "0");
            Settings.Secure.putInt(mContext.getContentResolver(),
                    Settings.Secure.ADB_ENABLED, 0);
            Settings.Secure.putInt(mContext.getContentResolver(),
                    Settings.Secure.ADB_ENABLED, 1);
        }
        updatePreference();
    }

    private void updatePreference() {
        String value = SystemProperties.get(ROOT_ACCESS_PROPERTY, "0");
        ((ListPreference) mPreference).setValue(value);
        ((ListPreference) mPreference).setSummary(mContext.getResources()
                .getStringArray(R.array.root_access_entries)[Integer.valueOf(value)]);
    }

    @VisibleForTesting
    boolean isAdminUser() {
        return ((UserManager) mContext.getSystemService(Context.USER_SERVICE)).isAdminUser();
    }
}
