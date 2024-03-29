package com.exam.natour.UI.View.Auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.exam.natour.R;


public class Signup extends Fragment {

    private AuthViewModel authViewModel;
    Button signupButton,goToLoginButton,backButton;
    EditText emailInput,usernameInput,passwordInput,passwordConfirmationInput;

    public Signup() {
        // Required empty public constructor
    }


    public static Signup newInstance(String param1, String param2) {
        Signup fragment = new Signup();
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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        backButton = view.findViewById(R.id.back_button_signup);
        signupButton = view.findViewById(R.id.signup_button);
        goToLoginButton = view.findViewById(R.id.go_to_login_button);
        emailInput = view.findViewById(R.id.email_signup_input);
        usernameInput = view.findViewById(R.id.username_signup_input);
        passwordInput = view.findViewById(R.id.password_signup_input);
        passwordConfirmationInput = view.findViewById(R.id.confirm_password_signup_input);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("SignUpPage", "Torno alla pagina main di autenticazione");
                goToAuthPage();
            }
        });
        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("SignUpPage", "Passo alla pagina di login");
                goToLoginPage();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("SignUpPage", "Provo ad effettuare la registrazione");
                signupButton.setEnabled(false);
                String email = emailInput.getText().toString();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String passwordConfirmation= passwordConfirmationInput.getText().toString();
                if(validateSignupInput(email,username,password,passwordConfirmation)){
                    authViewModel.signup(view.getContext(),email,username,password,passwordConfirmation);
                }else{
                    Log.e("Validazione input registrazione", "I campi non risultano validi");
                    signupButton.setEnabled(true);
                }
            }
        });

        return view;
    }

    private boolean validateSignupInput(String email, String username, String password, String passwordConfirmation) {
        boolean validated = true;

        if(email.length() == 0){
            this.emailInput.setError("Il campo Email non può essere vuoto");
            validated = false;
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            this.emailInput.setError("Inserire una Email valida");
            validated = false;
        }
        if(username.length() == 0){
            this.usernameInput.setError("Il campo Username non può essere vuoto");
            validated = false;
        }
        if(password.length() == 0){
            this.passwordInput.setError("Il campo Password non può essere vuoto");
            validated = false;
        }else if(password.length() < 8){
            this.passwordInput.setError("Il campo Password deve avere almeno 8 caratteri");
            validated = false;
        }
        if(passwordConfirmation.length() == 0){
            this.passwordConfirmationInput.setError("Il campo Conferma password non può essere vuoto");
            validated = false;
        }else if(!passwordConfirmation.equals(password)){
            this.passwordConfirmationInput.setError("Il campo Conferma password non coincide con la Password");
            validated = false;
        }
        return validated;
    }

    private void goToLoginPage(){
        getParentFragmentManager().beginTransaction().replace(R.id.AuthContainer, new Login()).commit();
    }

    private void goToAuthPage(){
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else {
            getParentFragmentManager().beginTransaction().replace(R.id.AuthContainer, new AuthMainPage()).commit();
        }
    }


}