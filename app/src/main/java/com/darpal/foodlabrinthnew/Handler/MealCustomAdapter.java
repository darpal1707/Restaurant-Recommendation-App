package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.darpal.foodlabrinthnew.NotDecided.CuisineQuestionFragment;
import com.darpal.foodlabrinthnew.NotDecided.NotDecidedActivity;
import com.darpal.foodlabrinthnew.R;

public class MealCustomAdapter extends BaseAdapter {

    RadioButton checkBox;
    ImageView imageView;
    private int[] array = new int[0];
    Context context;
    NotDecidedActivity notDecidedActivity;
    String arr[];

    public MealCustomAdapter(Context mealcontext, int[] array, String  arr[]){
        context = mealcontext;
        this.array = array;
        this.arr=arr;
        notDecidedActivity = (NotDecidedActivity) context;
    }

    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public Object getItem(int position) {
        return array[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View grid;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        grid = new View(context);
        grid = layoutInflater.inflate(R.layout.mealcell, null);
        ImageView imageview = (ImageView) grid.findViewById(R.id.imageview);
        //final RadioButton checkbox = (RadioButton) grid.findViewById(R.id.abcd);
        imageview.setImageResource(array[position]);


        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuisineQuestionFragment secondQuestion = new CuisineQuestionFragment();
                String s = arr[position];
                //Toast.makeText(context, "s==" + s, Toast.LENGTH_SHORT).show();
                Bundle b = new Bundle();
                b.putString("Meal",s);
                secondQuestion.setArguments(b);
                notDecidedActivity.getSupportFragmentManager().beginTransaction().replace(R.id.questionFrame, secondQuestion).addToBackStack(null).commit();
            }
        });
        return grid;
    }
}
