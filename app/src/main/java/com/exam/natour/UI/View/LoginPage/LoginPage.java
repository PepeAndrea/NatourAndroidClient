package com.exam.natour.UI.View.LoginPage;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.exam.natour.Activity.MainActivity;
import com.exam.natour.Model.AuthUser;
import com.exam.natour.R;
import com.google.android.material.snackbar.Snackbar;

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

                loginButton.setEnabled(false);
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                emailInput.setError(null);
                if(validateLoginInput(email,password)){
                    loginPageViewModel.login(view.getContext(),email,password);
                }else{
                    loginButton.setEnabled(true);
                }

            }
        });


        return view;
    }

    private boolean validateLoginInput(String email, String password) {
        boolean validated = true;

        if(email.length() == 0){
            this.emailInput.setError("Il campo email non può essere vuoto");
            validated = false;
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            this.emailInput.setError("Inserire una mail valida");
            validated = false;
        }
        if(password.length() == 0){
            this.passwordInput.setError("Il campo email non può essere vuoto");
            validated = false;
        }
        return validated;
    }

    private void goToSignupPage(){
        //getParentFragmentManager().beginTransaction().replace(R.id.AuthContainer, new LoginPage()).commit();
        Toast.makeText(getContext(),"Ancora non impostata",Toast.LENGTH_LONG).show();
        new AlertDialog.Builder(getContext())
                .setTitle("Attenzione")
                .setMessage("Non ancora implementata")
                .show();
    }



}