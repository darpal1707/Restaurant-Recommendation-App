package com.darpal.foodlabrinthnew.NavBarPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.darpal.foodlabrinthnew.Handler.BasedOnLikesAdapter;
import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.R;

import java.util.ArrayList;
import java.util.List;

public class SearchResultDisplayActivity extends AppCompatActivity {

    private List<BasedOnLikes> likesDetailsList;
    BasedOnLikesAdapter likesAdapter;
    public static String business_id, TrendID;
    public static String categories, TrendCuisine;
    public static String name, Trendname;
    public static String address, Trendaddress;
    public static String latitude, Trendlat;
    public static String longitude, Trendlong;
    public static String city, Trendcity;
    public static String state, Trendstate;
    private RecyclerView likes_recycler;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_display);

        likes_recycler = findViewById(R.id.likes_detail_recyclerview);
        Intent intent = getIntent();
        value = intent.getStringExtra("cuisine_value");
        //Toast.makeText(this, "transfered value is " + value, Toast.LENGTH_SHORT).show();
        likesDetailsList = new ArrayList<BasedOnLikes>();
        likesAdapter = new BasedOnLikesAdapter(SearchResultDisplayActivity.this, likesDetailsList);
        showLikesDetailData();
    }

    private void showLikesDetailData() {

    }
}
