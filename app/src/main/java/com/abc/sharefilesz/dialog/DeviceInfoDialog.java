package com.abc.sharefilesz.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;

import com.abc.sharefilesz.R;
import com.abc.sharefilesz.db.AccessDatabase;
import com.abc.sharefilesz.model.NetworkDevice;
import com.abc.sharefilesz.util.AppUtils;
import com.abc.sharefilesz.util.NetworkDeviceLoader;

public class DeviceInfoDialog extends AlertDialog.Builder
{
    public static final String TAG = DeviceInfoDialog.class.getSimpleName();

    public DeviceInfoDialog(@NonNull final Activity activity, final AccessDatabase database,
                            final NetworkDevice device)
    {
        super(activity);

        try {
            database.reconstruct(device);

            @SuppressLint("InflateParams")
            View rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_device, null);

            ImageView image = rootView.findViewById(R.id.image);
            TextView text1 = rootView.findViewById(R.id.text1);
            TextView modelText = rootView.findViewById(R.id.modelText);
            TextView versionText = rootView.findViewById(R.id.versionText);
            final SwitchCompat accessSwitch = rootView.findViewById(R.id.accessSwitch);
            final SwitchCompat trustSwitch = rootView.findViewById(R.id.trustSwitch);

            NetworkDeviceLoader.showPictureIntoView(device, image, AppUtils.getDefaultIconBuilder(activity));
            text1.setText(device.nickname);
            modelText.setText(String.format("%s %s", device.brand.toUpperCase(), device.model.toUpperCase()));
            versionText.setText(device.versionName);
            accessSwitch.setChecked(!device.isRestricted);
            trustSwitch.setEnabled(!device.isRestricted);
            trustSwitch.setChecked(device.isTrusted);

            accessSwitch.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener()
                    {
                        @Override
                        public void onCheckedChanged(CompoundButton button, boolean isChecked)
                        {
                            device.isRestricted = !isChecked;
                            database.publish(device);
                            trustSwitch.setEnabled(isChecked);
                        }
                    }
            );

            trustSwitch.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener()
                    {
                        @Override
                        public void onCheckedChanged(CompoundButton button, boolean isChecked)
                        {
                            device.isTrusted = isChecked;
                            database.publish(device);
                        }
                    }
            );

            setView(rootView);
            setPositiveButton(R.string.butn_close, null);

            setNegativeButton(R.string.butn_remove, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    new RemoveDeviceDialog(activity, device)
                            .show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
