package com.ahyoxsoft.restauranthub.main;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import com.ahyoxsoft.restauranthub.R;

public class RestaurantListing extends AppCompatActivity {
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_listing);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        videoView = (VideoView) findViewById(R.id.video);
        //"android.resource://com.myapplication/" + R.drawable.vidio);
        //videoView.setVideoURI(Uri.parse("android.resource://"+getPackageName() + R.drawable.video));
        videoView.start();
    }
}
