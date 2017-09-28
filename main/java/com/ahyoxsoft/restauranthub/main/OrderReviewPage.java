package com.ahyoxsoft.restauranthub.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ahyoxsoft.restauranthub.R;
import com.ahyoxsoft.restauranthub.utility.AppConstants;
import com.ahyoxsoft.restauranthub.utility.ConnectionURL;
import com.ahyoxsoft.restauranthub.utility.RequestSingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderReviewPage extends AppCompatActivity {
    private double price;
    private int merchantID;
    private RequestSingleton mRequestSingleton;
    private RequestQueue mRequestQueue;
    private String selectedFoodItem = "";
    private String response;
    private String restaurantName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_review_page);

        TextView priceView = (TextView) findViewById(R.id.price);
        price = getIntent().getDoubleExtra("PRICE", 0.0);
        selectedFoodItem = getIntent().getStringExtra("FOODS");
        merchantID = getIntent().getIntExtra("MERCHANT_ID", 0);
        restaurantName = getIntent().getStringExtra(AppConstants.RESTAURANT_NAME);

        priceView.setText("$" + price);

        findViewById(R.id.order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //There should be an if statement to check if transaction was successful before moving to next activity
                String transactionID = paymentProcessor();
                if (transactionID.contains("HUB-")) {
                    Intent intent = new Intent(OrderReviewPage.this, OrderSuccessPage.class);
                    intent.putExtra("TRANSACTION_ID", transactionID);
                    intent.putExtra("TOTAL_PRICE", price);
                    intent.putExtra("FOODS", selectedFoodItem);
                    intent.putExtra(AppConstants.RESTAURANT_NAME, restaurantName);
                    startActivity(intent);
                }else if (transactionID.equals("ORDER_ERROR")) {
                    Toast.makeText(OrderReviewPage.this, "", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(OrderReviewPage.this, "Oops!!! Please try again", Toast.LENGTH_LONG).show();
                }
            }
        });

        mRequestSingleton = RequestSingleton.getInstance(this);
        mRequestQueue = mRequestSingleton.getRequestQueue();

        network();
    }

    private String paymentProcessor() {
        HashMap<String, String> param = new HashMap<>();
        param.put("userID", String.valueOf(getSharedPreferences(AppConstants.ACCESS, MODE_PRIVATE).getInt(AppConstants.USER_ID, 0)));
        param.put("merchantID", String.valueOf(merchantID));
        param.put("foodName", selectedFoodItem);
        param.put("totalPrice", String.valueOf(price));
        param.put("", "");
        param.put("", "");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, RequestSingleton.getEncodeURL(ConnectionURL.PAYMENT_URL, param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                response = jsonObject.toString();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                response = "ERROR";
            }
        });

        mRequestQueue.add(jsonObjectRequest);

        return "HUB-65421336-aa2b-48b8-8250-614137fe859d";//response;
    }

    private void network() {
        HashMap<String, String> param = new HashMap<>();
        param.put("p", "linkToken");
        param.put("v_merchant_id", "demo");
        param.put("memo", "Food Ordering Payment");
        param.put("total", String.valueOf(price));
        param.put("merchant_ref", "hjuygg");
        param.put("recurrent", "false");
        param.put("interval", "0");
        param.put("notify_url", "http://www.notify.com");
        param.put("success_url", "http://www.success.com");
        param.put("fail_url", "http://www.fail.com");
        param.put("developer_code", "569f922113f8f");
        param.put("cur", "NGN");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RequestSingleton.getEncodeURL(ConnectionURL.PAYMENT_URL, param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("Response", jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Error", volleyError.getLocalizedMessage());
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }
}
