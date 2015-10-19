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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class RootAccessWarningDialog extends InstrumentedDialogFragment implements
        DialogInterface.OnClickListener, DialogInterface.OnDismissListener {

    public static final String TAG = "RootAccessWarningDialog";

    public static void show(DevelopmentSettingsDashboardFragment host,
            RootAccessPreferenceController controller) {
        final FragmentManager manager = host.getActivity().getSupportFragmentManager();
        if (manager.findFragmentByTag(TAG) == null) {
            final RootAccessWarningDialog dialog = new RootAccessWarningDialog();
            dialog.setTargetFragment(host, 0 /* requestCode */);
            dialog.show(manager, TAG);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.TYPE_UNKNOWN;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.root_access_warning_title)
                .setMessage(R.string.root_access_warning_message)
                .setPositiveButton(android.R.string.ok, this /* onClickListener */)
                .setNegativeButton(android.R.string.cancel, this /* onClickListener */)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final RootAccessDialogHost host = (RootAccessDialogHost) getTargetFragment();
        if (host != null) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                host.onRootAccessDialogConfirmed();
            } else {
                host.onRootAccessDialogDismissed();
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final RootAccessDialogHost host = (RootAccessDialogHost) getTargetFragment();
        if (host != null) {
            host.onRootAccessDialogDismissed();
        }
    }
}
