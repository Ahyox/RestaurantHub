package com.ahyoxsoft.restauranthub.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahyoxsoft.restauranthub.R;
import com.ahyoxsoft.restauranthub.auth.LoginPage;
import com.ahyoxsoft.restauranthub.utility.AppConstants;
import com.ahyoxsoft.restauranthub.utility.LocationGetter;
import com.ahyoxsoft.restauranthub.utility.RequestSingleton;
import com.ahyoxsoft.restauranthub.utility.Restaurant;
import com.android.volley.RequestQueue;

import java.io.IOException;
import java.util.List;

public class LocationPage extends AppCompatActivity {
    private LocationGetter locationGetter;
    private LocationGetter.Coordinates coordinates;
    private LinearLayout searchContainer;
    private RequestSingleton mRequestSingleton;
    private RequestQueue mRequestQueue;
    private SharedPreferences pref;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        locationGetter = new LocationGetter(this);
        coordinates = locationGetter.getLocation(20, 20);

        pref = getSharedPreferences(AppConstants.ACCESS, MODE_PRIVATE);

        searchContainer = (LinearLayout) findViewById(R.id.search_container);
        searchContainer.removeAllViews();

        mRequestSingleton = RequestSingleton.getInstance(this);
        mRequestQueue = mRequestSingleton.getRequestQueue();


        geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(coordinates.getLatitude(), coordinates.getLongitude(), 10);

            for (Address address : addresses) {
                Log.e("Country Code", address.getCountryCode());
                Log.e("Locality", address.getLocality());
                Log.e("Admin Area", address.getAdminArea());
                Log.e("Feature Name", address.getFeatureName());
                Log.e("Phone", address.getPhone());
                Log.e("Sub Locality", address.getSubLocality());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Hello", "Hello");
        }
        Toast.makeText(this, "Latitude: "+coordinates.getLatitude()+ " Longitude: "+coordinates.getLongitude(), Toast.LENGTH_LONG).show();
    }


    private void auto(final Restaurant restaurant) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.restaurant_summary, null);
        TextView restaurantNameView = (TextView) row.findViewById(R.id.restaurant_name);
        TextView restaurantAddressView = (TextView) row.findViewById(R.id.restaurant_address);
        TextView stateView = (TextView) row.findViewById(R.id.state);
        TextView countryView = (TextView) row.findViewById(R.id.country);

        restaurantNameView.setText(restaurant.getName());
        restaurantAddressView.setText(restaurant.getAddress());
        stateView.setText(restaurant.getState());
        countryView.setText(restaurant.getCountry());

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin()) {
                    Intent intent = new Intent(LocationPage.this, BookingPage.class);
                    intent.putExtra(AppConstants.RESTAURANT_ID, restaurant.getId());
                    intent.putExtra(AppConstants.RESTAURANT_NAME, restaurant.getName());
                    intent.putExtra(AppConstants.RESTAURANT_ADDRESS, restaurant.getAddress());
                    intent.putExtra(AppConstants.RESTAURANT_PHONE_NO, restaurant.getPhoneNo());
                    intent.putExtra(AppConstants.RESTAURANT_EMAIL, restaurant.getEmail());
                    startActivity(intent);
                }else {
                    startActivity(new Intent(LocationPage.this, LoginPage.class));
                }
            }
        });

        searchContainer.addView(row);
    }

    private boolean isLogin() {
        return pref.getBoolean(AppConstants.LOGIN, false);
    }
}
