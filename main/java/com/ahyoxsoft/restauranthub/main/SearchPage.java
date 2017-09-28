package com.ahyoxsoft.restauranthub.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ahyoxsoft.restauranthub.R;
import com.ahyoxsoft.restauranthub.auth.LoginPage;
import com.ahyoxsoft.restauranthub.utility.AppConstants;
import com.ahyoxsoft.restauranthub.utility.ConnectionURL;
import com.ahyoxsoft.restauranthub.utility.RequestSingleton;
import com.ahyoxsoft.restauranthub.utility.Restaurant;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.HashMap;

public class SearchPage extends AppCompatActivity {
    private LinearLayout searchContainer;
    private RequestSingleton mRequestSingleton;
    private RequestQueue mRequestQueue;
    private EditText searchView;
    private SharedPreferences pref;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pref = getSharedPreferences(AppConstants.ACCESS, MODE_PRIVATE);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        searchContainer = (LinearLayout) findViewById(R.id.search_container);
        searchContainer.removeAllViews();


        mRequestSingleton = RequestSingleton.getInstance(this);
        mRequestQueue = mRequestSingleton.getRequestQueue();

        searchView = (EditText) findViewById(R.id.search);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void search(String key) {
        searchContainer.removeAllViews();
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> param = new HashMap<>();
        param.put("searchKey", key);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                RequestSingleton.getEncodeURL(ConnectionURL.SEARCH_URL, param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Gson gson = new Gson();
                Restaurant restaurant = gson.fromJson(jsonObject.toString(), Restaurant.class);
                progressBar.setVisibility(View.GONE);
                for (Restaurant r : restaurant.getRestaurants()) {
                    auto(r);
                }
                //progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //progressDialog.dismiss();
            }
        });

        mRequestQueue.add(objectRequest);
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
                    Intent intent = new Intent(SearchPage.this, BookingPage.class);
                    intent.putExtra(AppConstants.RESTAURANT_ID, restaurant.getId());
                    intent.putExtra(AppConstants.RESTAURANT_NAME, restaurant.getName());
                    intent.putExtra(AppConstants.RESTAURANT_ADDRESS, restaurant.getAddress());
                    intent.putExtra(AppConstants.RESTAURANT_PHONE_NO, restaurant.getPhoneNo());
                    intent.putExtra(AppConstants.RESTAURANT_EMAIL, restaurant.getEmail());
                    startActivity(intent);
                }else {
                    startActivity(new Intent(SearchPage.this, LoginPage.class));
                }
            }
        });

        searchContainer.addView(row);
    }

    private boolean isLogin() {
        return pref.getBoolean(AppConstants.LOGIN, false);
    }
}
