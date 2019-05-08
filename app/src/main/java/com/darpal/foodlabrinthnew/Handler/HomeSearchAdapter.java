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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.NavBarPages.SearchRestaurantHomePageActivity;
import com.darpal.foodlabrinthnew.R;
import com.darpal.foodlabrinthnew.RestaurantProfileActivity;
import com.darpal.foodlabrinthnew.Util.LikesUtil;

import java.util.List;

public class HomeSearchAdapter extends RecyclerView.Adapter<HomeSearchAdapter.HomeVH> {

    private List<BasedOnLikes> searchArrayList;
    Context context;
    private int[] images;

    public HomeSearchAdapter(List<BasedOnLikes> searchArrayList, SearchRestaurantHomePageActivity context, int[] images) {
        this.searchArrayList = searchArrayList;
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public HomeSearchAdapter.HomeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.likes_home_cell, parent, false);

        return new HomeSearchAdapter.HomeVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeSearchAdapter.HomeVH holder, int position) {
        BasedOnLikes item = searchArrayList.get(position);
        if(item!=null) {
            int image_id = images[position];
            holder.resImage.setImageResource(image_id);
            holder.name.setText(searchArrayList.get(position).getName());
            holder.address.setText(searchArrayList.get(position).getAddress());
            holder.review_count.setText(searchArrayList.get(position).getReview_count());
            holder.city.setText(searchArrayList.get(position).getCity());
            holder.state.setText(searchArrayList.get(position).getState());
        }
        else {
            Toast.makeText(context, "Something went wrong in Adapter!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return searchArrayList.size();
    }

    public class HomeVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private ImageView resImage;
        private TextView address;
        private TextView review_count;
        private TextView city;
        private TextView state;

        public HomeVH(@NonNull View view) {
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
            Intent intent = new Intent(context, RestaurantProfileActivity.class);
            intent.putExtra("business_id", LikesUtil.searchId.get(position));
            intent.putExtra("name", LikesUtil.searchName.get(position));
            intent.putExtra("cuisine", LikesUtil.searchCuisine.get(position));
            intent.putExtra("address", LikesUtil.searchAddress.get(position));
            intent.putExtra("city", LikesUtil.searchCity.get(position));
            intent.putExtra("state", LikesUtil.searchState.get(position));
            intent.putExtra("lat", LikesUtil.searchLat.get(position));
            intent.putExtra("long", LikesUtil.searchLong.get(position));
            intent.putExtra("hours", LikesUtil.searchHours.get(position));

            context.startActivity(intent);
        }
    }
}
