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
import com.darpal.foodlabrinthnew.R;
import com.darpal.foodlabrinthnew.Util.LikesUtil;
import com.darpal.foodlabrinthnew.Util.TrendingUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public HomeFragment() {
        // Required empty public constructor
    }

    public static int[] trendingImage = {R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine};

    public static int[] likesImage = {R.mipmap.ten,R.mipmap.one,R.mipmap.two,R.mipmap.three,R.mipmap.four,R.mipmap.five,
            R.mipmap.six,R.mipmap.seven,R.mipmap.eight,R.mipmap.nine};

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

    List<String> cusineData;
    String data;

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cuisinePref", Context.MODE_PRIVATE);
        try {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("cuisine", "");
            if (json.isEmpty()) {
                Toast.makeText(getActivity(), " didnt get any data", Toast.LENGTH_SHORT).show();
            } else {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                cusineData = gson.fromJson(json, type);
                data = cusineData.toString();
                data = data.substring(1,data.length()-1);
                Toast.makeText(getActivity(), "cusine liked are " + data, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("exception shared pref", String.valueOf(e));
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

        search = (EditText) view.findViewById(R.id.searchview_homepage);
        notDecided = (Button) view.findViewById(R.id.notDecided_btn);
        trending_recycler = (RecyclerView) view.findViewById(R.id.trending_recyclerview);
        likes_recycler = (RecyclerView) view.findViewById(R.id.likes_recyclerview);

        trendingList = new ArrayList<>();
        trendingAdapter = new TrendingAdapter(getContext(), trendingList, trendingImage);
        showTrendingData();

        likesList = new ArrayList<>();
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
                .limitToFirst(100);

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

                    if(categories.contains(data)) {
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

                }
                likesAdapter = new BasedOnLikesAdapter(getContext(), likesList, likesImage);
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
