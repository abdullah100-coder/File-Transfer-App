package com.abc.sharefilesz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.abc.sharefilesz.R;

public class SplashActivity extends Activity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       // setSkipPermissionRequest(true);
      //  setWelcomePageDisallowed(true);

        new Handler().postDelayed(this::gotoMainActivity, 3000);

    }

    private void gotoMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


}
