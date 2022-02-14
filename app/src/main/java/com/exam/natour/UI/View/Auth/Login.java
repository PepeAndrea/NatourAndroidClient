package com.exam.natour.UI.View.Auth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.exam.natour.R;

public class Login extends Fragment {

    private AuthViewModel authViewModel;
    Button loginButton,goToSignUpButton,backButton;
    EditText emailInput,passwordInput;

    public Login() {
        // Required empty public constructor
    }

    public static Login newInstance() {
        Login fragment = new Login();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        backButton = view.findViewById(R.id.back_button_login);
        loginButton = view.findViewById(R.id.login_button);
        goToSignUpButton = view.findViewById(R.id.go_to_signup_button);
        emailInput = view.findViewById(R.id.email_login_input);
        passwordInput = view.findViewById(R.id.passwod_login_input);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAuthPage();
            }
        });

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
                if(validateLoginInput(email,password)){
                    authViewModel.login(view.getContext(),email,password);
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
            this.passwordInput.setError("Il campo password non può essere vuoto");
            validated = false;
        }
        return validated;
    }

    private void goToSignupPage(){
        getParentFragmentManager().beginTransaction().replace(R.id.AuthContainer, new Signup()).commit();
    }

    private void goToAuthPage(){
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else {
            getParentFragmentManager().beginTransaction().replace(R.id.AuthContainer, new AuthMainPage()).commit();
        }
    }


}