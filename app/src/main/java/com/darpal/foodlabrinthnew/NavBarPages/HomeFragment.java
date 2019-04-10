package com.darpal.foodlabrinthnew.NavBarPages;


import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
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

    private List<Trending> trendingList;
    TrendingAdapter trendingAdapter;

    private List<BasedOnLikes> likesList;
    BasedOnLikesAdapter likesAdapter;

    DatabaseReference databaseReference;
    Query recentPostsQuery;

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        trending_recycler = (RecyclerView) view.findViewById(R.id.trending_recyclerview);
        likes_recycler = (RecyclerView) view.findViewById(R.id.likes_recyclerview);

        trendingList = new ArrayList<>();
        trendingList.add(new Trending(R.drawable.american, "Lenwich", "American", "8th AVE", "1500"));
        trendingList.add(new Trending(R.drawable.mexican, "Chipotle", "Mexican", "Herald Square", "1881"));
        trendingList.add(new Trending(R.drawable.indian, "Sapphire", "Indian", "1845 Broadway", "2121"));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("business");
        recentPostsQuery = databaseReference.child("business")
                .limitToFirst(10);

        trendingAdapter = new TrendingAdapter(getContext(),trendingList);
        trending_recycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        trending_recycler.setAdapter(trendingAdapter);

        likesList = new ArrayList<BasedOnLikes>();
        likesAdapter = new BasedOnLikesAdapter(getContext(),likesList);
        showLikesData();



        return view;
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
                    BasedOnLikes basedOnLikes = dataSnapshot1.child("value").getValue(BasedOnLikes.class);
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
