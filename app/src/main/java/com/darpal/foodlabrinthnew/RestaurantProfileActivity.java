package com.darpal.foodlabrinthnew;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Handler.ReviewsAdapter;
import com.darpal.foodlabrinthnew.Model.Reviews;
import com.darpal.foodlabrinthnew.NavBarPages.SearchResultDisplayActivity;
import com.darpal.foodlabrinthnew.Util.LikesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RestaurantProfileActivity extends AppCompatActivity {

    MapView mapView;
    GoogleMap map;
    TextView restName, addressLocation, restCity, resState,
            resCuisine, reshours, mapDirectionLabel;
    TextView categories, openHours, reviews;
    double lat, longi;
    FloatingActionButton mapLink;
    Button share, addreview, like;
    RecyclerView reviewsRecycler;
    private List<Reviews> reviewsList;
    ReviewsAdapter reviewsAdapter;

    public static String reviewText;
    public static String date;
    public static String business_id;
    public static String userid;
    public static String reviewId;
    public static String resid;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mReference;


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
        addreview = (Button) findViewById(R.id.addReview);
        like = (Button) findViewById(R.id.likeBtn);
        categories = (TextView) findViewById(R.id.CategoriesTitle);
        openHours = (TextView) findViewById(R.id.OpenHoursTitle);
        reviews = (TextView) findViewById(R.id.ReviewsTitle);
        mapDirectionLabel = (TextView) findViewById(R.id.mapDirectionLabel);
        mapLink = (FloatingActionButton) findViewById(R.id.mapbutton);
        share = (Button) findViewById(R.id.ShareBtn);


        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Montserrat-Medium.ttf");
        restName.setTypeface(custom_font);
        resCuisine.setTypeface(custom_font);
        addressLocation.setTypeface(custom_font);
        restCity.setTypeface(custom_font);
        resState.setTypeface(custom_font);
        addreview.setTypeface(custom_font);
        share.setTypeface(custom_font);
        like.setTypeface(custom_font);
        categories.setTypeface(custom_font);
        openHours.setTypeface(custom_font);
        reviews.setTypeface(custom_font);
        reshours.setTypeface(custom_font);
        mapDirectionLabel.setTypeface(custom_font);


        reviewsRecycler = (RecyclerView) findViewById(R.id.reviewsRecycler);
        reviewsList = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(this, reviewsList);
        mReference = FirebaseDatabase.getInstance().getReference();
        showReviews();

        Intent intent = getIntent();
        if (intent != null) {
            resid = intent.getStringExtra("business_id");
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

        final String cuisineClicked = SearchResultDisplayActivity.value;

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("cuisinePref", Context.MODE_PRIVATE);
                Toast.makeText(RestaurantProfileActivity.this, "Cuisine clicked is " + cuisineClicked, Toast.LENGTH_SHORT).show();
                LikesUtil.likedCuisine.add(cuisineClicked);
                Gson gson = new Gson();
                String json = gson.toJson(LikesUtil.likedCuisine);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("cuisine", json);
                editor.apply();
            }
        });


        final String mapUrl = "http://maps.google.com/maps?&daddr=" + lat + "," + longi;
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
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

        addreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    String email = user.getEmail();
                    String Uid = user.getUid();

                    LayoutInflater li = LayoutInflater.from(RestaurantProfileActivity.this);
                    View promptsView = li.inflate(R.layout.fragment_full_screen_dialog, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            RestaurantProfileActivity.this);
                    alertDialogBuilder.setView(promptsView);
                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.review_dialog);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            String userid = user.getUid();
                                            String reviewText = userInput.getText().toString();
                                            Date currentTime = Calendar.getInstance().getTime();
                                            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                                            String formatDate = dateFormat.format(currentTime);
                                            //Toast.makeText(RestaurantProfileActivity.this, "date" + formatDate, Toast.LENGTH_SHORT).show();

                                            Reviews reviews = new Reviews(resid, formatDate, reviewText, userid);
                                            mReference.child("reviews").push().setValue(reviews);
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    // No user is signed in
                    Snackbar snackbar = Snackbar.make(v,"Login to submit a review",Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
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

    private void showReviews() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query trendingPostQuery = reference.child("reviews")
                .limitToLast(20);

        trendingPostQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.e("Reviews Test", String.valueOf(dataSnapshot1));
                    reviewText = String.valueOf(dataSnapshot1.child("text").getValue());
                    date = String.valueOf(dataSnapshot1.child("date").getValue());
                    business_id = String.valueOf(dataSnapshot1.child("business_id").getValue());
                    reviewId = String.valueOf(dataSnapshot1.child("review_id").getValue());
                    userid = String.valueOf(dataSnapshot1.child("user_id").getValue());

                    if (business_id.contains(resid)) {
                        Reviews review = new Reviews(String.valueOf(dataSnapshot1.child("business_id").getValue()),
                                String.valueOf(dataSnapshot1.child("date").getValue()),
                                String.valueOf(dataSnapshot1.child("text").getValue()),
                                String.valueOf(dataSnapshot1.child("user_id").getValue()));

                        reviewsList.add(review);
                    }

                }
                reviewsAdapter = new ReviewsAdapter(RestaurantProfileActivity.this, reviewsList);
                reviewsRecycler.setLayoutManager(new LinearLayoutManager(RestaurantProfileActivity.this, LinearLayoutManager.VERTICAL, false));
                reviewsRecycler.setAdapter(reviewsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RestaurantProfileActivity.this, "Didn't get any data in Datasnapshot", Toast.LENGTH_SHORT).show();
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
