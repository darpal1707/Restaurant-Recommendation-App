package com.darpal.foodlabrinthnew.NavBarPages;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darpal.foodlabrinthnew.Handler.LikesAdapter;
import com.darpal.foodlabrinthnew.Handler.TrendingAdapter;
import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.Model.Trending;
import com.darpal.foodlabrinthnew.R;

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
    LikesAdapter likesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        trending_recycler = (RecyclerView) view.findViewById(R.id.trending_recyclerview);
        likes_recycler = (RecyclerView) view.findViewById(R.id.likes_recyclerview);

        trendingAdapter = new TrendingAdapter(getContext(),trendingList);
        trending_recycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        trending_recycler.setAdapter(trendingAdapter);

        likesAdapter = new LikesAdapter(getContext(),likesList);
        likes_recycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        likes_recycler.setAdapter(likesAdapter);


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trendingList = new ArrayList<>();
        trendingList.add(new Trending(R.drawable.american, "Lenwich", "American", "8th AVE", "1500"));
        trendingList.add(new Trending(R.drawable.mexican, "Chipotle", "Mexican", "Herald Square", "1881"));
        trendingList.add(new Trending(R.drawable.indian, "Sapphire", "Indian", "1845 Broadway", "2121"));

        likesList = new ArrayList<>();
        likesList.add(new BasedOnLikes(R.drawable.korean, "Red Poke", "Korean", "885, 8th AVE", "238"));
        likesList.add(new BasedOnLikes(R.drawable.mediterranean, "Naya Express", "Mediterranean", "56W, 56th Street", "2694"));
        likesList.add(new BasedOnLikes(R.drawable.desserts, "Pink Berry", "Dessert", "330W, 58th Street", "552"));
        likesList.add(new BasedOnLikes(R.drawable.sushi, "Han Sushi", "Japanese", "854, 10th AVE", "2086"));
        likesList.add(new BasedOnLikes(R.drawable.italian, "Parm", "Italian", "235, Columbus AVE", "1553"));
        likesList.add(new BasedOnLikes(R.drawable.indian, "Indikitch", "Indian", "940, 8th AVE", "935"));
        likesList.add(new BasedOnLikes(R.drawable.fast_food, "Luigi's Pizza", "Pizza", "936, 8th AVE", "1581"));
        likesList.add(new BasedOnLikes(R.drawable.chinese, "China Gourmet", "Chinese", "877, 8th AVE", "5629"));
    }
}
