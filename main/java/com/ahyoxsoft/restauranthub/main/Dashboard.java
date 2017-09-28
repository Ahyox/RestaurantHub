package com.ahyoxsoft.restauranthub.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahyoxsoft.restauranthub.InfoGraphics;
import com.ahyoxsoft.restauranthub.R;
import com.ahyoxsoft.restauranthub.auth.LoginPage;
import com.ahyoxsoft.restauranthub.drawer.AboutPage;
import com.ahyoxsoft.restauranthub.drawer.SettingsPage;
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
import java.util.Calendar;
import java.util.HashMap;

public class Dashboard extends AppCompatActivity  {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout dailyFoodImage;
    private TextView dailyFood;
    private LinearLayout popularRestaurant;
    private SharedPreferences pref;
    private RequestSingleton mRequestSingleton;
    private RequestQueue mRequestQueue;
    private TextView welcomeMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        welcomeMessage = (TextView) findViewById(R.id.welcome_message);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dailyFoodImage = (RelativeLayout) findViewById(R.id.daily_food_image);
        dailyFood = (TextView) findViewById(R.id.daily_food);
        popularRestaurant = (LinearLayout) findViewById(R.id.popular_restaurant);

        //This is the guy that response to drawer toggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // TODO Auto-generated method stub
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // TODO Auto-generated method stub
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        findViewById(R.id.place_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, SearchPage.class));
            }
        });

        findViewById(R.id.payment_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        pref = getSharedPreferences(AppConstants.ACCESS, MODE_PRIVATE);
        welcomeMessage.setText("Hello " + pref.getString(AppConstants.USERNAME, "Foodie"));

        mRequestSingleton = RequestSingleton.getInstance(this);
        mRequestQueue = mRequestSingleton.getRequestQueue();

    }

    @Override
    protected void onStart() {
        super.onStart();
        popularRestaurant.removeAllViews();
        checkDailyMenu();
        network();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void closeDrawer(int value) {
        if(isLogin()) {
            switch (value) {
                case 1:
                    startActivity(new Intent(Dashboard.this, DiscoverPage.class));
                    break;
                case 2:
                    //startActivity(new Intent(Dashboard.this, ));
                    break;
                case 3:
                    startActivity(new Intent(Dashboard.this, FundAccount.class));
                    break;
                case 4:
                    startActivity(new Intent(Dashboard.this, AboutPage.class));
                    break;
                case 5:
                    startActivity(new Intent(Dashboard.this, SettingsPage.class));
                    break;
                case 6:
                    //startActivity(new Intent(Dashboard.this, ));
                    break;
                case 7:
                    logout();
                    break;
            }

        } else {
            startActivity(new Intent(Dashboard.this, LoginPage.class));
        }

        drawerLayout.closeDrawers();
    }

    private void network () {
        popularRestaurant.removeAllViews();
        HashMap<String, String> param = new HashMap<>();
        param.put("searchKey", "");
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                RequestSingleton.getEncodeURL(ConnectionURL.RANDOM_RESTAURANT_URL, param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Gson gson = new Gson();
                Restaurant restaurant = gson.fromJson(jsonObject.toString(), Restaurant.class);

                for (Restaurant r : restaurant.getRestaurants()) {
                    populate(r);
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

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You about to log out from the app. Do you want to continue?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pref.getString(AppConstants.LOGIN_METHOD, "").equals(AppConstants.APP)) {
                    //Log out code here
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(AppConstants.LOGIN, false);
                    editor.putString(AppConstants.LOGIN_METHOD, "");
                    editor.putString(AppConstants.USERNAME, "Foodie");
                    editor.putInt(AppConstants.USER_ID, 0);
                    editor.apply();
                    Intent intent = new Intent(Dashboard.this, InfoGraphics.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void checkDailyMenu() {
        Calendar calendar =  Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour > 23 && hour < 12) {
            dailyFood.setText("BREAKFAST ?");
            dailyFoodImage.setBackgroundResource(R.drawable.breakfast);
        }else if (hour > 12 && hour <= 16) {
            dailyFood.setText("LUNCH ?");
        }else if (hour > 16 && hour <= 23) {
            dailyFood.setText("DINNER ?");
            dailyFoodImage.setBackgroundResource(R.drawable.dinner);
        }
    }

    private void populate(final Restaurant restaurant) {
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
                    Intent intent = new Intent(Dashboard.this, BookingPage.class);
                    intent.putExtra(AppConstants.RESTAURANT_ID, restaurant.getId());
                    intent.putExtra(AppConstants.RESTAURANT_NAME, restaurant.getName());
                    intent.putExtra(AppConstants.RESTAURANT_ADDRESS, restaurant.getAddress());
                    intent.putExtra(AppConstants.RESTAURANT_PHONE_NO, restaurant.getPhoneNo());
                    intent.putExtra(AppConstants.RESTAURANT_EMAIL, restaurant.getEmail());
                    startActivity(intent);
                }else {
                    startActivity(new Intent(Dashboard.this, LoginPage.class));
                }
            }
        });

        popularRestaurant.addView(row);
    }

    private boolean isLogin() {
        return pref.getBoolean(AppConstants.LOGIN, false);
    }

    private void openGPSDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to on GPS");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
