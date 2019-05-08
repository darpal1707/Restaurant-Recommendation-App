package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Model.Trending;
import com.darpal.foodlabrinthnew.R;
import com.darpal.foodlabrinthnew.RestaurantProfileActivity;
import com.darpal.foodlabrinthnew.Util.LikesUtil;
import com.darpal.foodlabrinthnew.Util.TrendingUtil;

import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingVH> {

    private Context context;
    private List<Trending> trendingData;
    private int[] images;

    public TrendingAdapter(Context context, List<Trending> trendingList, int[] images) {
        this.trendingData = trendingList;
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public TrendingVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.trending_home_cell, viewGroup, false);
        return new TrendingVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  TrendingVH trendingVH,  int i) {
        Trending item = trendingData.get(i);
        if(item!=null) {
           // Log.e("pos",trendingData.get(i).getName()+" "+i);
            int image_id = images[i];
            trendingVH.resImage.setImageResource(image_id);
            trendingVH.name.setText(trendingData.get(i).getName());
            trendingVH.address.setText(trendingData.get(i).getAddress());
            trendingVH.review_count.setText(trendingData.get(i).getReview_count());
            trendingVH.city.setText(trendingData.get(i).getCity());
            trendingVH.state.setText(trendingData.get(i).getState());
        }
        else {
            Toast.makeText(context, "Something went wrong in Adapter!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return trendingData.size();
    }

    public class TrendingVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private TextView address;
        private TextView review_count;
        private TextView city;
        private TextView state;
        private TextView is_open;
        private ImageView resImage;

        TrendingVH(@NonNull View itemView) {
            super(itemView);

            resImage = (ImageView) itemView.findViewById(R.id.res_img);
            name = (TextView) itemView.findViewById(R.id.res_title);
            address = (TextView) itemView.findViewById(R.id.res_location);
            review_count = (TextView) itemView.findViewById(R.id.ratings_value);
            city = (TextView) itemView.findViewById(R.id.res_city);
            state = (TextView) itemView.findViewById(R.id.res_state);
            itemView.setOnClickListener(this);

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
            intent.putExtra("business_id", TrendingUtil.businessIdArraryList.get(position));
            intent.putExtra("name", TrendingUtil.businessNameArrayList.get(position));
            intent.putExtra("cuisine", TrendingUtil.businessCuisineArrayList.get(position));
            intent.putExtra("address", TrendingUtil.businessAddressArrayList.get(position));
            intent.putExtra("city", TrendingUtil.businessCityArrayList.get(position));
            intent.putExtra("state", TrendingUtil.businessStateArrayList.get(position));
            intent.putExtra("lat", TrendingUtil.businessLatArrayList.get(position));
            intent.putExtra("long", TrendingUtil.businessLongArrayList.get(position));
            intent.putExtra("hours",TrendingUtil.businessHoursArrayList.get(position));
            context.startActivity(intent);
        }
    }
}
