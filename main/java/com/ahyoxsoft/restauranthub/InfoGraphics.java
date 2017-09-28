package com.ahyoxsoft.restauranthub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.ahyoxsoft.restauranthub.auth.LoginPage;
import com.ahyoxsoft.restauranthub.auth.SignUpForm;
import com.ahyoxsoft.restauranthub.main.Dashboard;

/**
Class
**/
public class InfoGraphics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_graphics_layout);

        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoGraphics.this, Dashboard.class));
            }
        });

        findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoGraphics.this, SignUpForm.class));
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoGraphics.this, LoginPage.class));
            }
        });
    }
}
