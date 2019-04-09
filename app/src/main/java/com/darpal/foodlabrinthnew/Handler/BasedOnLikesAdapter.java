package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BasedOnLikesAdapter extends RecyclerView.Adapter<BasedOnLikesAdapter.LikesVH> {

    private List<BasedOnLikes> basedOnLikesArrayList = new ArrayList<>();
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
        holder.resName.setText(basedOnLikesArrayList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return basedOnLikesArrayList.size();
    }

    public class LikesVH extends RecyclerView.ViewHolder {

        private TextView resName;
        //private TextView resCuisine;
        private TextView resLocation;
        private TextView resReviewCount;
        private TextView city;
        private TextView state;
        private TextView is_open;

        public LikesVH(View view) {
            super(view);
            //resImage = (ImageView) itemView.findViewById(R.id.res_img);
            resName = (TextView) itemView.findViewById(R.id.res_title);
            //resCuisine = (TextView) itemView.findViewById(R.id.res_cuisine);
            resLocation = (TextView) itemView.findViewById(R.id.res_location);
            resReviewCount = (TextView) itemView.findViewById(R.id.ratings_value);
            //city = (TextView) itemView.findViewById(R.id.res_city);
            //state = (TextView) itemView.findViewById(R.id.res_state);

        }
    }
}