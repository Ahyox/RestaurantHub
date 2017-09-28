package com.ahyoxsoft.restauranthub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.ahyoxsoft.restauranthub.main.Dashboard;
import com.ahyoxsoft.restauranthub.utility.AppConstants;

public class SplashScreen extends AppCompatActivity {
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        pref = getSharedPreferences(AppConstants.ACCESS, MODE_PRIVATE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!pref.getBoolean(AppConstants.LOGIN, false) && pref.getInt(AppConstants.USER_ID, 0) == 0) {
                    startActivity(new Intent(SplashScreen.this, InfoGraphics.class));
                }else {
                    startActivity(new Intent(SplashScreen.this, Dashboard.class));
                }
            }
        }, 2000L);
    }


}
