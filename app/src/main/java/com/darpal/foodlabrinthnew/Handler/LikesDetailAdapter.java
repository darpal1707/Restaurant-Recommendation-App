package com.darpal.foodlabrinthnew.Handler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darpal.foodlabrinthnew.Model.BasedOnLikes;
import com.darpal.foodlabrinthnew.R;

import java.util.ArrayList;
import java.util.List;

public class LikesDetailAdapter  extends RecyclerView.Adapter<LikesDetailVH> {
    private List<BasedOnLikes> userModels;

    public LikesDetailAdapter(){
        this.userModels = new ArrayList<>();
    }

    @NonNull
    @Override
    public LikesDetailVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LikesDetailVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.likes_home_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LikesDetailVH holder, int position) {
        holder.setItem(userModels.get(position));
    }

    public void addAll(List<BasedOnLikes> newUsers) {
        String  initialSize = String.valueOf(userModels.size());
        userModels.addAll(newUsers);
        notifyItemRangeInserted(Integer.parseInt(initialSize), newUsers.size());
    }

    public String getLastItemId() {
        return userModels.get(userModels.size() - 1).getBusiness_id();
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }
}
