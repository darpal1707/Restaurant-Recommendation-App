package com.darpal.foodlabrinthnew.NotDecided;


import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.NotDecided.RestaurantData.NotDecidedOutputActivity;
import com.darpal.foodlabrinthnew.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BudgetQuestionFragment extends Fragment {


    public BudgetQuestionFragment() {
        // Required empty public constructor
    }

    private TextView subtitleTextView;
    private View descriptionLayout;
    String l = "", m = "", h = "", temp = "";
    public String cuisineValue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_budget_question, container, false);

        CardView low = (CardView) view.findViewById(R.id.lowbudget);
        CardView medium = (CardView) view.findViewById(R.id.mediumbudget);
        CardView high = (CardView) view.findViewById(R.id.highbudget);
        ArrayList<String> getdata = new ArrayList<>();

        low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l = "500";
                m = "";
                h = "";
                Intent intent = new Intent(getActivity(), NotDecidedOutputActivity.class);
                intent.putExtra("cuisine",cuisineValue);
                startActivity(intent);
            }
        });
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m = "1500";
                l = "";
                h = "";
                Intent intent = new Intent(getActivity(), NotDecidedOutputActivity.class);
                intent.putExtra("cuisine",cuisineValue);
                startActivity(intent);
            }
        });
        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h = "2500";
                l = "";
                m = "";
                Intent intent = new Intent(getActivity(), NotDecidedOutputActivity.class);
                intent.putExtra("cuisine",cuisineValue);
                startActivity(intent);
            }
        });

        return view;
    }

    public void alldata( String ambiance,String data[],String meal) {
        String meal1 = meal;
        String ambianc = ambiance;
        String[] arr = data;
        for(int i=0;i<data.length;i++)
        {
            cuisineValue = data[i];
            Log.e("fourth fragment",data[i]);
        }
    }
}
