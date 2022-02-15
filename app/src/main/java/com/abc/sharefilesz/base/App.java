package com.abc.sharefilesz.base;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.abc.sharefilesz.R;
import com.abc.sharefilesz.util.AppUtils;
import com.abc.sharefilesz.util.PreferenceUtils;
import com.genonbeta.android.framework.preference.DbSharablePreferences;
import com.onesignal.OneSignal;

public class App extends Application
{
    public static final String TAG = App.class.getSimpleName();
    public static final String ACTION_REQUEST_PREFERENCES_SYNC = "com.genonbeta.intent.action.REQUEST_PREFERENCES_SYNC";

    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent != null)
                if (ACTION_REQUEST_PREFERENCES_SYNC.equals(intent.getAction())) {
                    SharedPreferences preferences = AppUtils.getDefaultPreferences(context).getWeakManager();

                    if (preferences instanceof DbSharablePreferences)
                        ((DbSharablePreferences) preferences).sync();
                }
        }
    };

    @Override
    public void onCreate()
    {
        super.onCreate();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
              //  .autoPromptLocation(true)
                .init();

        initializeSettings();
        getApplicationContext().registerReceiver(mReceiver, new IntentFilter(ACTION_REQUEST_PREFERENCES_SYNC));

    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        getApplicationContext().unregisterReceiver(mReceiver);
    }

    private void initializeSettings()
    {
        SharedPreferences defaultPreferences = AppUtils.getDefaultLocalPreferences(this);
        boolean nsdDefined = defaultPreferences.contains("nsd_enabled");

        PreferenceManager.setDefaultValues(this, R.xml.preferences_defaults_main, false);
        
        
        if (!nsdDefined)
            defaultPreferences.edit()
                    .putBoolean("nsd_enabled", Build.VERSION.SDK_INT >= 19)
                    .apply();

        PreferenceUtils.syncDefaults(getApplicationContext());

    }
}
