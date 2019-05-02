package com.darpal.foodlabrinthnew;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.darpal.foodlabrinthnew.Model.Trending;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class RestaurantProfileActivity extends AppCompatActivity {

    MapView mapView;
    GoogleMap map;
    TextView restName,addressLocation,restCity,resState,
            resCuisine, reshours, resReviews, reviewsUseful,
            reviewsFunny;
    double lat, longi;
    FloatingActionButton mapLink;
    Button share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);

        restName = (TextView) findViewById(R.id.tvName);
        resCuisine = (TextView) findViewById(R.id.cuisines);
        addressLocation = (TextView) findViewById(R.id.tvEducation);
        restCity = (TextView) findViewById(R.id.restCity);
        resState = (TextView) findViewById(R.id.resState);
        reshours = (TextView) findViewById(R.id.hours_time);
        resReviews = (TextView) findViewById(R.id.review_text);
        reviewsUseful = (TextView) findViewById(R.id.usefulValue);
        reviewsFunny = (TextView) findViewById(R.id.funnyValue);
        mapLink = (FloatingActionButton) findViewById(R.id.mapbutton);
        share = (Button) findViewById(R.id.ShareBtn);

        Intent intent = getIntent();
        if(intent!=null){
            String business_id = intent.getStringExtra("business_id");
            String name = intent.getStringExtra("name");
            String address = intent.getStringExtra("address");
            String city = intent.getStringExtra("city");
            String state = intent.getStringExtra("state");
            String cuisine = intent.getStringExtra("cuisine");
            lat = Double.parseDouble(intent.getStringExtra("lat"));
            longi = Double.parseDouble(intent.getStringExtra("long"));
            String hours = intent.getStringExtra("hours");

            restName.setText(name);
            resCuisine.setText(cuisine);
            addressLocation.setText(address);
            restCity.setText(city);
            resState.setText(state);
            reshours.setText(hours);
        }
        final String mapUrl = "http://maps.google.com/maps?&daddr="+lat+","+longi;
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, "Food Labrinth");
                share.putExtra(Intent.EXTRA_TEXT, "Hey! I found this restaurant on Food Labrinth. Checkout the place! " + mapUrl);

                startActivity(Intent.createChooser(share, "Share link!"));
            }
        });
        mapLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(mapUrl));
                startActivity(intent);
            }
        });

        mapView = (MapView) findViewById(R.id.res_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                map = mMap;
                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(RestaurantProfileActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(RestaurantProfileActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                // For dropping a marker at a point on the Map
                //LatLng sydney = new LatLng(43.6054989743, -79.652288909);
                LatLng sydney = new LatLng(lat, longi);
                mMap.addMarker(new MarkerOptions().position(sydney));

                // For zooming automatically to the location of the marker
                float zoomLevel = (float) 16.0; //This goes up to 21
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
