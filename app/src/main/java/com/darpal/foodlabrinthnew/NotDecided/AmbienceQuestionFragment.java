package com.darpal.foodlabrinthnew.NotDecided;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Handler.AmbienceCustomAdapter;
import com.darpal.foodlabrinthnew.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.
 */
public class AmbienceQuestionFragment extends Fragment {


    public AmbienceQuestionFragment() {
        // Required empty public constructor
    }

    public CheckBox checkBox;
    private int[] array = new int[]{R.drawable.luxury, R.drawable.romantic, R.drawable.rooftop, R.drawable.streetamb, R.drawable.unnamed, R.drawable.hangoutspark};
    private String arr[] = {"Luxury", "Romantic", "Roof Top", "Street/Open Area", "Unique Dining", "Hangout"};
    private ArrayList<String> getdata;
    private String meal;
    private String[] stockArr;
    String s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14;
    TextView AmbienceQuestion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_ambience_question, container, false);

        getdata = new ArrayList<>();
        GridView gridView = (GridView) view.findViewById(R.id.ambiencegrid);
        AmbienceCustomAdapter ambienceCustomAdapter = new AmbienceCustomAdapter(getActivity(), array);
        gridView.setAdapter(ambienceCustomAdapter);

        AmbienceQuestion = (TextView) view.findViewById(R.id.AmbienceTitle);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/Montserrat-Medium.ttf");
        AmbienceQuestion.setTypeface(custom_font);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data = arr[position];
                //Toast.makeText(getActivity(), "" + data, Toast.LENGTH_SHORT).show();
                BudgetQuestionFragment fourthQuestion = new BudgetQuestionFragment();
                fourthQuestion.alldata(data, stockArr, meal);
                getFragmentManager().beginTransaction().replace(R.id.questionFrame, fourthQuestion).addToBackStack(null).commit();
            }
        });

        SharedPreferences pref = getActivity().getSharedPreferences("Cuisines", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return view;
    }

    public void GetArrayData(ArrayList<String> arrayList, String meal) {
        this.getdata = arrayList;
        this.meal = meal;
        HashSet<String> hs = new HashSet<>();
        hs.addAll(getdata);
        getdata.clear();
        getdata.addAll(hs);
        for (int i = 0; i < arrayList.size(); i++) {
            Log.e("DataGet", getdata.get(i));
            Log.e("Meal", meal);
        }
        stockArr = new String[getdata.size()];
        stockArr = getdata.toArray(stockArr);
        StringTokenizer st = new StringTokenizer(getdata.toString());
        while (st.hasMoreTokens()) {
            System.out.println("Next token is : " + st.nextToken(","));
        }
        /*try {
            editor.putString("Meal", meal);
            for (int i = 0; i < getdata.size(); i++) {
                editor.putString("val" + String.valueOf(i), getdata.get(i));
            }
            editor.apply();
        } catch (Exception e) {
            Log.e("error", String.valueOf(e));
        }*/
    }

}
