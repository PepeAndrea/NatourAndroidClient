package com.exam.natour.UI.View.AuthMainPage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.exam.natour.R;
import com.exam.natour.UI.View.LoginPage.LoginPage;
import com.exam.natour.UI.View.LoginPage.LoginPageViewModel;

public class AuthMainPage extends Fragment {

    private Button FacebookLogin,GoogleLogin,EmailLogin;

    public AuthMainPage() {
        // Required empty public constructor
    }

    public static AuthMainPage newInstance() {
        AuthMainPage fragment = new AuthMainPage();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auth_main_page, container, false);
        this.EmailLogin = view.findViewById(R.id.EmailLogin);
        this.FacebookLogin = view.findViewById(R.id.FacebookLogin);
        this.GoogleLogin = view.findViewById(R.id.GoogleLogin);

        this.EmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginPage();
            }
        });


        return view;
    }

    private void goToLoginPage(){
        getParentFragmentManager().beginTransaction().
                replace(R.id.AuthContainer, new LoginPage()).
                commit();
    }





}