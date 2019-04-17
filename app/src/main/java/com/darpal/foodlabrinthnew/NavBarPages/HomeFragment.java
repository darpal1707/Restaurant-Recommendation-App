package com.darpal.foodlabrinthnew.NavBarPages;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Handler.BasedOnLikesAdapter;
import com.darpal.foodlabrinthnew.Handler.TrendingAdapter;
import com.darpal.foodlabrinthnew.MapView.MapActivity;
import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.Model.Trending;
import com.darpal.foodlabrinthnew.NotDecided.NotDecidedActivity;
import com.darpal.foodlabrinthnew.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public HomeFragment() {
        // Required empty public constructor
    }

    Button notDecided;
    private RecyclerView trending_recycler;
    private RecyclerView likes_recycler;

    private List<Trending> trendingList;
    TrendingAdapter trendingAdapter;

    private List<BasedOnLikes> likesList;
    BasedOnLikesAdapter likesAdapter;

    public static String business_id;
    public static String name;
    public static String address;
    public static String latitude;
    public static String longitude;
    public static String city;
    public static String state;


    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.mymenu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(),MapActivity.class);
                startActivity(intent);
                return true;
            }
        });
        notDecided = (Button) view.findViewById(R.id.notDecided_btn);
        trending_recycler = (RecyclerView) view.findViewById(R.id.trending_recyclerview);
        likes_recycler = (RecyclerView) view.findViewById(R.id.likes_recyclerview);

        trendingList = new ArrayList<>();
        trendingAdapter = new TrendingAdapter(getContext(),trendingList);
        showTrendingData();

        likesList = new ArrayList<>();
        likesAdapter = new BasedOnLikesAdapter(getContext(),likesList);
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
                .limitToLast(5);

        trendingPostQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.e("Trending Test", String.valueOf(dataSnapshot1));
                    business_id = String.valueOf(dataSnapshot1.child("business_id").getValue());
                    name = String.valueOf(dataSnapshot1.child("name").getValue());
                    address = String.valueOf(dataSnapshot1.child("address").getValue());
                    latitude =  String.valueOf(dataSnapshot1.child("latitude").getValue());
                    longitude = String.valueOf(dataSnapshot1.child("longitude").getValue());
                    city = String.valueOf(dataSnapshot1.child("city").getValue());
                    state = String.valueOf(dataSnapshot1.child("state").getValue());

                    Trending trending = new Trending(String.valueOf(dataSnapshot1.child("name").getValue()),
                            String.valueOf(dataSnapshot1.child("address").getValue()),
                            String.valueOf(dataSnapshot1.child("review_count").getValue()),
                            String.valueOf(dataSnapshot1.child("city").getValue()),
                            String.valueOf(dataSnapshot1.child("state").getValue()),
                            String.valueOf(dataSnapshot1.child("latitude").getValue()),
                            String.valueOf(dataSnapshot1.child("longitude").getValue()),
                            String.valueOf(dataSnapshot1.child("business_id").getValue()));
                    trendingList.add(trending);
                }
                trending_recycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
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
                .limitToFirst(10);

        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.e("Likes Test", String.valueOf(ds));
                    business_id = String.valueOf(ds.child("business_id").getValue());
                    name = String.valueOf(ds.child("name").getValue());
                    address = String.valueOf(ds.child("address").getValue());
                    latitude =  String.valueOf(ds.child("latitude").getValue());
                    longitude = String.valueOf(ds.child("longitude").getValue());
                    city = String.valueOf(ds.child("city").getValue());
                    state = String.valueOf(ds.child("state").getValue());

                    BasedOnLikes basedOnLikes = new BasedOnLikes(String.valueOf(ds.child("name").getValue()),
                            String.valueOf(ds.child("address").getValue()),
                            String.valueOf(ds.child("review_count").getValue()),
                            String.valueOf(ds.child("city").getValue()),
                            String.valueOf(ds.child("state").getValue()));
                    likesList.add(basedOnLikes);
                }

                likes_recycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                likes_recycler.setAdapter(likesAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Didn't get any data in Datasnapshot", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
