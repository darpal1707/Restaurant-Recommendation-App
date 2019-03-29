package com.darpal.foodlabrinthnew.NavBarPages;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.darpal.foodlabrinthnew.Login_Signup.LoginFragment;
import com.darpal.foodlabrinthnew.Login_Signup.SignupFragment;
import com.darpal.foodlabrinthnew.R;

public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LoginFragment loginFragment = new LoginFragment();
        getFragmentManager().beginTransaction().replace(R.id.login_frame, loginFragment).commit();

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

}
