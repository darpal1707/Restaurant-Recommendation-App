package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.R;

import java.util.List;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.BasedOnLikesVH> {

    Context context;
    List<BasedOnLikes> likesData;


    public LikesAdapter(Context context, List<BasedOnLikes> likesData) {
        this.context = context;
        this.likesData = likesData;
    }

    @NonNull
    @Override
    public BasedOnLikesVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.likes_home_cell, viewGroup,false);
        BasedOnLikesVH basedOnLikesVH = new BasedOnLikesVH(view);
        return basedOnLikesVH;
    }


    @Override
    public void onBindViewHolder(@NonNull BasedOnLikesVH basedOnLikesVH, int i) {
        basedOnLikesVH.resImage.setImageResource(likesData.get(i).getResImage());
        basedOnLikesVH.resName.setText(likesData.get(i).getResName());
        basedOnLikesVH.resCuisine.setText(likesData.get(i).getResCuisine());
        basedOnLikesVH.resLocation.setText(likesData.get(i).getResLocation());
        basedOnLikesVH.resReviewCount.setText(likesData.get(i).getResReviewCount());
    }

    @Override
    public int getItemCount() {
        return likesData.size();
    }

    public static class BasedOnLikesVH extends RecyclerView.ViewHolder {

        private ImageView resImage;
        private TextView resName;
        private TextView resCuisine;
        private TextView resLocation;
        private TextView resReviewCount;

        public BasedOnLikesVH(@NonNull View itemView) {
            super(itemView);

            resImage = (ImageView) itemView.findViewById(R.id.res_img);
            resName = (TextView) itemView.findViewById(R.id.res_title);
            resCuisine = (TextView) itemView.findViewById(R.id.res_cuisine);
            resLocation = (TextView) itemView.findViewById(R.id.res_location);
            resReviewCount = (TextView) itemView.findViewById(R.id.ratings_value);
        }
    }
}
