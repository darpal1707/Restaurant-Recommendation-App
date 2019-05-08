package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darpal.foodlabrinthnew.Model.Reviews;
import com.darpal.foodlabrinthnew.Model.Trending;
import com.darpal.foodlabrinthnew.R;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewVH> {

    private Context context;
    private List<Reviews> reviewsData;

    public ReviewsAdapter(Context context, List<Reviews> reviewsData) {
        this.context = context;
        this.reviewsData = reviewsData;
    }

    @NonNull
    @Override
    public ReviewsAdapter.ReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_cell, parent, false);
        return new ReviewsAdapter.ReviewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ReviewVH holder, int position) {
        Reviews item = reviewsData.get(position);
        if(item!=null) {
            // Log.e("pos",trendingData.get(i).getName()+" "+i);
            holder.review.setText(reviewsData.get(position).getText());
            holder.date.setText(reviewsData.get(position).getDate());
            holder.resRating.setRating(Float.parseFloat(reviewsData.get(position).getStars()));
        }
        else {
            Toast.makeText(context, "Something went wrong in Adapter!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return reviewsData.size();
    }

    public class ReviewVH extends RecyclerView.ViewHolder {

        private TextView review;
        private TextView date;
        private RatingBar resRating;

        public ReviewVH(@NonNull View itemView) {
            super(itemView);
            review = (TextView) itemView.findViewById(R.id.review_text);
            date = (TextView) itemView.findViewById(R.id.review_date);
            resRating = (RatingBar) itemView.findViewById(R.id.resRating);

            Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Montserrat-Medium.ttf");
            review.setTypeface(custom_font);
            date.setTypeface(custom_font);
        }
    }
}
