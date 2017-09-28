package com.ahyoxsoft.restauranthub.auth;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.ahyoxsoft.restauranthub.R;
import com.ahyoxsoft.restauranthub.utility.AppConstants;
import com.ahyoxsoft.restauranthub.utility.ConnectionURL;
import com.ahyoxsoft.restauranthub.utility.RequestSingleton;
import com.ahyoxsoft.restauranthub.utility.User;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;
import java.util.HashMap;

public class ForgotPassword extends AppCompatActivity {
    private RequestSingleton mRequestSingleton;
    private RequestQueue mRequestQueue;
    private EditText usernameView, emailView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        usernameView = (EditText) findViewById(R.id.username);
        emailView = (EditText) findViewById(R.id.email);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                network(usernameView.getText().toString(), emailView.getText().toString());
            }
        });

        mRequestSingleton = RequestSingleton.getInstance(this);
        mRequestQueue = mRequestSingleton.getRequestQueue();
    }

    private void network(final String username, String email) {
        final ProgressDialog progressDialog = new ProgressDialog(ForgotPassword.this);
        progressDialog.setMessage("Please wait loading....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("username", username);
        param.put("email", email);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                RequestSingleton.getEncodeURL(ConnectionURL.FORGET_PASSORD_URL, param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Gson gson = new Gson();
                User user = gson.fromJson(jsonObject.toString(), User.class);
                if (user.getResponse() == AppConstants.SUCCESS) {
                    Intent intent = new Intent(ForgotPassword.this, ResetPasswordPage.class);
                    intent.putExtra(AppConstants.USERNAME, username);
                    intent.putExtra(AppConstants.USER_ID, user.getUserID());
                    startActivity(intent);
                }else if (user.getResponse() == AppConstants.ERROR) {
                    Toast.makeText(ForgotPassword.this, "Oops!!! error in connection. Please try again", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
            }
        });

        mRequestQueue.add(objectRequest);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("An email has just been sent to your email. Kindly follow the step to reset your password");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(ForgotPassword.this, LoginPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.create().show();
    }
}
