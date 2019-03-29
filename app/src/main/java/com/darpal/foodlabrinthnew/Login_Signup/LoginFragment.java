package com.darpal.foodlabrinthnew.Login_Signup;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.NavBarPages.ProfileFragment;
import com.darpal.foodlabrinthnew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }

    EditText inputEmail, inputPassword;
    TextView signup;
    Button sigin;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            UserProfileFragment loginFragment = new UserProfileFragment();
            getFragmentManager().beginTransaction().replace(R.id.login_frame, loginFragment).commit();
        }

        inputEmail = (EditText) view.findViewById(R.id.et_email_address);
        inputPassword = (EditText) view.findViewById(R.id.et_password);
        signup = (TextView) view.findViewById(R.id.sign_up);
        sigin = (Button) view.findViewById(R.id.btn_login);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupFragment signupFragment = new SignupFragment();
                getFragmentManager().beginTransaction().replace(R.id.login_frame, signupFragment).commit();
            }
        });

        sigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError("Password should be minimum 6 characters");
                                    } else {
                                        Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    UserProfileFragment userProfileFragment = new UserProfileFragment();
                                    getFragmentManager().beginTransaction().replace(R.id.login_frame, userProfileFragment).commit();
                                }
                            }
                        });
            }
        });
        return view;
    }

}
