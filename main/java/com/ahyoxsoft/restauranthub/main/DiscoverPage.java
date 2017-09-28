package com.ahyoxsoft.restauranthub.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ahyoxsoft.restauranthub.R;
import com.ahyoxsoft.restauranthub.utility.AppConstants;

public class DiscoverPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViewById(R.id.breakfast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiscoverPage.this, RestaurantListing.class);
                intent.putExtra(AppConstants.BREAKFAST, AppConstants.BREAKFAST);
                startActivity(intent);
            }
        });

        findViewById(R.id.lunch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiscoverPage.this, RestaurantListing.class);
                intent.putExtra(AppConstants.LUNCH, AppConstants.LUNCH);
                startActivity(intent);
            }
        });

        findViewById(R.id.afternoon_tea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiscoverPage.this, RestaurantListing.class);
                intent.putExtra(AppConstants.AFTERNOON_TEA, AppConstants.AFTERNOON_TEA);
                startActivity(intent);
            }
        });

        findViewById(R.id.dinner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiscoverPage.this, RestaurantListing.class);
                intent.putExtra(AppConstants.DINNER, AppConstants.DINNER);
                startActivity(intent);
            }
        });
    }
}
