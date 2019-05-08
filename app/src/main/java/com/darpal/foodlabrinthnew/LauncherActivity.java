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


import com.romellfudi.permission.PermissionService;

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

        new PermissionService(this).request(
                new String[]{ACCESS_FINE_LOCATION, CAMERA, ACCESS_COARSE_LOCATION,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE},
                callback);

        /*new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
            }
        }, SPLASH_TIME);*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        callback.handler(permissions, grantResults);
    }

    private PermissionService.Callback callback = new PermissionService.Callback() {
        @Override
        public void onRefuse(ArrayList<String> RefusePermissions) {
            // todo
        }

        @Override
        public void onFinally() {
            Intent mainIntent = new Intent(LauncherActivity.this,MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    };
}
