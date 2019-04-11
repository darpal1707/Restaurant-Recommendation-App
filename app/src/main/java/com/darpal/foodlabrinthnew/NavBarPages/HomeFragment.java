package com.darpal.foodlabrinthnew.NavBarPages;


import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Handler.BasedOnLikesAdapter;
import com.darpal.foodlabrinthnew.Handler.TrendingAdapter;
import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.Model.Trending;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public HomeFragment() {
        // Required empty public constructor
    }

    private RecyclerView trending_recycler;
    private RecyclerView likes_recycler;

    private List<Trending> trendingList;
    TrendingAdapter trendingAdapter;

    private List<BasedOnLikes> likesList;
    BasedOnLikesAdapter likesAdapter;

    DatabaseReference databaseReference;


    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        trending_recycler = (RecyclerView) view.findViewById(R.id.trending_recyclerview);
        likes_recycler = (RecyclerView) view.findViewById(R.id.likes_recyclerview);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("business");

        trendingList = new ArrayList<>();
        trendingAdapter = new TrendingAdapter(getContext(),trendingList);
        showTrendingData();



        likesList = new ArrayList<>();
        likesAdapter = new BasedOnLikesAdapter(getContext(),likesList);
        showLikesData();



        return view;
    }

    private void showTrendingData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query  trendingPostQuery = databaseReference.child("business").limitToFirst(5);

        trendingPostQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.e("Trending Test", String.valueOf(dataSnapshot1));
                    Trending trending = new Trending(String.valueOf(dataSnapshot1.child("name").getValue()),
                            String.valueOf(dataSnapshot1.child("address").getValue()),
                            String.valueOf(dataSnapshot1.child("review_count").getValue()),
                            String.valueOf(dataSnapshot1.child("city").getValue()),
                            String.valueOf(dataSnapshot1.child("state").getValue()));
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
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.e("TEST", String.valueOf(dataSnapshot1));

                    BasedOnLikes basedOnLikes = new BasedOnLikes(String.valueOf(dataSnapshot1.child("name").getValue()),
                            String.valueOf(dataSnapshot1.child("address").getValue()),
                            String.valueOf(dataSnapshot1.child("review_count").getValue()),
                            String.valueOf(dataSnapshot1.child("city").getValue()),
                            String.valueOf(dataSnapshot1.child("state").getValue()));
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
