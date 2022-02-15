package com.abc.sharefilesz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.abc.sharefilesz.fragment.QRConnectFragment;
import com.abc.sharefilesz.util.NetworkDeviceSelectedListener;
import com.abc.sharefilesz.R;
import com.abc.sharefilesz.model.NetworkDevice;

public class BarcodeScannerActivity extends Activity
{
    public static final String EXTRA_DEVICE_ID = "extraDeviceId";
    public static final String EXTRA_CONNECTION_ADAPTER = "extraConnectionAdapter";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setResult(RESULT_CANCELED);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        QRConnectFragment fragment = (QRConnectFragment) getSupportFragmentManager().findFragmentById(R.id.barcodeScannerFragment);

        if (fragment != null)
            fragment.setDeviceSelectedListener(new NetworkDeviceSelectedListener()
            {
                @Override
                public boolean onNetworkDeviceSelected(NetworkDevice networkDevice, NetworkDevice.Connection connection)
                {
                    setResult(RESULT_OK, new Intent()
                            .putExtra(EXTRA_DEVICE_ID, networkDevice.deviceId)
                            .putExtra(EXTRA_CONNECTION_ADAPTER, connection.adapterName));
                    finish();

                    return true;
                }

                @Override
                public boolean isListenerEffective()
                {
                    return true;
                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (android.R.id.home == item.getItemId())
            onBackPressed();
        else
            return super.onOptionsItemSelected(item);

        return true;
    }
}
