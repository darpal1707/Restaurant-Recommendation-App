package com.darpal.foodlabrinthnew;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Handler.PhotoDisplayAdapter;
import com.darpal.foodlabrinthnew.Handler.ReviewsAdapter;
import com.darpal.foodlabrinthnew.Model.Reviews;
import com.darpal.foodlabrinthnew.Model.Upload;
import com.darpal.foodlabrinthnew.NavBarPages.ProfileFragment;
import com.darpal.foodlabrinthnew.NavBarPages.SearchResultDisplayActivity;
import com.darpal.foodlabrinthnew.Util.LikesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
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
    public static String resid;
    public static String res_stars;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mReference;
    String stars;

    RecyclerView photos;
    List<Upload> mUploads;
    PhotoDisplayAdapter photoDisplayAdapter;

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


        photos = (RecyclerView) findViewById(R.id.photo_horizontal);
        mUploads = new ArrayList<>();
        showPhotos();

        applyTypeFace();
        reviewsRecycler = (RecyclerView) findViewById(R.id.reviewsRecycler);
        reviewsList = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(RestaurantProfileActivity.this, reviewsList);
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

        //Likes user clicked value
        final String cuisineClicked = SearchResultDisplayActivity.value;
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    SharedPreferences preferences = getSharedPreferences("cuisinePref", Context.MODE_PRIVATE);
                    Toast.makeText(RestaurantProfileActivity.this, "Cuisine clicked is " + cuisineClicked, Toast.LENGTH_SHORT).show();
                    LikesUtil.likedCuisine.add(cuisineClicked);
                    Gson gson = new Gson();
                    String json = gson.toJson(LikesUtil.likedCuisine);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("cuisine", json);
                    editor.apply();
                }
                else {
                    // No user is signed in
                    Snackbar snackbar = Snackbar.make(v, "Please Login to get Custom Recommendations", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        //share button code
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

        //Direction floating action button
        mapLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(mapUrl));
                startActivity(intent);
            }
        });

        //Add review button action
        addreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    String email = user.getEmail();
                    String Uid = user.getUid();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            RestaurantProfileActivity.this);
                    LayoutInflater li = LayoutInflater.from(RestaurantProfileActivity.this);
                    View promptsView = li.inflate(R.layout.fragment_full_screen_dialog, null);
                    alertDialogBuilder.setView(promptsView);
                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.review_dialog);
                    final AppCompatRatingBar ratingBar = (AppCompatRatingBar) promptsView.findViewById(R.id.ratingBar);
                    final Button photo = (Button) promptsView.findViewById(R.id.uploadbtn);
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            stars = String.valueOf(rating);
                        }
                    });
                    photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);
                        }
                    });
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String userid = user.getUid();
                                            String reviewText = userInput.getText().toString();

                                            Date currentTime = Calendar.getInstance().getTime();
                                            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                                            String formatDate = dateFormat.format(currentTime);

                                            Reviews reviews = new Reviews(resid, formatDate, reviewText, userid, stars);
                                            mReference.child("reviews").push().setValue(reviews);
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    // No user is signed in
                    Snackbar snackbar = Snackbar.make(v, "Login to submit a review", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }


            }
        });

        //Google maps display
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

    private void applyTypeFace() {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Medium.ttf");
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
    }

    private void showPhotos() {
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("photos");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                    Log.e("photo url", String.valueOf(postSnapshot));
                }
                photoDisplayAdapter = new PhotoDisplayAdapter(RestaurantProfileActivity.this, mUploads);
                photos.setLayoutManager(new LinearLayoutManager(RestaurantProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
                photos.setAdapter(photoDisplayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RestaurantProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Display reviews based on the restaurant
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
                    userid = String.valueOf(dataSnapshot1.child("userid").getValue());
                    res_stars = String.valueOf(dataSnapshot1.child("stars").getValue());

                    if(business_id.equals(resid)) {
                        //Toast.makeText(RestaurantProfileActivity.this, "business id" + business_id, Toast.LENGTH_SHORT).show();
                        Reviews review = new Reviews(String.valueOf(dataSnapshot1.child("business_id").getValue()),
                                String.valueOf(dataSnapshot1.child("date").getValue()),
                                String.valueOf(dataSnapshot1.child("text").getValue()),
                                String.valueOf(dataSnapshot1.child("userid").getValue()),
                                String.valueOf(dataSnapshot1.child("stars").getValue()));

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

    //Camera function to upload photo clicked to the database
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            final String user_id = user.getUid();
            Toast.makeText(RestaurantProfileActivity.this, "Uploading Now", Toast.LENGTH_SHORT).show();

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBAOS = baos.toByteArray();

            FirebaseApp.initializeApp(RestaurantProfileActivity.this);

            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://foodlabrinthnew.appspot.com/");
            StorageReference imagesRef = storageRef.child("photos" + new Date().getTime());

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference mDatabaseRef = database.getReference("photos");

            final UploadTask uploadTask = imagesRef.putBytes(dataBAOS);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(RestaurantProfileActivity.this, "Sending failed. Please try again", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //mProgressBar.setProgress(0);
                        }
                    }, 500);
                    Toast.makeText(RestaurantProfileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();

                    // Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString());
                    Upload upload = new Upload(downloadUrl.toString(), user_id, resid);
                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(upload);

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    //mProgressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(RestaurantProfileActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
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
