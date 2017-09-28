package com.ahyoxsoft.restauranthub.main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import com.ahyoxsoft.restauranthub.R;
import com.ahyoxsoft.restauranthub.utility.AppConstants;
import com.ahyoxsoft.restauranthub.utility.ConnectionURL;
import com.ahyoxsoft.restauranthub.utility.RequestSingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class FundAccount extends AppCompatActivity {
    TextView selectedMonth, selectedYear;
    private RequestSingleton mRequestSingleton;
    private RequestQueue mRequestQueue;
    private EditText amountField, cardNoField, cvvField;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectedMonth = (TextView) findViewById(R.id.selected_month);
        findViewById(R.id.month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FundAccount.this);
                builder.setSingleChoiceItems(getResources().getStringArray(R.array.month), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        selectedMonth.setText(getResources().getStringArray(R.array.month)[which]);
                    }
                });
                builder.create().show();
            }
        });

        selectedYear = (TextView) findViewById(R.id.selected_year);
        findViewById(R.id.year).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(FundAccount.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedYear.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
                    }
                }, 2016, 11, 01);
                dialog.show();
            }
        });

        amountField = (EditText) findViewById(R.id.amount);
        cardNoField = (EditText) findViewById(R.id.card_no);
        cvvField = (EditText) findViewById(R.id.cvv);

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
        boolean cancel = false;
        View view = null;
        if(TextUtils.isEmpty(amountField.getText().toString())) {
            cancel = true;
            view = amountField;
            amountField.setError("Field can't be empty");
        }else if(TextUtils.isEmpty(cardNoField.getText().toString())) {
            cancel = true;
            view = cardNoField;
            cardNoField.setError("Field can't be empty");
        } else if(TextUtils.isEmpty(cvvField.getText().toString())) {
            cancel = true;
            view = cvvField;
            cvvField.setError("Field can't be empty");
        }
        if (cancel) {
            view.requestFocus();
        }else {
            network(pref.getString(AppConstants.USER_ID, ""), amountField.getText().toString(), cardNoField.getText().toString(), selectedMonth.getText().toString(), selectedYear.getText().toString(), cvvField.getText().toString());
        }
    }

    /**
     *
     * @param userID
     * @param amount
     * @param cardNo
     * @param cardExpMonth
     * @param cardExpYear
     * @param cvv
     */
    private void network(final String userID, final String amount, final String cardNo, final String cardExpMonth, final String cardExpYear, final String cvv) {
        final ProgressDialog progressDialog = new ProgressDialog(FundAccount.this);
        progressDialog.setMessage("Please wait loading....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, ConnectionURL.FUND_ACCOUNT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (s.equals("SUCCESS")) {
                    startActivity(new Intent(FundAccount.this, SuccessPage.class));
                }else if(s.equals("BANK_ERROR")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FundAccount.this);
                    builder.setMessage("Transaction was unsuccessful, please try again");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }else {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("userid", userID);
                param.put("amount", amount);
                param.put("cardno", cardNo);
                param.put("expmth", cardExpMonth);
                param.put("expyear", cardExpYear);
                param.put("cvv", cvv);
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("Content-Type","application/x-www-form-urlencoded");
                return param;
            }
        };
        mRequestQueue.add(request);
    }
}
