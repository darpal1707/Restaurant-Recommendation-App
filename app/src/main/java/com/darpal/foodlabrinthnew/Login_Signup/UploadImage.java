package com.darpal.foodlabrinthnew.Login_Signup;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.Model.Upload;
import com.darpal.foodlabrinthnew.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadImage extends Fragment {


    public UploadImage() {
        // Required empty public constructor
    }

    private Button mUpload;         //Chooses the image from camera
    private ImageView mImageView;               //Image view
    private ProgressBar mProgressBar;           //Progress bar
    private static final int CAMERA_REQUEST_CODE=1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_upload_image, container, false);

        mUpload = view.findViewById(R.id.button_choose_image);
        mImageView = view.findViewById(R.id.image_view);
        mProgressBar = view.findViewById(R.id.progress_bar);

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_REQUEST_CODE && resultCode==RESULT_OK) {

            Toast.makeText(getActivity(), "Uploading Now", Toast.LENGTH_SHORT).show();

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBAOS = baos.toByteArray();

            mImageView.setImageBitmap(bitmap);

            // Picasso.with(this).load(String.valueOf(bitmap)).into(mImageView);

            FirebaseApp.initializeApp(getActivity());

            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://foodlabrinthnew.appspot.com/");
            StorageReference imagesRef = storageRef.child("photos" + new Date().getTime());

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference mDatabaseRef = database.getReference("photos");

            final UploadTask uploadTask = imagesRef.putBytes(dataBAOS);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getActivity(), "Sending failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(0);
                        }
                    },500);
                    Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    // Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString());
                  /*  Upload upload = new Upload(downloadUrl.toString(), );
                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(upload);*/

                   /* Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
                            taskSnapshot.getStorage().getDownloadUrl().toString());

                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(upload);
                   // Toast.makeText(getApplicationContext(), "Posted Successfuly", Toast.LENGTH_SHORT).show();*/

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
