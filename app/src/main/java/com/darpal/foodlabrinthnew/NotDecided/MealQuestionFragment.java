package com.darpal.foodlabrinthnew.NotDecided;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RadioButton;

import com.darpal.foodlabrinthnew.Handler.MealCustomAdapter;
import com.darpal.foodlabrinthnew.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MealQuestionFragment extends Fragment {


    public MealQuestionFragment() {
        // Required empty public constructor
    }

    private int[] array = new int[]{R.drawable.breakfast, R.drawable.lunch, R.drawable.dinner, R.drawable.coffee, R.drawable.latenight};
    private String arr[] = {"Breakfast", "Lunch", "Dinner", "Cafe", "Late Night"};


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_question, container, false);

        GridView gridView = (GridView) view.findViewById(R.id.mealgrid);
        final MealCustomAdapter mealCustomAdapter = new MealCustomAdapter(getActivity(), array, arr);
        gridView.setAdapter(mealCustomAdapter);

        return view;
    }

}
