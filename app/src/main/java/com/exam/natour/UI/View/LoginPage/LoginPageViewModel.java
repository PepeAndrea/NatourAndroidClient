package com.exam.natour.UI.View.LoginPage;

import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.exam.natour.Network.Repository.AuthRepository;

public class LoginPageViewModel extends ViewModel {

    private AuthRepository authRepository;


    public LoginPageViewModel() {
        this.authRepository = AuthRepository.getInstance();
    }


    public void login(String email, String password) {
        this.authRepository.login(email, password);
    }
}
