package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BasedOnLikesAdapter extends RecyclerView.Adapter<BasedOnLikesAdapter.LikesVH> {

    private List<BasedOnLikes> basedOnLikesArrayList;
    Context context;


    public BasedOnLikesAdapter(Context context, List<BasedOnLikes> based) {
        this.basedOnLikesArrayList = based;
        this.context = context;
    }

    @NonNull
    @Override
    public BasedOnLikesAdapter.LikesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.likes_home_cell, parent, false);

        return new LikesVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BasedOnLikesAdapter.LikesVH holder, int position) {
        BasedOnLikes item = basedOnLikesArrayList.get(position);
        if(item!=null) {
            holder.name.setText(basedOnLikesArrayList.get(position).getName());
            holder.address.setText(basedOnLikesArrayList.get(position).getAddress());
            holder.review_count.setText(basedOnLikesArrayList.get(position).getReview_count());
            holder.city.setText(basedOnLikesArrayList.get(position).getCity());
            holder.state.setText(basedOnLikesArrayList.get(position).getState());
        }
        else {
            Toast.makeText(context, "Something went wrong in Adapter!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return basedOnLikesArrayList.size();
    }

    public class LikesVH extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView address;
        private TextView review_count;
        private TextView city;
        private TextView state;
        private TextView is_open;

        public LikesVH(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.res_title);
            address = (TextView) itemView.findViewById(R.id.res_location);
            review_count = (TextView) itemView.findViewById(R.id.ratings_value);
            city = (TextView) itemView.findViewById(R.id.res_city);
            state = (TextView) itemView.findViewById(R.id.res_state);

        }
    }
}