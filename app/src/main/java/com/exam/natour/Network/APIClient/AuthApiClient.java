package com.exam.natour.Network.APIClient;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.exam.natour.Model.AuthUser;
import com.exam.natour.Model.LoginResponse.LoginResponse;
import com.exam.natour.Network.APICaller;
import com.exam.natour.Network.RetroInstance;
import com.exam.natour.UI.View.LoginPage.LoginPage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthApiClient {

    private APICaller apiCaller;
    private static AuthApiClient authApiClient;

    public AuthApiClient() {
        this.apiCaller = RetroInstance.getRetrofitClient().create(APICaller.class);
    }

    public static AuthApiClient getInstance(){
        if(authApiClient == null){
            authApiClient = new AuthApiClient();
        }
        return authApiClient;
    }


    public void login(String email,String password){
        Call<LoginResponse> call = apiCaller.login(email, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                try {
                    if(response.isSuccessful()){
                        Log.i("API 200","Login riuscito correttamente per: "+email);
                        AuthUser authUser = AuthUser.getInstance();
                        authUser.setEmail(response.body().getData().getUser().getEmail());
                        authUser.setName(response.body().getData().getUser().getName());
                        authUser.setToken(response.body().getData().getToken());
                    }else if(response.code() == 422){
                        Log.i("API 422",new JSONObject(response.errorBody().string()).getJSONObject("errors").toString());
                    }else if(response.code() == 401){
                        Log.i("API 401",new JSONObject(response.errorBody().string()).getString("message"));
                    }
                }catch (JSONException | IOException e) {
                    Log.e("Errore durante chiamata al backend","Messaggio di errore: "+e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.i("API Error",t.toString());
            }
        });
    }



}
