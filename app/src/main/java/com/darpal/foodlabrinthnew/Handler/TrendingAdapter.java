package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
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

import java.util.List;

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
        View view = LayoutInflater.from(context).inflate(R.layout.likes_home_cell, viewGroup, false);
        TrendingVH trendingVH = new TrendingVH(view);
        return new TrendingVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TrendingVH trendingVH, final int i) {
        Trending item = trendingData.get(i);
        if(item!=null) {
            trendingVH.name.setText(trendingData.get(i).getName());
            trendingVH.address.setText(trendingData.get(i).getAddress());
            trendingVH.review_count.setText(trendingData.get(i).getReview_count());
            //trendingVH.city.setText(trendingData.get(i).getCity());
            //trendingVH.state.setText(trendingData.get(i).getState());
        }
        else {
            Toast.makeText(context, "Something went wrong in Adapter!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return trendingData.size();
    }

    public static class TrendingVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private TextView address;
        private TextView review_count;
        private TextView city;
        private TextView state;
        private TextView is_open;

        public TrendingVH(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.res_title);
            address = (TextView) itemView.findViewById(R.id.res_location);
            review_count = (TextView) itemView.findViewById(R.id.ratings_value);
            city = (TextView) itemView.findViewById(R.id.res_city);
            state = (TextView) itemView.findViewById(R.id.res_state);
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
