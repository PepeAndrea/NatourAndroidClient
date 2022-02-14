package com.exam.natour.UI.View.LoginPage;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.exam.natour.Activity.AuthActivity;
import com.exam.natour.Network.Repository.AuthRepository;

public class LoginPageViewModel extends ViewModel {

    private AuthRepository authRepository;


    public LoginPageViewModel() {
        this.authRepository = AuthRepository.getInstance();
    }


    public void login(Context context, String email, String password) {
        this.authRepository.login(context,email, password);
    }

    public void checkSavedToken(Context context, String token) {
        this.authRepository.checkSavedToken(context,token);
    }
}
