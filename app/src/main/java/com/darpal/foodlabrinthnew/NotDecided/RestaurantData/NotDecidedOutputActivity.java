package com.darpal.foodlabrinthnew.NotDecided.RestaurantData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Handler.BasedOnLikesAdapter;
import com.darpal.foodlabrinthnew.Handler.NotDecidedAdapter;
import com.darpal.foodlabrinthnew.Handler.SearchFragmentAdapter;
import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.NavBarPages.SearchResultDisplayActivity;
import com.darpal.foodlabrinthnew.R;
import com.darpal.foodlabrinthnew.Util.LikesUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class NotDecidedOutputActivity extends AppCompatActivity {

    RecyclerView notDecided_recycler;
    List<BasedOnLikes> notDecidedList;
    NotDecidedAdapter likesAdapter;
    public static String business_id;
    public static String categories;
    public static String name;
    public static String address;
    public static String latitude;
    public static String longitude;
    public static String city;
    public static String state;
    public static String hours;
    public String cuisineTransfered;

    public static int[] likesImage = {R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_decided_output);

        LikesUtil.searchId.clear();
        LikesUtil.searchName.clear();
        LikesUtil.searchAddress.clear();
        LikesUtil.searchCuisine.clear();
        LikesUtil.searchCity.clear();
        LikesUtil.searchState.clear();
        LikesUtil.searchLat.clear();
        LikesUtil.searchLong.clear();
        LikesUtil.searchHours.clear();

        notDecided_recycler = (RecyclerView) findViewById(R.id.notDecidedData);
        Intent intent = getIntent();
        cuisineTransfered = intent.getStringExtra("cuisine");
        Toast.makeText(this, "Value is " + cuisineTransfered, Toast.LENGTH_SHORT).show();
        notDecidedList = new ArrayList<BasedOnLikes>();
        showNotDecidedData();
    }

    private void showNotDecidedData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query recentPostsQuery = databaseReference.child("business")
                .limitToFirst(300);

        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.e("notDecided", String.valueOf(ds));
                    business_id = String.valueOf(ds.child("business_id").getValue());
                    categories = String.valueOf(ds.child("categories").getValue());
                    name = String.valueOf(ds.child("name").getValue());
                    address = String.valueOf(ds.child("address").getValue());
                    latitude = String.valueOf(ds.child("latitude").getValue());
                    longitude = String.valueOf(ds.child("longitude").getValue());
                    city = String.valueOf(ds.child("city").getValue());
                    state = String.valueOf(ds.child("state").getValue());
                    hours = String.valueOf(ds.child("hours").getValue());

                    if (categories.contains(cuisineTransfered)) {
                        LikesUtil.searchId.add(business_id);
                        LikesUtil.searchName.add(name);
                        LikesUtil.searchCuisine.add(categories);
                        LikesUtil.searchAddress.add(address);
                        LikesUtil.searchCity.add(city);
                        LikesUtil.searchState.add(state);
                        LikesUtil.searchLat.add(latitude);
                        LikesUtil.searchLong.add(longitude);
                        LikesUtil.searchHours.add(hours);

                        BasedOnLikes basedOnLikes = new BasedOnLikes(String.valueOf(ds.child("name").getValue()),
                                String.valueOf(ds.child("address").getValue()),
                                String.valueOf(ds.child("review_count").getValue()),
                                String.valueOf(ds.child("city").getValue()),
                                String.valueOf(ds.child("state").getValue()),
                                String.valueOf(ds.child("categories").getValue()),
                                String.valueOf(ds.child("hours").getValue()));

                        notDecidedList.add(basedOnLikes);
                    }

                   /* else{
                        Toast.makeText(SearchResultDisplayActivity.this, "Something went wrong! Please Search Again!", Toast.LENGTH_SHORT).show();
                    }*/
                }
                likesAdapter = new NotDecidedAdapter(notDecidedList,NotDecidedOutputActivity.this, likesImage);
                notDecided_recycler.setLayoutManager(new LinearLayoutManager(NotDecidedOutputActivity.this, LinearLayoutManager.VERTICAL, false));
                notDecided_recycler.setAdapter(likesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NotDecidedOutputActivity.this, "Didn't get any data in Datasnapshot", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
