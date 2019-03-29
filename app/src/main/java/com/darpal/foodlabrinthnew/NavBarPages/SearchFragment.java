package com.darpal.foodlabrinthnew.NavBarPages;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toolbar;

import com.darpal.foodlabrinthnew.Handler.CuisineSearchAdapter;
import com.darpal.foodlabrinthnew.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    public SearchFragment() {
        // Required empty public constructor
    }
    GridView cuisine;
    static final String[] cuisines = new String[]{"American", "Asian", "BBQ", "Chinese", "Coffee & Tea", "Deli", "Desserts",
            "European", "Fast Food", "Greek", "Halal", "Indian", "Italian", "Jamaican", "Japanese", "Korean",
            "Mediterranean", "Mexican", "Salads", "Spanish", "Sushi", "Thai", "Vegan", "Vegetarian"};

    int[] cuisineImg = {R.drawable.american, R.drawable.asian, R.drawable.bbq, R.drawable.chinese,
            R.drawable.coffee_tea, R.drawable.deli, R.drawable.desserts_two, R.drawable.european,
            R.drawable.fast_food, R.drawable.greek, R.drawable.halal, R.drawable.indian,
            R.drawable.italian, R.drawable.jamaican, R.drawable.japanese, R.drawable.korean,
            R.drawable.mediterranean, R.drawable.mexican, R.drawable.salads, R.drawable.spanish,
            R.drawable.sushi, R.drawable.thai, R.drawable.vegan, R.drawable.vegetarian};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        cuisine = (GridView) view.findViewById(R.id.cuisineGrid);
        CuisineSearchAdapter searchAdapter = new CuisineSearchAdapter(getContext(), cuisines, cuisineImg);
        cuisine.setVerticalScrollBarEnabled(false);
        cuisine.setAdapter(searchAdapter);

        return view;
    }

}
