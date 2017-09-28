package com.ahyoxsoft.restauranthub.drawer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ahyoxsoft.restauranthub.R;

public class AboutPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
