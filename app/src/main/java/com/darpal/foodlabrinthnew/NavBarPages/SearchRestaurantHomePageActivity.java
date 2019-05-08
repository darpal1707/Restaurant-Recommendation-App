package com.darpal.foodlabrinthnew.NavBarPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Handler.HomeSearchAdapter;
import com.darpal.foodlabrinthnew.Handler.SearchFragmentAdapter;
import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.R;
import com.darpal.foodlabrinthnew.Util.LikesUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.darpal.foodlabrinthnew.NavBarPages.SearchResultDisplayActivity.searchImage;

public class SearchRestaurantHomePageActivity extends AppCompatActivity {

    public static String searchValue;
    private List<BasedOnLikes> searchList;
    HomeSearchAdapter likesAdapter;
    public static String business_id;
    public static String categories;
    public static String name;
    public static String address;
    public static String latitude;
    public static String longitude;
    public static String city;
    public static String state;
    public static String hours;
    RecyclerView searchRecycler;
    ImageView noData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant_home_page);

        LikesUtil.searchId.clear();
        LikesUtil.searchName.clear();
        LikesUtil.searchAddress.clear();
        LikesUtil.searchCuisine.clear();
        LikesUtil.searchCity.clear();
        LikesUtil.searchState.clear();
        LikesUtil.searchLat.clear();
        LikesUtil.searchLong.clear();
        LikesUtil.searchHours.clear();

        noData = (ImageView) findViewById(R.id.noDataImage);
        searchRecycler = (RecyclerView) findViewById(R.id.searchRecycler);
        Intent intent = getIntent();
        searchValue = intent.getStringExtra("searchHomeValue");
        //Toast.makeText(this, "value is " + searchValue, Toast.LENGTH_SHORT).show();
        searchList = new ArrayList<>();
        showSearchData();
    }

    private void showSearchData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query recentPostsQuery = databaseReference.child("business")
                .limitToFirst(400);

        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.e("search", String.valueOf(ds));
                    business_id = String.valueOf(ds.child("business_id").getValue());
                    categories = String.valueOf(ds.child("categories").getValue());
                    name = String.valueOf(ds.child("name").getValue());
                    address = String.valueOf(ds.child("address").getValue());
                    latitude = String.valueOf(ds.child("latitude").getValue());
                    longitude = String.valueOf(ds.child("longitude").getValue());
                    city = String.valueOf(ds.child("city").getValue());
                    state = String.valueOf(ds.child("state").getValue());
                    hours = String.valueOf(ds.child("hours").getValue());

                    if(name.contains(searchValue) || categories.contains(searchValue) || state.contains(searchValue)) {
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

                        searchList.add(basedOnLikes);
                    }
                    /*else {
                        searchRecycler.setVisibility(View.INVISIBLE);
                        noData.setVisibility(View.VISIBLE);
                    }*/
                }
                likesAdapter = new HomeSearchAdapter(searchList,SearchRestaurantHomePageActivity.this, searchImage);
                searchRecycler.setLayoutManager(new LinearLayoutManager(SearchRestaurantHomePageActivity.this, LinearLayoutManager.VERTICAL, false));
                searchRecycler.setAdapter(likesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchRestaurantHomePageActivity.this, "Didn't get any data in Datasnapshot", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
