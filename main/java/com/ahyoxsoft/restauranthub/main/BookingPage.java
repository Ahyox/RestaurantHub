package com.ahyoxsoft.restauranthub.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ahyoxsoft.restauranthub.R;
import com.ahyoxsoft.restauranthub.utility.AppConstants;
import com.ahyoxsoft.restauranthub.utility.ConnectionURL;
import com.ahyoxsoft.restauranthub.utility.RequestSingleton;
import com.ahyoxsoft.restauranthub.utility.ServerResponse;
import com.ahyoxsoft.restauranthub.utility.Transaction;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookingPage extends AppCompatActivity {
    private LinearLayout orderContainer;
    private double total = 0.0;
    private TextView restaurantNameView, restaurantAddressView;
    private String call, email, restaurantName, restaurantAddress;
    private int restaurantID;
    private LinearLayout submitButton;
    private RequestSingleton mRequestSingleton;
    private RequestQueue mRequestQueue;
    private Map<String, String> foodItem = new HashMap<>();
    private String selectedFoodItem = "";
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        orderContainer = (LinearLayout) findViewById(R.id.order_container);
        orderContainer.removeAllViews();

        Intent intent = getIntent();

        restaurantNameView = (TextView) findViewById(R.id.restaurant_name);
        restaurantAddressView = (TextView) findViewById(R.id.restaurant_address);

        restaurantName = intent.getStringExtra(AppConstants.RESTAURANT_NAME);
        restaurantAddress = intent.getStringExtra(AppConstants.RESTAURANT_ADDRESS);

        call = intent.getStringExtra(AppConstants.RESTAURANT_PHONE_NO);
        email = intent.getStringExtra(AppConstants.RESTAURANT_EMAIL);
        restaurantID = intent.getIntExtra(AppConstants.RESTAURANT_ID, 0);

        restaurantNameView.setText(restaurantName);
        restaurantAddressView.setText(restaurantAddress);

        pref = getSharedPreferences(AppConstants.ACCESS, MODE_PRIVATE);

        //Call the restaurant
        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + call));
                    startActivity(intent);
                } catch (SecurityException e) {
                    Toast.makeText(BookingPage.this, "Sorry this service is unavailable", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Email the restaurant
        findViewById(R.id.mail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode(email) +
                        "?subject=" + Uri.encode("Restaurant Hub User")+
                        "&body=" + Uri.encode("\n\n\n\nSent from Restaurant Hub App");
                Uri uri = Uri.parse(uriText);

                send.setData(uri);
                startActivity(Intent.createChooser(send, "Send mail to Restaurant"));
            }
        });

        //Submit order
        submitButton = (LinearLayout) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry<String, String> entry : foodItem.entrySet()) {
                    selectedFoodItem += entry.getValue()+",";
                }
                if (!selectedFoodItem.isEmpty()) {
                    network(pref.getString(AppConstants.USER_ID, ""), String.valueOf(restaurantID), selectedFoodItem, String.valueOf(total));
                    //foodItem.clear();
                    selectedFoodItem = "";
                }else {
                    Toast.makeText(BookingPage.this, "You need to select food item", Toast.LENGTH_LONG).show();
                }
            }
        });

        mRequestSingleton = RequestSingleton.getInstance(this);
        mRequestQueue = mRequestSingleton.getRequestQueue();


        network(restaurantID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConstants.RESTAURANT_NAME, restaurantName);
        outState.putString(AppConstants.RESTAURANT_ADDRESS, restaurantAddress);
        outState.putString(AppConstants.RESTAURANT_PHONE_NO, call);
        outState.putString(AppConstants.RESTAURANT_EMAIL, email);
        outState.putInt(AppConstants.RESTAURANT_ID, restaurantID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            restaurantAddressView.setText(savedInstanceState.getString(AppConstants.RESTAURANT_ADDRESS));
            restaurantNameView.setText(savedInstanceState.getString(AppConstants.RESTAURANT_NAME));

            call = savedInstanceState.getString(AppConstants.RESTAURANT_PHONE_NO);
            email = savedInstanceState.getString(AppConstants.RESTAURANT_EMAIL);
            restaurantID = savedInstanceState.getInt(AppConstants.RESTAURANT_ID, 0);
        }
    }

    private void network(int restaurantID) {
        HashMap<String, String> param = new HashMap<>();
        param.put("merchantID", String.valueOf(restaurantID));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RequestSingleton.getEncodeURL(ConnectionURL.MARKET_URL, param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Gson gson = new Gson();
                Transaction transaction = gson.fromJson(jsonObject.toString(), Transaction.class);
                if (!transaction.getTransactions().isEmpty()) {
                    submitButton.setVisibility(View.VISIBLE);
                }
                for (Transaction t : transaction.getTransactions()) {
                    autoInflate(t);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    private void network(String userid, String merchantid, String foods, String price) {
        final ProgressDialog progressDialog = new ProgressDialog(BookingPage.this);
        progressDialog.setMessage("Please wait loading....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("userID", userid);
        param.put("merchantID", merchantid);
        param.put("foodName", foods);
        param.put("totalPrice", price);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RequestSingleton.getEncodeURL(ConnectionURL.PAYMENT_URL, param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                Gson gson = new Gson();
                ServerResponse response = gson.fromJson(jsonObject.toString(), ServerResponse.class);
                if (response.getResponseMessage().equals("ORDER_ERROR")) {
                    Toast.makeText(BookingPage.this,  "Oops! an error just occurred, please try again", Toast.LENGTH_LONG).show();
                }else if (response.getResponseMessage().equals("INSUFFICIENT_FUND")) {
                    Toast.makeText(BookingPage.this,  "You have insufficient fund to perform this transaction", Toast.LENGTH_LONG).show();
                }else {
                    //Success
                    Intent intent = new Intent(BookingPage.this, OrderSuccessPage.class);
                    intent.putExtra("TOTAL_PRICE", total);
                    intent.putExtra(AppConstants.RESTAURANT_NAME, restaurantName);
                    intent.putExtra("TRANSACTION_ID", response.getResponseMessage());
                    intent.putExtra("FOODS", selectedFoodItem);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(BookingPage.this, "Oops a fatal error just occurred", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    private void autoInflate(final Transaction transaction) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.order_item_inflate_layout, null);
        TextView foodView = (TextView) row.findViewById(R.id.food_name);
        TextView foodDescription = (TextView) row.findViewById(R.id.food_description);
        TextView foodPrice = (TextView) row.findViewById(R.id.food_price);

        CheckBox checkBox = (CheckBox) row.findViewById(R.id.food_checked);

        foodView.setText(transaction.getFoodName());
        foodDescription.setText(transaction.getFoodDecription());
        foodPrice.setText("$" + String.valueOf(transaction.getFoodPrice()));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (total >= 0.0) {
                    if (isChecked) {
                        foodItem.put(transaction.getFoodName(), transaction.getFoodName());
                        total = total + transaction.getFoodPrice();
                    } else {
                        foodItem.remove(transaction.getFoodName());
                        total = total - transaction.getFoodPrice();
                    }
                    Toast.makeText(BookingPage.this, "Total Price: " + total, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BookingPage.this, "Not allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        orderContainer.addView(row);
    }
}
