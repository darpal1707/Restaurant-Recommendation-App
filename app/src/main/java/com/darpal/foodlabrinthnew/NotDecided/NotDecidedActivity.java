package com.darpal.foodlabrinthnew.NotDecided;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.darpal.foodlabrinthnew.R;

public class NotDecidedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_decided);

        MealQuestionFragment questionFragment = new MealQuestionFragment();
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.questionFrame,questionFragment);
        transaction.commit();
    }
}
