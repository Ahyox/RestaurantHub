package com.ahyoxsoft.restauranthub.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.ahyoxsoft.restauranthub.R;
import com.ahyoxsoft.restauranthub.main.Dashboard;
import com.ahyoxsoft.restauranthub.utility.AppConstants;
import com.ahyoxsoft.restauranthub.utility.ConnectionURL;
import com.ahyoxsoft.restauranthub.utility.RequestSingleton;
import com.ahyoxsoft.restauranthub.utility.User;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignUpForm extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private EditText usernameView, passwordView, emailView;
    String username, password, email;
    private RequestSingleton mRequestSingleton;
    private RequestQueue mRequestQueue;
    private SharedPreferences pref;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_sign_up_form);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        emailView = (EditText) findViewById(R.id.email);


        //Login page
        findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        //Facebook
        findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpFacebook();
            }
        });

        //Google Plus
        findViewById(R.id.google_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpGooglePlus();
            }
        });

        //Move to Login page when clicked
        findViewById(R.id.login_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpForm.this, LoginPage.class));
            }
        });
        pref = getSharedPreferences(AppConstants.ACCESS, MODE_PRIVATE);

        mRequestSingleton = RequestSingleton.getInstance(this);
        mRequestQueue = mRequestSingleton.getRequestQueue();


        //Google plus initialization
        googlePlusInit();


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                //Log.e("FB Access Token", accessToken);

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {

                            String password = object.getString("id");
                            String name = object.getString("name");
                            String mail = object.getString("email");

                            network(name, mail, password, "2");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SignUpForm.this, "Please try again", Toast.LENGTH_SHORT).show();
                            //Log.e("onComplete Error", e.getLocalizedMessage());
                        }
                    }
                });

                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignUpForm.this, "Facebook Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignUpForm.this, "Oops!!! an error just occurred, try again ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void network(final String username, final String email, final String password, final String medium) {
        final ProgressDialog progressDialog = new ProgressDialog(SignUpForm.this);
        progressDialog.setMessage("Please wait loading....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, ConnectionURL.SIGN_UP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                User user = gson.fromJson(s, User.class);
                if (user.getResponse() == AppConstants.SUCCESS) {
                    //Store UserID and login in preference
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(AppConstants.LOGIN, true);
                    if (medium.equals("1")) {
                        editor.putString(AppConstants.LOGIN_METHOD, AppConstants.APP);
                    } else if (medium.equals("2")) {
                        editor.putString(AppConstants.LOGIN_METHOD, AppConstants.FACEBOOK);
                    }else if (medium.equals("3")) {
                        editor.putString(AppConstants.LOGIN_METHOD, AppConstants.GOOGLE_PLUS);
                    }
                    editor.putString(AppConstants.USERNAME, username);
                    editor.putString(AppConstants.USER_ID, user.getUserID());
                    editor.apply();

                    Intent intent = new Intent(SignUpForm.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if (user.getResponse() == AppConstants.ERROR) {
                    Toast.makeText(SignUpForm.this, "Oops!!! error in connection. Please try again", Toast.LENGTH_LONG).show();
                } else if (user.getResponse() == AppConstants.EMAIL_EXIST) {
                    Toast.makeText(SignUpForm.this, "This email used already exist, check the email and retry", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
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
                param.put("username", username);
                param.put("password", password);
                param.put("email", email);
                param.put("medium", medium);
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /**
     * Validate user credentials before sending to server
     */
    private void validate() {
        username = usernameView.getText().toString();
        password = passwordView.getText().toString();
        email = emailView.getText().toString();

        boolean cancel = false;
        View view = null;

        if(TextUtils.isEmpty(username)) {
            usernameView.setError("Field is required");
            view = usernameView;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            emailView.setError("Field is required");
            view = emailView;
            cancel = true;
        }else if (!email.contains("@")) {
            emailView.setError("Invalid email");
            view = emailView;
            cancel = true;
        }else if (TextUtils.isEmpty(password)) {
            passwordView.setError("Field is required");
            view =  passwordView;
            cancel = true;
        }

        if (!cancel) {
            network(username, email, password, "1");
        }else {
            view.requestFocus();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e("Message", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String username = acct.getDisplayName();
            String mail = acct.getEmail();
            String password =  acct.getId();

            Log.e("User ID", password);
            Log.e("Name", username);
            Log.e("Email", mail);

            network(username, mail, password, "3");
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Oops please try again", Toast.LENGTH_LONG).show();
        }
    }

    private void googlePlusInit() {
        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * Facebook Sign up
     */
    private void signUpFacebook() {
        try {
            LoginManager.getInstance().logInWithReadPermissions(SignUpForm.this, Arrays.asList("public_profile", "email"));
        }catch (Exception e) {
            Toast.makeText(this, "Error just occurred, check your Internet and try again", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Google Plus Sign up
     */
    private void signUpGooglePlus() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
