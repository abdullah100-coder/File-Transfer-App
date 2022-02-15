package com.abc.sharefilesz.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;

import com.abc.sharefilesz.base.AppConfig;
import com.abc.sharefilesz.base.Keyword;
import com.abc.sharefilesz.base.App;
import com.abc.sharefilesz.BuildConfig;
import com.abc.sharefilesz.R;
import com.abc.sharefilesz.db.AccessDatabase;
import com.abc.sharefilesz.dialog.RationalePermissionRequest;
import com.abc.sharefilesz.view.TextDrawable;
import com.abc.sharefilesz.model.NetworkDevice;
import com.abc.sharefilesz.service.DeviceScannerService;
import com.genonbeta.android.framework.preference.DbSharablePreferences;
import com.genonbeta.android.framework.preference.SuperPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.AnyRes;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AppUtils
{
    public static final String TAG = AppUtils.class.getSimpleName();

    private static int mUniqueNumber = 0;
    private static AccessDatabase mDatabase;
    private static SuperPreferences mDefaultPreferences;
    private static SuperPreferences mDefaultLocalPreferences;
    private static SuperPreferences mViewingPreferences;

    public static void applyAdapterName(NetworkDevice.Connection connection)
    {
        if (connection.ipAddress == null) {
            Log.e(AppUtils.class.getSimpleName(), "Connection should be provided with IP address");
            return;
        }

        List<AddressedInterface> interfaceList = NetworkUtils.getInterfaces(true, AppConfig.DEFAULT_DISABLED_INTERFACES);

        for (AddressedInterface addressedInterface : interfaceList) {
            if (NetworkUtils.getAddressPrefix(addressedInterface.getAssociatedAddress())
                    .equals(NetworkUtils.getAddressPrefix(connection.ipAddress))) {
                connection.adapterName = addressedInterface.getNetworkInterface().getDisplayName();
                return;
            }
        }

        connection.adapterName = Keyword.Local.NETWORK_INTERFACE_UNKNOWN;
    }

    public static void applyDeviceToJSON(Context context, JSONObject object) throws JSONException
    {
        NetworkDevice device = getLocalDevice(context);
        JSONObject deviceInformation = new JSONObject();
        JSONObject appInfo = new JSONObject();

        deviceInformation.put(Keyword.DEVICE_INFO_SERIAL, device.deviceId);
        deviceInformation.put(Keyword.DEVICE_INFO_BRAND, device.brand);
        deviceInformation.put(Keyword.DEVICE_INFO_MODEL, device.model);
        deviceInformation.put(Keyword.DEVICE_INFO_USER, device.nickname);

        try {
            ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(context.openFileInput("profilePicture"));

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageBytes);
            deviceInformation.put(Keyword.DEVICE_INFO_PICTURE, Base64.encodeToString(imageBytes.toByteArray(), 0));
        } catch (Exception e) {
            // do nothing
        }

        appInfo.put(Keyword.APP_INFO_VERSION_CODE, device.versionNumber);
        appInfo.put(Keyword.APP_INFO_VERSION_NAME, device.versionName);

        object.put(Keyword.APP_INFO, appInfo);
        object.put(Keyword.DEVICE_INFO, deviceInformation);
    }



    public static boolean checkRunningConditions(Context context)
    {
        for (RationalePermissionRequest.PermissionRequest request : getRequiredPermissions(context))
            if (ActivityCompat.checkSelfPermission(context, request.permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }


    public static TextDrawable.IShapeBuilder getDefaultIconBuilder(Context context)
    {
        TextDrawable.IShapeBuilder builder = TextDrawable.builder();

        builder.beginConfig()
                .firstLettersOnly(true)
                .textMaxLength(1)
                .textColor(ContextCompat.getColor(context, AppUtils.getReference(context, R.attr.colorControlNormal)))
                .shapeColor(ContextCompat.getColor(context, AppUtils.getReference(context, R.attr.colorPassive)));

        return builder;
    }

    public static AccessDatabase getDatabase(Context context)
    {
        if (mDatabase == null)
            mDatabase = new AccessDatabase(context);

        return mDatabase;
    }



    public static SuperPreferences getDefaultPreferences(final Context context)
    {
        if (mDefaultPreferences == null) {
            DbSharablePreferences databasePreferences = new DbSharablePreferences(context, "__default", true)
                    .setUpdateListener(new DbSharablePreferences.AsynchronousUpdateListener()
                    {
                        @Override
                        public void onCommitComplete()
                        {
                            context.sendBroadcast(new Intent(App.ACTION_REQUEST_PREFERENCES_SYNC));
                        }
                    });

            mDefaultPreferences = new SuperPreferences(databasePreferences);
            mDefaultPreferences.setOnPreferenceUpdateListener(new SuperPreferences.OnPreferenceUpdateListener()
            {
                @Override
                public void onPreferenceUpdate(SuperPreferences superPreferences, boolean commit)
                {
                    PreferenceUtils.syncPreferences(superPreferences, getDefaultLocalPreferences(context).getWeakManager());
                }
            });
        }

        return mDefaultPreferences;
    }

    public static SuperPreferences getDefaultLocalPreferences(final Context context)
    {
        if (mDefaultLocalPreferences == null) {
            mDefaultLocalPreferences = new SuperPreferences(PreferenceManager.getDefaultSharedPreferences(context));

            mDefaultLocalPreferences.setOnPreferenceUpdateListener(new SuperPreferences.OnPreferenceUpdateListener()
            {
                @Override
                public void onPreferenceUpdate(SuperPreferences superPreferences, boolean commit)
                {
                    PreferenceUtils.syncPreferences(superPreferences, getDefaultPreferences(context).getWeakManager());
                }
            });
        }

        return mDefaultLocalPreferences;
    }

    public static String getDeviceSerial(Context context)
    {
        return Build.VERSION.SDK_INT < 26
                ? Build.SERIAL
                : (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED ? Build.getSerial() : null);
    }

    public static String getFriendlySSID(String ssid)
    {
        return ssid
                .replace("\"", "")
                .substring(AppConfig.PREFIX_ACCESS_POINT.length())
                .replace("_", " ");
    }

    @NonNull
    public static String getHotspotName(Context context)
    {
        return AppConfig.PREFIX_ACCESS_POINT + AppUtils.getLocalDeviceName(context)
                .replaceAll(" ", "_");
    }

    public static String getLocalDeviceName(Context context)
    {
        String deviceName = getDefaultPreferences(context)
                .getString("device_name", null);

        return deviceName == null || deviceName.length() == 0
                ? Build.MODEL.toUpperCase()
                : deviceName;
    }

    public static NetworkDevice getLocalDevice(Context context)
    {
        NetworkDevice device = new NetworkDevice(getDeviceSerial(context));

        device.brand = Build.BRAND;
        device.model = Build.MODEL;
        device.nickname = AppUtils.getLocalDeviceName(context);
        device.isRestricted = false;
        device.isLocalAddress = true;

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0);

            device.versionNumber = packageInfo.versionCode;
            device.versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return device;
    }

    @AnyRes
    public static int getReference(Context context, @AttrRes int refId)
    {
        TypedValue typedValue = new TypedValue();

        if (!context.getTheme().resolveAttribute(refId, typedValue, true)) {
            TypedArray values = context.getTheme().obtainStyledAttributes(context.getApplicationInfo().theme,
                    new int[]{refId});

            return values.length() > 0
                    ? values.getResourceId(0, 0)
                    : 0;
        }

        return typedValue.resourceId;
    }

    public static List<RationalePermissionRequest.PermissionRequest> getRequiredPermissions(Context context)
    {
        List<RationalePermissionRequest.PermissionRequest> permissionRequests = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= 16) {
            permissionRequests.add(new RationalePermissionRequest.PermissionRequest(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    R.string.text_requestPermissionStorage,
                    R.string.text_requestPermissionStorageSummary));
        }

        if (Build.VERSION.SDK_INT >= 26) {
            permissionRequests.add(new RationalePermissionRequest.PermissionRequest(context,
                    Manifest.permission.READ_PHONE_STATE,
                    R.string.text_requestPermissionReadPhoneState,
                    R.string.text_requestPermissionReadPhoneStateSummary));
        }

        return permissionRequests;
    }

    public static int getUniqueNumber()
    {
        return (int) (System.currentTimeMillis() / 1000) + (++mUniqueNumber);
    }

    public static SuperPreferences getViewingPreferences(Context context)
    {
        if (mViewingPreferences == null)
            mViewingPreferences = new SuperPreferences(context.getSharedPreferences(Keyword.Local.SETTINGS_VIEWING, Context.MODE_PRIVATE));

        return mViewingPreferences;
    }



    public static <T> T quickAction(T clazz, QuickActions<T> quickActions)
    {
        quickActions.onQuickActions(clazz);
        return clazz;
    }

    public static boolean toggleDeviceScanning(Context context)
    {
        if (DeviceScannerService.getDeviceScanner().isScannerAvailable()) {
            context.startService(new Intent(context, DeviceScannerService.class)
                    .setAction(DeviceScannerService.ACTION_SCAN_DEVICES));

            return true;
        }

        DeviceScannerService.getDeviceScanner()
                .interrupt();

        return false;
    }

    public static void startWebShareActivity(Context context, boolean startWebShare) {

    }

    public static void startForegroundService(Context context, Intent intent)
    {
        if (Build.VERSION.SDK_INT >= 26)
            context.startForegroundService(intent);
        else
            context.startService(intent);
    }

    public interface QuickActions<T>
    {
        void onQuickActions(T clazz);
    }
}