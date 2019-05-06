package com.darpal.foodlabrinthnew.Login_Signup;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.firebase.ui.auth.AuthUI.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment {


    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    Button reset;
    TextView login;
    EditText email;
    private FirebaseAuth auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_forgot_password, container, false);

        auth = FirebaseAuth.getInstance();
        login = (TextView) view.findViewById(R.id.loginPage);
        email = (EditText) view.findViewById(R.id.et_email_address);
        reset = (Button) view.findViewById(R.id.btn_reset);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                if (TextUtils.isEmpty(emailText)) {
                    Toast.makeText(getActivity(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().sendPasswordResetEmail(emailText)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("in reset", "Email sent.");
                                    Toast.makeText(getActivity(), "Email sent.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragment = new LoginFragment();
                getFragmentManager().beginTransaction().replace(R.id.login_frame,loginFragment).commit();
            }
        });

        return view;

    }

}
