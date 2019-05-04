package com.darpal.foodlabrinthnew.NavBarPages;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Handler.BasedOnLikesAdapter;
import com.darpal.foodlabrinthnew.Handler.TrendingAdapter;
import com.darpal.foodlabrinthnew.MainActivity;
import com.darpal.foodlabrinthnew.MapView.MapActivity;
import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.Model.Trending;
import com.darpal.foodlabrinthnew.NotDecided.NotDecidedActivity;
import com.darpal.foodlabrinthnew.ObjectSerializer;
import com.darpal.foodlabrinthnew.R;
import com.darpal.foodlabrinthnew.Util.LikesUtil;
import com.darpal.foodlabrinthnew.Util.TrendingUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public HomeFragment() {
        // Required empty public constructor
    }

    Button notDecided;
    EditText search;
    TextView viewmore;
    private RecyclerView trending_recycler;
    private RecyclerView likes_recycler;

    private List<Trending> trendingList;
    TrendingAdapter trendingAdapter;

    private List<BasedOnLikes> likesList;
    BasedOnLikesAdapter likesAdapter;

    public static String business_id, TrendID;
    public static String categories, TrendCuisine;
    public static String name, Trendname;
    public static String address, Trendaddress;
    public static String latitude, Trendlat;
    public static String longitude, Trendlong;
    public static String city, Trendcity;
    public static String state, Trendstate;
    public static String hours, likesHours;

    int[] myImageList = {R.drawable.american, R.drawable.asian, R.drawable.bbq, R.drawable.chinese,
            R.drawable.coffee_tea, R.drawable.deli, R.drawable.desserts_two, R.drawable.european,
            R.drawable.fast_food, R.drawable.greek, R.drawable.halal, R.drawable.indian,
            R.drawable.italian, R.drawable.jamaican, R.drawable.japanese, R.drawable.korean,
            R.drawable.mediterranean, R.drawable.mexican, R.drawable.salads, R.drawable.spanish,
            R.drawable.sushi, R.drawable.thai, R.drawable.vegan, R.drawable.vegetarian};

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cuisinePref", Context.MODE_PRIVATE);
        try {
            LikesUtil.likedCuisine = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("cuisine", ObjectSerializer.serialize(new ArrayList<String>())));
            Toast.makeText(getActivity(), "Liked value is here " + LikesUtil.likedCuisine, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "something went wrong in home fragment shared pref", Toast.LENGTH_SHORT).show();
        }

        EditText searchHome = (EditText) view.findViewById(R.id.searchview_homepage);
        searchHome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    MainActivity.navigation.setSelectedItemId(R.id.search);
                    SearchFragment searchFragment = new SearchFragment();
                    getFragmentManager().beginTransaction().replace(R.id.login_frame, searchFragment).commit();
                }
            }
        });
        viewmore = (TextView) view.findViewById(R.id.viewmore_likesLabel);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.mymenu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
                return true;
            }
        });

        viewmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LikesListDetailActivity.class);
                startActivity(intent);
            }
        });

        search = (EditText) view.findViewById(R.id.searchview_homepage);

        notDecided = (Button) view.findViewById(R.id.notDecided_btn);
        trending_recycler = (RecyclerView) view.findViewById(R.id.trending_recyclerview);
        likes_recycler = (RecyclerView) view.findViewById(R.id.likes_recyclerview);

        trendingList = new ArrayList<>();
        trendingAdapter = new TrendingAdapter(getContext(), trendingList);
        showTrendingData();

        likesList = new ArrayList<>();
        likesAdapter = new BasedOnLikesAdapter(getContext(), likesList);
        showLikesData();
        notDecided.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotDecidedActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void showTrendingData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query trendingPostQuery = reference.child("business")
                .limitToLast(1);

        trendingPostQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.e("Trending Test", String.valueOf(dataSnapshot1));
                    TrendID = String.valueOf(dataSnapshot1.child("business_id").getValue());
                    Trendname = String.valueOf(dataSnapshot1.child("name").getValue());
                    TrendCuisine = String.valueOf(dataSnapshot1.child("categories").getValue());
                    Trendaddress = String.valueOf(dataSnapshot1.child("address").getValue());
                    Trendlat = String.valueOf(dataSnapshot1.child("latitude").getValue());
                    Trendlong = String.valueOf(dataSnapshot1.child("longitude").getValue());
                    Trendcity = String.valueOf(dataSnapshot1.child("city").getValue());
                    Trendstate = String.valueOf(dataSnapshot1.child("state").getValue());
                    hours = String.valueOf(dataSnapshot1.child("hours").getValue());

                    TrendingUtil.businessIdArraryList.add(TrendID);
                    TrendingUtil.businessNameArrayList.add(Trendname);
                    TrendingUtil.businessCuisineArrayList.add(TrendCuisine);
                    TrendingUtil.businessAddressArrayList.add(Trendaddress);
                    TrendingUtil.businessCityArrayList.add(Trendcity);
                    TrendingUtil.businessStateArrayList.add(Trendstate);
                    TrendingUtil.businessLatArrayList.add(Trendlat);
                    TrendingUtil.businessLongArrayList.add(Trendlong);
                    TrendingUtil.businessHoursArrayList.add(hours);

                    Trending trending = new Trending(String.valueOf(dataSnapshot1.child("name").getValue()),
                            String.valueOf(dataSnapshot1.child("address").getValue()),
                            String.valueOf(dataSnapshot1.child("review_count").getValue()),
                            String.valueOf(dataSnapshot1.child("city").getValue()),
                            String.valueOf(dataSnapshot1.child("state").getValue()),
                            String.valueOf(dataSnapshot1.child("latitude").getValue()),
                            String.valueOf(dataSnapshot1.child("longitude").getValue()),
                            String.valueOf(dataSnapshot1.child("business_id").getValue()),
                            String.valueOf(dataSnapshot1.child("categories").getValue()),
                            String.valueOf(dataSnapshot1.child("hours").getValue()));
                    trendingList.add(trending);
                }
                trending_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                trending_recycler.setAdapter(trendingAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Didn't get any data in Datasnapshot", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLikesData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query recentPostsQuery = databaseReference.child("business")
                .limitToFirst(20);

        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.e("Likes Test", String.valueOf(ds));
                    business_id = String.valueOf(ds.child("business_id").getValue());
                    categories = String.valueOf(ds.child("categories").getValue());
                    name = String.valueOf(ds.child("name").getValue());
                    address = String.valueOf(ds.child("address").getValue());
                    latitude = String.valueOf(ds.child("latitude").getValue());
                    longitude = String.valueOf(ds.child("longitude").getValue());
                    city = String.valueOf(ds.child("city").getValue());
                    state = String.valueOf(ds.child("state").getValue());
                    likesHours = String.valueOf(ds.child("hours").getValue());

                    LikesUtil.businessIdArraryList.add(business_id);
                    LikesUtil.businessNameArrayList.add(name);
                    LikesUtil.businessCuisineArrayList.add(categories);
                    LikesUtil.businessAddressArrayList.add(address);
                    LikesUtil.businessCityArrayList.add(city);
                    LikesUtil.businessStateArrayList.add(state);
                    LikesUtil.businessLatArrayList.add(latitude);
                    LikesUtil.businessLongArrayList.add(longitude);
                    LikesUtil.businessHoursArrayList.add(likesHours);

                    BasedOnLikes basedOnLikes = new BasedOnLikes(String.valueOf(ds.child("name").getValue()),
                            String.valueOf(ds.child("address").getValue()),
                            String.valueOf(ds.child("review_count").getValue()),
                            String.valueOf(ds.child("city").getValue()),
                            String.valueOf(ds.child("state").getValue()),
                            String.valueOf(ds.child("categories").getValue()),
                            String.valueOf(ds.child("hours").getValue()));

                    likesList.add(basedOnLikes);
                }

                likes_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                likes_recycler.setAdapter(likesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Didn't get any data in Datasnapshot", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
