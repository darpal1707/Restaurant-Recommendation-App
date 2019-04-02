package com.darpal.foodlabrinthnew.NavBarPages;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Handler.LikesAdapter;
import com.darpal.foodlabrinthnew.Handler.TrendingAdapter;
import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.Model.Trending;
import com.darpal.foodlabrinthnew.R;
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

    private RecyclerView trending_recycler;
    private RecyclerView likes_recycler;

    private ArrayList<Trending> trendingList;
    TrendingAdapter trendingAdapter;

    private ArrayList<BasedOnLikes> likesList;
    LikesAdapter likesAdapter;

    DatabaseReference databaseReference;
    Query recentPostsQuery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        trending_recycler = (RecyclerView) view.findViewById(R.id.trending_recyclerview);
        likes_recycler = (RecyclerView) view.findViewById(R.id.likes_recyclerview);

        trendingList = new ArrayList<Trending>();
        likesList = new ArrayList<BasedOnLikes>();

        trendingList.add(new Trending(R.drawable.american, "Lenwich", "American", "8th AVE", "1500"));
        trendingList.add(new Trending(R.drawable.mexican, "Chipotle", "Mexican", "Herald Square", "1881"));
        trendingList.add(new Trending(R.drawable.indian, "Sapphire", "Indian", "1845 Broadway", "2121"));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("business");
        recentPostsQuery = databaseReference.child("business")
                .limitToFirst(10);

        trendingAdapter = new TrendingAdapter(getContext(),trendingList);
        trending_recycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        trending_recycler.setAdapter(trendingAdapter);

        likesAdapter = new LikesAdapter(getContext(),likesList);
        likes_recycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));



        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getActivity(), "inside the database reference", Toast.LENGTH_SHORT).show();
                String value = String.valueOf(dataSnapshot.getValue(BasedOnLikes.class));
                Log.d("inside dbreference", "Value is: " + value);
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    BasedOnLikes basedOnLikes = dataSnapshot1.getValue(BasedOnLikes.class);
                    likesList.add(basedOnLikes);
                }
                likesAdapter = new LikesAdapter(getContext(),likesList);
                likes_recycler.setAdapter(likesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("datares", String.valueOf(databaseReference));

        return view;
    }

   /* @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trendingList = new ArrayList<>();
        trendingList.add(new Trending(R.drawable.american, "Lenwich", "American", "8th AVE", "1500"));
        trendingList.add(new Trending(R.drawable.mexican, "Chipotle", "Mexican", "Herald Square", "1881"));
        trendingList.add(new Trending(R.drawable.indian, "Sapphire", "Indian", "1845 Broadway", "2121"));

        likesList = new ArrayList<>();
    }*/
}
