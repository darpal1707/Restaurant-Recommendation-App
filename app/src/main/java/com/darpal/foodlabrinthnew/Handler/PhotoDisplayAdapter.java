package com.darpal.foodlabrinthnew.Handler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darpal.foodlabrinthnew.Model.Upload;
import com.darpal.foodlabrinthnew.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.firebase.ui.auth.AuthUI.TAG;

public class PhotoDisplayAdapter extends RecyclerView.Adapter<PhotoDisplayAdapter.ImageVH> {

    private Context mContext;
    private List<Upload> mUploads;
    boolean isImageFitToScreen;

    public PhotoDisplayAdapter(Context mContext, List<Upload> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }


    @NonNull
    @Override
    public PhotoDisplayAdapter.ImageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.photo_display_cell, parent, false);
        return new ImageVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoDisplayAdapter.ImageVH holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        Picasso.get().load(uploadCurrent.getImageUrl()).placeholder(R.mipmap.load).fit()
                .centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageVH extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageVH(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image_view_upload);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
            });
        }
    }
}
