package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.NavBarPages.SearchFragment;
import com.darpal.foodlabrinthnew.R;

public class CuisineSearchAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] cuisine;
    private final int[] cuisineImg;

    public CuisineSearchAdapter(Context mContext, String[] cuisine, int[] cuisineImg) {
        this.mContext = mContext;
        this.cuisine = cuisine;
        this.cuisineImg = cuisineImg;
    }


    @Override
    public int getCount() {
        return cuisine.length;
    }

    @Override
    public Object getItem(int position) {
        return cuisine[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        grid = (View.inflate(mContext, R.layout.item_cuisine_grid, null));
        ImageView cuisineImage = (ImageView) grid.findViewById(R.id.grid_image);
        cuisineImage.setImageResource(cuisineImg[position]);

        return grid;
    }
}
