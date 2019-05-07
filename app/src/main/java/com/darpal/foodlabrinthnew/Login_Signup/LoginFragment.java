package com.darpal.foodlabrinthnew.Login_Signup;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.firebase.ui.auth.AuthUI.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }

    EditText inputEmail, inputPassword;
    TextView signup, forgot;
    Button sigin;
    private FirebaseAuth auth;

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton google_loginBtn;

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
        google_loginBtn = (SignInButton) view.findViewById(R.id.Google_sign_in_button);
        forgot = (TextView) view.findViewById(R.id.forgot_password);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordFragment loginFragment = new ForgotPasswordFragment();
                getFragmentManager().beginTransaction().replace(R.id.login_frame, loginFragment).commit();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupFragment signupFragment = new SignupFragment();
                getFragmentManager().beginTransaction().replace(R.id.login_frame, signupFragment).commit();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        google_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });


        sigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError("Please enter correct email address");
                    //Toast.makeText(getActivity(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (inputPassword.getText().toString().length()<6) {
                    inputPassword.setError("Password should be atleast 6 characters");
                    //Toast.makeText(getActivity(), "Enter password!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        if (alreadyloggedAccount != null) {
            Toast.makeText(getActivity(), "Already Logged In", Toast.LENGTH_SHORT).show();
            UserProfileFragment userProfileFragment = new UserProfileFragment();
            getFragmentManager().beginTransaction().replace(R.id.login_frame, userProfileFragment).commit();
        } else {
            Log.d("LoginFragment", "Not logged in");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);

                        UserProfileFragment userProfileFragment = new UserProfileFragment();
                        getFragmentManager().beginTransaction().replace(R.id.login_frame, userProfileFragment).commit();

                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w("LoginFragment", "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }
}
