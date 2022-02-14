package com.exam.natour.Network.Repository;

import android.content.Context;
import android.view.LayoutInflater;

import com.exam.natour.Network.APIClient.AuthApiClient;

public class AuthRepository {

    private AuthApiClient authApiClient;
    private static AuthRepository authRepository;


    public AuthRepository() {
        this.authApiClient = AuthApiClient.getInstance();
    }

    public static AuthRepository getInstance(){
        if(authRepository == null){
            authRepository = new AuthRepository();
        }
        return authRepository;
    }

    public void login(Context context, String email, String password){
        this.authApiClient.login(context,email, password);
    }

    public void checkSavedToken(Context context, String token) {
        this.authApiClient.checkSavedToken(context,token);
    }
}
