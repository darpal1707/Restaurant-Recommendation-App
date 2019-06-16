package com.darpal.foodlabrinthnew.NavBarPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Handler.BasedOnLikesAdapter;
import com.darpal.foodlabrinthnew.Handler.SearchFragmentAdapter;
import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.R;
import com.darpal.foodlabrinthnew.Util.LikesUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.darpal.foodlabrinthnew.NavBarPages.HomeFragment.likesHours;

public class SearchResultDisplayActivity extends AppCompatActivity {

    private List<BasedOnLikes> likesDetailsList;
    SearchFragmentAdapter likesAdapter;
    public static String business_id;
    public static String categories;
    public static String name;
    public static String address;
    public static String latitude;
    public static String longitude;
    public static String city;
    public static String state;
    public static String hours;
    private RecyclerView likes_recycler;
    ShimmerFrameLayout search;

    public static String value;
    public static int[] searchImage = {R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine, R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_display);

        search = (ShimmerFrameLayout) findViewById(R.id.searchShimmer);
        search.startShimmerAnimation();

        LikesUtil.searchId.clear();
        LikesUtil.searchName.clear();
        LikesUtil.searchAddress.clear();
        LikesUtil.searchCuisine.clear();
        LikesUtil.searchCity.clear();
        LikesUtil.searchState.clear();
        LikesUtil.searchLat.clear();
        LikesUtil.searchLong.clear();
        LikesUtil.searchHours.clear();

        likes_recycler = findViewById(R.id.likes_detail_recyclerview);
        Intent intent = getIntent();
        value = intent.getStringExtra("cuisine_value");
        likesDetailsList = new ArrayList<BasedOnLikes>();
        showLikesDetailData();
    }

    private void showLikesDetailData() {
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

                    if(categories.contains(value)) {
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

                        likesDetailsList.add(basedOnLikes);
                    }
                }
                likesAdapter = new SearchFragmentAdapter(SearchResultDisplayActivity.this, likesDetailsList, searchImage);
                likes_recycler.setLayoutManager(new LinearLayoutManager(SearchResultDisplayActivity.this, LinearLayoutManager.VERTICAL, false));
                likes_recycler.setAdapter(likesAdapter);

                search.stopShimmerAnimation();
                search.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchResultDisplayActivity.this, "Didn't get any data in Datasnapshot", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
