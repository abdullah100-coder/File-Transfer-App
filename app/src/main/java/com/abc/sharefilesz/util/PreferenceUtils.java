package com.abc.sharefilesz.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils extends com.genonbeta.android.framework.util.PreferenceUtils
{
    public static void syncDefaults(Context context)
    {
        syncDefaults(context, true, false);
    }

    public static void syncDefaults(Context context, boolean compare, boolean fromXml)
    {
        SharedPreferences preferences = AppUtils.getDefaultLocalPreferences(context);
        SharedPreferences binaryPreferences = AppUtils.getDefaultPreferences(context);

        if (compare)
            sync(preferences, binaryPreferences);
        else {
            if (fromXml)
                syncPreferences(preferences, binaryPreferences);
            else
                syncPreferences(binaryPreferences, preferences);
        }
    }
}
