package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Model.Trending;
import com.darpal.foodlabrinthnew.R;
import com.darpal.foodlabrinthnew.RestaurantProfileActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingVH> {

    public static Context context;
    List<Trending> trendingData;

    public TrendingAdapter(Context context, List<Trending> trendingList) {
        this.context = context;
        this.trendingData = trendingList;
    }

    @NonNull
    @Override
    public TrendingVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.trending_home_cell, viewGroup, false);
        TrendingVH trendingVH = new TrendingVH(view);
        return new TrendingVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TrendingVH trendingVH, final int i) {
        Log.d("TAG", "Trending: OnBinder Called.");
        trendingVH.resImage.setImageResource(trendingData.get(i).getResImage());
        trendingVH.resName.setText(trendingData.get(i).getResName());
        trendingVH.resCuisine.setText(trendingData.get(i).getResCuisine());
        trendingVH.resLocation.setText(trendingData.get(i).getResLocation());
        trendingVH.resReviewCount.setText(trendingData.get(i).getResReviewCount());

    }

    @Override
    public int getItemCount() {
        return trendingData.size();
    }

    public static class TrendingVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView resImage;
        TextView resName;
        TextView resCuisine;
        TextView resLocation;
        TextView resReviewCount;
        CardView trendingCard;

        public TrendingVH(@NonNull View itemView) {
            super(itemView);

            trendingCard = (CardView) itemView.findViewById(R.id.res_cellcard);
            resImage = (ImageView) itemView.findViewById(R.id.res_img);
            resName = (TextView) itemView.findViewById(R.id.res_title);
            resCuisine = (TextView) itemView.findViewById(R.id.res_cuisine);
            resLocation = (TextView) itemView.findViewById(R.id.res_location);
            resReviewCount = (TextView) itemView.findViewById(R.id.ratings_value);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "" + getPosition(), Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(context, RestaurantProfileActivity.class);
            context.startActivity(myIntent);
        }
    }
}
