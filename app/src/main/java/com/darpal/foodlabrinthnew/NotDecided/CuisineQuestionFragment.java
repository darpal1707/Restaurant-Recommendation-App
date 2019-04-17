package com.darpal.foodlabrinthnew.NotDecided;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Handler.CuisineCustomAdapter;
import com.darpal.foodlabrinthnew.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.
 */
public class CuisineQuestionFragment extends Fragment {


    public CuisineQuestionFragment() {
        // Required empty public constructor
    }

    GridView gridView;
    public CheckBox cb;
    ImageView imageview;
    Button click;
    private CuisineCustomAdapter cuisineCustomAdapter;
    String arr1[];
    String arraynew;
    ArrayList<String> arrayList;
    String Meal;

    String arr[] = {"American", "Asian", "BBQ", "Chinese", "Coffee & Tea", "Deli", "Desserts",
            "European", "Fast Food", "Greek", "Halal", "Indian", "Italian", "Jamaican", "Japanese", "Korean",
            "Mediterranean", "Mexican", "Salads", "Spanish", "Sushi", "Thai", "Vegan", "Vegetarian"};

    int[] array = {R.drawable.american, R.drawable.asian, R.drawable.bbq, R.drawable.chinese,
            R.drawable.coffee_tea, R.drawable.deli, R.drawable.desserts_two, R.drawable.european,
            R.drawable.fast_food, R.drawable.greek, R.drawable.halal, R.drawable.indian,
            R.drawable.italian, R.drawable.jamaican, R.drawable.japanese, R.drawable.korean,
            R.drawable.mediterranean, R.drawable.mexican, R.drawable.salads, R.drawable.spanish,
            R.drawable.sushi, R.drawable.thai, R.drawable.vegan, R.drawable.vegetarian};

    String[] stockArr;
    String s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cuisine_question, container, false);
        gridView = (GridView) view.findViewById(R.id.cuisinegrid);
        arrayList = new ArrayList<>();
        Bundle b = getArguments();
        Meal = b.getString("Meal");

        cuisineCustomAdapter = new CuisineCustomAdapter(getActivity(), array,arr);
        gridView.setAdapter(cuisineCustomAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    arraynew = arr[position];
                    arrayList.add(arraynew);
                    for(int i=0;i<arrayList.size();i++)
                    {
                        Log.e("data",arrayList.get(i));
                    }
                } catch (Exception ignored) {
                    Toast.makeText(getActivity(), "" + ignored, Toast.LENGTH_SHORT).show();
                }
                HashSet<String > hashSet=new HashSet<String>();
                hashSet.addAll(arrayList);
                Log.e("NewArray", String.valueOf(hashSet));
                Toast.makeText(getActivity(), "" + arraynew, Toast.LENGTH_SHORT).show();
            }
        });

        click = (Button) view.findViewById(R.id.nextpage);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbienceQuestionFragment thirdQuestion = new AmbienceQuestionFragment();
                thirdQuestion.GetArrayData(arrayList, Meal);
                getFragmentManager().beginTransaction().replace(R.id.questionFrame, thirdQuestion).addToBackStack(null).commit();
            }
        });
        return view;
    }

}
