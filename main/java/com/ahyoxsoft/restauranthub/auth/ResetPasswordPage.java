package com.ahyoxsoft.restauranthub.auth;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ahyoxsoft.restauranthub.R;
import com.ahyoxsoft.restauranthub.main.Dashboard;
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

public class ResetPasswordPage extends AppCompatActivity {
    private EditText usernameView, oldPasswordView, newPasswordView, confirmPasswordView;
    private RequestSingleton mRequestSingleton;
    private RequestQueue mRequestQueue;
    private String username;
    private int userID;
    private  SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        usernameView = (EditText) findViewById(R.id.username);
        oldPasswordView = (EditText) findViewById(R.id.old_password);
        newPasswordView = (EditText) findViewById(R.id.new_password);
        confirmPasswordView = (EditText) findViewById(R.id.confirm_password);

        Intent intent = getIntent();
        username = intent.getStringExtra(AppConstants.USERNAME);
        usernameView.setText(username);
        userID = intent.getIntExtra(AppConstants.USER_ID, 0);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        pref = getSharedPreferences(AppConstants.ACCESS, MODE_PRIVATE);

        mRequestSingleton = RequestSingleton.getInstance(this);
        mRequestQueue = mRequestSingleton.getRequestQueue();
    }

    private void validate() {
        String confirmPassword = confirmPasswordView.getText().toString();
        String oldPassword = oldPasswordView.getText().toString();
        String newPassword = newPasswordView.getText().toString();

        boolean cancel = false;
        View view = null;

        if (!confirmPassword.equals(newPassword)) {
            view = confirmPasswordView;
            cancel = true;
            confirmPasswordView.setError("Password must match with new pasword");
        }

        if (cancel) {
            view.requestFocus();
        }else {
            network(username, oldPassword, newPassword);
        }
    }

    private void network(String username, String oldPassword, String newPassword) {
        final ProgressDialog progressDialog = new ProgressDialog(ResetPasswordPage.this);
        progressDialog.setMessage("Please wait loading....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("username", username);
        param.put("oldPassword", oldPassword);
        param.put("newPassword", newPassword);
        param.put("userID", String.valueOf(userID));

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                RequestSingleton.getEncodeURL(ConnectionURL.RESET_PASSWORD_URL, param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Gson gson = new Gson();
                User user = gson.fromJson(jsonObject.toString(), User.class);
                if (user.getResponse() == AppConstants.SUCCESS) {
                    //Store UserID and login in preference
                    showInfoDialog(user.getUserID());
                }else if (user.getResponse() == AppConstants.ERROR) {
                    Toast.makeText(ResetPasswordPage.this, "Oops!!! error in connection. Please try again", Toast.LENGTH_LONG).show();
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

    private void showInfoDialog(final String userID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Password Reset was successful");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(AppConstants.LOGIN, true);
                editor.putString(AppConstants.USER_ID, userID);
                editor.apply();
                Intent intent = new Intent(ResetPasswordPage.this, Dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.create().show();
    }
}
