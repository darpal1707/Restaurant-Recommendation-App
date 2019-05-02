package com.darpal.foodlabrinthnew.Login_Signup;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.darpal.foodlabrinthnew.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {


    public UserProfileFragment() {
        // Required empty public constructor
    }
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    Button signout;
    private GoogleSignInClient mGoogleSignInClient;
    CardView uploadPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_profile, container, false);
        uploadPhoto = (CardView) view.findViewById(R.id. photoUpload);
        TextView username = (TextView) view.findViewById(R.id.tvuname);
        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage image = new UploadImage();
                getFragmentManager().beginTransaction().replace(R.id.login_frame,image).commit();
            }
        });
        AccountManager manager = (AccountManager) getActivity().getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        String gmail = null;

        for(Account account: list)
        {
            if(account.type.equalsIgnoreCase("com.google"))
            {
                gmail = account.name;
                String fullName = gmail.substring(0,gmail.lastIndexOf("@"));
                username.setText(fullName);
                break;
            }
        }
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        signout = (Button) view.findViewById(R.id.btnLogout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        LoginFragment loginFragment = new LoginFragment();
                        getFragmentManager().beginTransaction().replace(R.id.login_frame,loginFragment).commit();
                    }
                });
            }
        });

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    LoginFragment loginFragment = new LoginFragment();
                    getFragmentManager().beginTransaction().replace(R.id.login_frame,loginFragment).commit();
                }
            }
        };
        return view;
    }

}
