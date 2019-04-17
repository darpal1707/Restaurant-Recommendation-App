package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.darpal.foodlabrinthnew.NotDecided.NotDecidedActivity;
import com.darpal.foodlabrinthnew.R;

public class AmbienceCustomAdapter extends BaseAdapter {

    CheckBox checkBox;
    ImageView imageView;
    private int[] array = new int[0];
    Context context;
    NotDecidedActivity notDecidedActivity;

    public AmbienceCustomAdapter(Context mealcontext, int[] array) {
        context = mealcontext;
        this.array = array;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        grid = new View(context);
        grid = layoutInflater.inflate(R.layout.cuisinecell, null);
        ImageView imageview = (ImageView) grid.findViewById(R.id.imageview);
        CheckBox checkbox = (CheckBox) grid.findViewById(R.id.checkbox);
        imageview.setImageResource(array[position]);
        /*grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/


        return grid;
    }
}
