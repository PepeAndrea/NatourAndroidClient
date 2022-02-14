package com.exam.natour.UI.View.Auth;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.exam.natour.Network.Repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private AuthRepository authRepository;


    public AuthViewModel() {
        this.authRepository = AuthRepository.getInstance();
    }


    public void login(Context context, String email, String password) {
        this.authRepository.login(context,email, password);
    }

    public void checkSavedToken(Context context, String token) {
        this.authRepository.checkSavedToken(context,token);
    }

    public void signup(Context context, String email, String username, String password, String passwordConfirmation) {
        this.authRepository.signup(context,email,username,password,passwordConfirmation);
    }

    public void loginProvider(Context context, String provider, String token) {
        this.authRepository.loginProvider(context,provider,token);
    }
}
