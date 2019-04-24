package com.darpal.foodlabrinthnew.Handler;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.R;
import com.darpal.foodlabrinthnew.RestaurantProfileActivity;
import com.darpal.foodlabrinthnew.Util.LikesUtil;

public class LikesDetailVH extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView name;
    private TextView address;
    private TextView review_count;
    private TextView city;
    private TextView state;
    private TextView is_open;
    Context context;

    public LikesDetailVH(@NonNull View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.res_title);
        address = (TextView) itemView.findViewById(R.id.res_location);
        review_count = (TextView) itemView.findViewById(R.id.ratings_value);
        city = (TextView) itemView.findViewById(R.id.res_city);
        state = (TextView) itemView.findViewById(R.id.res_state);
        itemView.setOnClickListener(this);
    }

    public void setItem(BasedOnLikes post){
        name.setText(post.name);
        address.setText(post.address);
        review_count.setText(post.review_count);
        city.setText(post.city);
        state.setText(post.state);
    }

    @Override
    public void onClick(View v) {
        int  position = (getAdapterPosition());
        //Toast.makeText(context, "Position: " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, RestaurantProfileActivity.class);
        intent.putExtra("business_id", LikesUtil.businessIdArraryList.get(position));
        intent.putExtra("name", LikesUtil.businessNameArrayList.get(position));
        intent.putExtra("cuisine", LikesUtil.businessCuisineArrayList.get(position));
        intent.putExtra("cuisine", LikesUtil.businessCuisineArrayList.get(position));
        intent.putExtra("address", LikesUtil.businessAddressArrayList.get(position));
        intent.putExtra("city", LikesUtil.businessCityArrayList.get(position));
        intent.putExtra("state", LikesUtil.businessStateArrayList.get(position));
        intent.putExtra("lat", LikesUtil.businessLatArrayList.get(position));
        intent.putExtra("long", LikesUtil.businessLongArrayList.get(position));

        context.startActivity(intent);
    }
}
