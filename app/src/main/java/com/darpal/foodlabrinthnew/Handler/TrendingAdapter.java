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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Model.Trending;
import com.darpal.foodlabrinthnew.NavBarPages.HomeFragment;
import com.darpal.foodlabrinthnew.R;
import com.darpal.foodlabrinthnew.RestaurantProfileActivity;

import java.util.List;

import static com.firebase.ui.auth.AuthUI.TAG;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingVH> {

    public static Context context;
    public List<Trending> trendingData;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public TrendingAdapter(Context context, List<Trending> trendingList) {
        this.trendingData = trendingList;
        this.context = context;

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
        final Trending item = trendingData.get(i);
        if(item!=null) {
           // Log.e("pos",trendingData.get(i).getName()+" "+i);
            trendingVH.name.setText(item.getName());
            trendingVH.address.setText(item.getAddress());
            trendingVH.review_count.setText(item.getReview_count());
            trendingVH.city.setText(item.getCity());
            trendingVH.state.setText(item.getState());
            trendingVH.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, (CharSequence) item, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, RestaurantProfileActivity.class);
                    intent.putExtra("rest_id",item.getBusiness_id());
                    intent.putExtra("name", item.getName());
                    intent.putExtra("address",HomeFragment.address);
                    intent.putExtra("city",HomeFragment.city);
                    intent.putExtra("state", HomeFragment.state);
                    intent.putExtra("lat",HomeFragment.latitude.trim().toString());
                    intent.putExtra("long", HomeFragment.longitude.trim().toString());
                    Toast.makeText(context, "" + item.getBusiness_id(), Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);
                }
            });
        }
        else {
            Toast.makeText(context, "Something went wrong in Adapter!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return trendingData.size();
    }

    public static class TrendingVH extends RecyclerView.ViewHolder {

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
            //itemView.setOnClickListener(this);
        }

       /* @Override
        public void onClick(View v) {
            String  position = String.valueOf(getAdapterPosition());
            Toast.makeText(context, "Position: " + position, Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(context, RestaurantProfileActivity.class);
            myIntent.putExtra("rest_id",HomeFragment.business_id);
            myIntent.putExtra("name", HomeFragment.name);
            myIntent.putExtra("address",HomeFragment.address);
            myIntent.putExtra("city",HomeFragment.city);
            myIntent.putExtra("state", HomeFragment.state);
            myIntent.putExtra("lat",HomeFragment.latitude);
            myIntent.putExtra("long", HomeFragment.longitude);
            Toast.makeText(context, "" + HomeFragment.business_id, Toast.LENGTH_SHORT).show();
            context.startActivity(myIntent);
        }*/
    }
}
