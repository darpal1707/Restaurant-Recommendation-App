package com.darpal.foodlabrinthnew;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


import java.util.ArrayList;
import java.util.Arrays;

public class LauncherActivity extends AppCompatActivity {
    int SPLASH_TIME = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        TextView tx = (TextView)findViewById(R.id.appname);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Montserrat-Medium.ttf");
        tx.setTypeface(custom_font);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(LauncherActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_TIME);

    }
}
