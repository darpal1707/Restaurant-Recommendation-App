package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.ImageLinks;
import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.R;
import com.darpal.foodlabrinthnew.RestaurantProfileActivity;
import com.darpal.foodlabrinthnew.RestaurantProfile_HomeActivity;
import com.darpal.foodlabrinthnew.Util.LikesUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BasedOnLikesAdapter extends RecyclerView.Adapter<BasedOnLikesAdapter.LikesVH> {

    private List<BasedOnLikes> basedOnLikesArrayList;
    Context context;
    int[] images;

    public BasedOnLikesAdapter(Context context, List<BasedOnLikes> based, int[] images) {
        this.basedOnLikesArrayList = based;
        this.context = context;
        this.images = images;
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
            int image_id = images[position];
            holder.resImage.setImageResource(image_id);
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

    public class LikesVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private ImageView resImage;
        private TextView address;
        private TextView review_count;
        private TextView city;
        private TextView state;
        private TextView is_open;

        public LikesVH(View view) {
            super(view);

            resImage = (ImageView) view.findViewById(R.id.res_img);
            name = (TextView) view.findViewById(R.id.res_title);
            address = (TextView) view.findViewById(R.id.res_location);
            review_count = (TextView) view.findViewById(R.id.ratings_value);
            city = (TextView) view.findViewById(R.id.res_city);
            state = (TextView) view.findViewById(R.id.res_state);
            view.setOnClickListener(this);

            Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Montserrat-Medium.ttf");
            name.setTypeface(custom_font);
            address.setTypeface(custom_font);
            review_count.setTypeface(custom_font);
            city.setTypeface(custom_font);
            state.setTypeface(custom_font);
        }

        @Override
        public void onClick(View v) {
            int  position = (getAdapterPosition());
            //Toast.makeText(context, "Position: " + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, RestaurantProfile_HomeActivity.class);
            intent.putExtra("business_id", LikesUtil.businessIdArraryList.get(position));
            intent.putExtra("name", LikesUtil.businessNameArrayList.get(position));
            intent.putExtra("cuisine", LikesUtil.businessCuisineArrayList.get(position));
            intent.putExtra("cuisine", LikesUtil.businessCuisineArrayList.get(position));
            intent.putExtra("address", LikesUtil.businessAddressArrayList.get(position));
            intent.putExtra("city", LikesUtil.businessCityArrayList.get(position));
            intent.putExtra("state", LikesUtil.businessStateArrayList.get(position));
            intent.putExtra("lat", LikesUtil.businessLatArrayList.get(position));
            intent.putExtra("long", LikesUtil.businessLongArrayList.get(position));
            intent.putExtra("hours", LikesUtil.businessHoursArrayList.get(position));

            context.startActivity(intent);
        }
    }
}