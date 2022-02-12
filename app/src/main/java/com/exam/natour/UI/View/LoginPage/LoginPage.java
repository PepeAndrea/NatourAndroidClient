package com.exam.natour.UI.View.LoginPage;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.exam.natour.Activity.MainActivity;
import com.exam.natour.Model.AuthUser;
import com.exam.natour.R;

public class LoginPage extends Fragment {

    private LoginPageViewModel loginPageViewModel;
    Button loginButton,goToSignUpButton;
    EditText emailInput,passwordInput;

    public LoginPage() {
        // Required empty public constructor
    }

    public static LoginPage newInstance() {
        LoginPage fragment = new LoginPage();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPageViewModel = new ViewModelProvider(this).get(LoginPageViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_page, container, false);
        loginButton = view.findViewById(R.id.login_button);
        goToSignUpButton = view.findViewById(R.id.go_to_signup_button);
        emailInput = view.findViewById(R.id.email_login_input);
        passwordInput = view.findViewById(R.id.passwod_login_input);

        goToSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignupPage();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginPageViewModel.login(emailInput.getText().toString(),passwordInput.getText().toString());
                if(AuthUser.getInstance().getToken() != null && AuthUser.getInstance().getToken().length() > 0 ){
                    startActivity(new Intent(view.getContext(), MainActivity.class));
                    getActivity().finish();
                }else{
                    Toast.makeText(view.getContext(),"Login non riuscito",Toast.LENGTH_LONG).show();
                }
            }
        });



        return view;
    }

    private void goToSignupPage(){
        //getParentFragmentManager().beginTransaction().replace(R.id.AuthContainer, new LoginPage()).commit();
        Toast.makeText(getContext(),"Ancora non impostata",Toast.LENGTH_LONG).show();
    }
}