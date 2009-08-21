package com.android.settings.ethernet;


import static android.net.ethernet.EthernetManager.ETH_STATE_DISABLED;
import static android.net.ethernet.EthernetManager.ETH_STATE_DISABLING;
import static android.net.ethernet.EthernetManager.ETH_STATE_ENABLED;
import static android.net.ethernet.EthernetManager.ETH_STATE_ENABLING;
import static android.net.ethernet.EthernetManager.ETH_STATE_UNKNOWN;

import com.android.settings.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.preference.Preference;
import android.preference.CheckBoxPreference;
import android.text.TextUtils;
import android.util.Config;
import android.util.Log;

public class EthernetEnabler implements Preference.OnPreferenceChangeListener{
		private static final boolean LOCAL_LOGD = true;
		private static final String TAG = "SettingsEthEnabler";
		//private final IntentFilter mEthStateFilter;
	    private Context mContext;
	    private EthernetManager mEthManager;
	    private CheckBoxPreference mEthCheckBoxPref;
	    private final CharSequence mOriginalSummary;
	    private EthernetConfigDialog mEthConfigDialog;


		private final BroadcastReceiver mEthStateReceiver = new BroadcastReceiver() {

	        @Override
	        public void onReceive(Context context, Intent intent) {
	            if (intent.getAction().equals(EthernetManager.ETH_STATE_CHANGED_ACTION)) {
	                handleEthStateChanged(
	                        intent.getIntExtra(EthernetManager.EXTRA_ETH_STATE,
									EthernetManager.ETH_STATE_UNKNOWN),
	                        intent.getIntExtra(EthernetManager.EXTRA_PREVIOUS_ETH_STATE,
								EthernetManager.ETH_STATE_UNKNOWN));
	            } else if (intent.getAction().equals(EthernetManager.NETWORK_STATE_CHANGED_ACTION)) {
	                handleNetworkStateChanged(
	                        (NetworkInfo) intent.getParcelableExtra(EthernetManager.EXTRA_NETWORK_INFO));
	            }
	        }
	    };

	    public void setConfigDialog (EthernetConfigDialog Dialog) {
		 mEthConfigDialog = Dialog;
	    }

	    public EthernetEnabler(Context context,
			EthernetManager ethernetManager,
	            CheckBoxPreference ethernetCheckBoxPreference) {
	        mContext = context;
	        mEthCheckBoxPref = ethernetCheckBoxPreference;
	        mEthManager = ethernetManager;

	        mOriginalSummary = ethernetCheckBoxPreference.getSummary();
	        ethernetCheckBoxPreference.setPersistent(false);
	        if(mEthManager.getEthState() == ETH_STATE_ENABLED)
			mEthCheckBoxPref.setChecked(true);

	        /*
	        mEthStateFilter = new IntentFilter(EthernetManager.ETH_STATE_CHANGED_ACTION);
	        mEthStateFilter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
	        */

	    }



		public EthernetManager getManager() {
		return mEthManager;
	    }

	    public void resume() {
		/*
	        int state = mEthManager.getEthState();
	        // This is the widget enabled state, not the preference toggled state
	        mEthCheckBoxPref.setEnabled(state == ETH_STATE_ENABLED || state == ETH_STATE_DISABLED
	                || state == ETH_STATE_UNKNOWN);
		*/
	//        mContext.registerReceiver(mEthStateReceiver, mEthStateFilter);
	        mEthCheckBoxPref.setOnPreferenceChangeListener(this);
	    }

	    public void pause() {
	      //  mContext.unregisterReceiver(mEthStateReceiver);
	        mEthCheckBoxPref.setOnPreferenceChangeListener(null);
	    }

		public boolean onPreferenceChange(Preference preference, Object newValue) {

			setEthEnabled((Boolean)newValue);
			return false;
		}

	    private void setEthEnabled(final boolean enable) {

	        int state = mEthManager.getEthState();

		Log.i(TAG,"Show configuration dialog " + enable);
		// Disable button
	        mEthCheckBoxPref.setEnabled(false);

	        if (state != ETH_STATE_ENABLED && enable) {
			if (mEthManager.ethConfigured() != true) {
				// Now, kick off the setting dialog to get the configurations
				mEthConfigDialog.enableAfterConfig();
				mEthConfigDialog.show();

			} else {
				mEthManager.setEthEnabled(enable);
			}
	        } else {
			mEthManager.setEthEnabled(enable);
	        }

	        mEthCheckBoxPref.setChecked(enable);
	     // Disable button
	        mEthCheckBoxPref.setEnabled(true);
	    }

	    private void handleEthStateChanged(int ethState, int previousEthState) {

	        if (LOCAL_LOGD) {
	            Log.d(TAG, "Received wifi state changed from "
	                    + getHumanReadableEthState(previousEthState) + " to "
	                    + getHumanReadableEthState(ethState));
	        }
	        /*
	        if (ethState == Eth_STATE_DISABLED || ethState == ETH_STATE_ENABLED) {
	            mEthCheckBoxPref.setChecked(wifiState == ETH_STATE_ENABLED);
	            mEthCheckBoxPref
	                    .setSummary(wifiState == ETH_STATE_DISABLED ? mOriginalSummary : null);

	            mEthCheckBoxPref.setEnabled(isEnabledByDependency());

	        } else if (ethState == ETH_STATE_DISABLING || wifiState == ETH_STATE_ENABLING) {
	            mEthCheckBoxPref.setSummary(wifiState == ETH_STATE_ENABLING ? R.string.wifi_starting
	                    : R.string.wifi_stopping);

	        } else if (ethState == ETH_STATE_UNKNOWN) {
	            int message = R.string.wifi_error;
	            if (previousEthState == ETH_STATE_ENABLING) message = R.string.error_starting;
	            else if (previousEthState == ETH_STATE_DISABLING) message = R.string.error_stopping;

	            mEthCheckBoxPref.setChecked(false);
	            mEthCheckBoxPref.setSummary(message);
	            mEthCheckBoxPref.setEnabled(true);
	        }
	        */
	    }

	    private void handleNetworkStateChanged(NetworkInfo networkInfo) {

	        if (LOCAL_LOGD) {
	            Log.d(TAG, "Received network state changed to " + networkInfo);
	        }
	        /*
	        if (mEthernetManager.isEthEnabled()) {
	            String summary = ethStatus.getStatus(mContext,
	                    mEthManager.getConnectionInfo().getSSID(), networkInfo.getDetailedState());
	            mEthCheckBoxPref.setSummary(summary);
	        }
	        */
	    }

	    private boolean isEnabledByDependency() {
	        Preference dep = getDependencyPreference();
	        if (dep == null) {
	            return true;
	        }

	        return !dep.shouldDisableDependents();
	    }

	    private Preference getDependencyPreference() {
	        String depKey = mEthCheckBoxPref.getDependency();
	        if (TextUtils.isEmpty(depKey)) {
	            return null;
	        }

	        return mEthCheckBoxPref.getPreferenceManager().findPreference(depKey);
	    }

	    private static String getHumanReadableEthState(int wifiState) {

	        switch (wifiState) {
	            case ETH_STATE_DISABLED:
	                return "Disabled";
	            case ETH_STATE_DISABLING:
	                return "Disabling";
	            case ETH_STATE_ENABLED:
	                return "Enabled";
	            case ETH_STATE_ENABLING:
	                return "Enabling";
	            case ETH_STATE_UNKNOWN:
	                return "Unknown";
	            default:
	                return "Some other state!";
	        }

	    }
}
