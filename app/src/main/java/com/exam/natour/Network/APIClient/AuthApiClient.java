package com.exam.natour.Network.APIClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;

import com.exam.natour.Activity.MainActivity;
import com.exam.natour.Model.AuthUser;
import com.exam.natour.Model.LoginResponse.LoginResponse;
import com.exam.natour.Network.APICaller;
import com.exam.natour.Network.RetroInstance;
import com.exam.natour.R;
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
    private SharedPreferences sharedPreferences;

    public AuthApiClient() {
        this.apiCaller = RetroInstance.getRetrofitClient().create(APICaller.class);
    }

    public static AuthApiClient getInstance(){
        if(authApiClient == null){
            authApiClient = new AuthApiClient();
        }
        return authApiClient;
    }


    public void login(Context context, String email, String password){
        Call<LoginResponse> call = apiCaller.login(email, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                try {
                    if(response.isSuccessful()){
                        Log.i("API 200","Login riuscito correttamente per: "+email);
                        saveLogin(context,response.body());
                        context.startActivity(new Intent(context, MainActivity.class));
                        ((Activity) context).finish();
                    }else if(response.code() == 422){
                        Log.i("API 422",new JSONObject(response.errorBody().string()).getJSONObject("errors").toString());
                        new AlertDialog.Builder(context)
                                .setTitle("Si è verificato un errore")
                                .setMessage("La invitiamo a controllare i dati inseriti e riprovare")
                                .show();
                    }else if(response.code() == 401){
                        Log.i("API 401",new JSONObject(response.errorBody().string()).getString("message"));
                        new AlertDialog.Builder(context)
                                .setTitle("Acccesso non riuscito")
                                .setMessage("Le credenziali inserite non sono corrette!")
                                .show();
                    }else if(response.code() == 500|| response.code() == 502){
                        Log.i("API 500/502",new JSONObject(response.errorBody().string()).getString("message"));
                        new AlertDialog.Builder(context)
                                .setTitle("Errore con il server remoto")
                                .setMessage("Attualmente la piattaforma non è disponibile.\nRiprovare più tardi.")
                                .show();
                    }
                    ((Activity) context).findViewById(R.id.login_button).setEnabled(true);
                }catch (JSONException | IOException e) {
                    Log.e("Errore durante chiamata al backend","Messaggio di errore: "+e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.i("API Error",t.toString());
                new AlertDialog.Builder(context)
                        .setTitle("Errore con il server remoto")
                        .setMessage("Attualmente la piattaforma non è disponibile.\nRiprovare più tardi.")
                        .show();
                ((Activity) context).findViewById(R.id.login_button).setEnabled(true);
            }
        });
    }

    private void saveLogin(Context context, LoginResponse response) {
        AuthUser authUser = AuthUser.getInstance();
        authUser.setEmail(response.getData().getUser().getEmail());
        authUser.setName(response.getData().getUser().getName());
        authUser.setToken(response.getData().getToken());
        sharedPreferences = context.getSharedPreferences("AUTH",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Token",response.getData().getToken());
        editor.apply();
    }

    public void checkSavedToken(Context context, String token) {
        AuthUser.getInstance().setToken(token);
        Call<LoginResponse> call = apiCaller.checkToken();
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    Log.i("API 200","Login riuscito correttamente per il token: "+token);
                    setSavedUser(context,response.body());
                    context.startActivity(new Intent(context, MainActivity.class));
                    ((Activity) context).finish();
                }else if(response.code() == 404){
                    Log.i("API 404","Il token fornito è scaduto on non è valido");
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.i("API Error",t.toString());
                new AlertDialog.Builder(context)
                        .setTitle("Errore con il server remoto")
                        .setMessage("Attualmente la piattaforma non è disponibile.\nRiprovare più tardi.")
                        .show();
                ((Activity) context).findViewById(R.id.login_button).setEnabled(true);
            }
        });
    }

    private void setSavedUser(Context context, LoginResponse response) {
        AuthUser authUser = AuthUser.getInstance();
        authUser.setEmail(response.getData().getUser().getEmail());
        authUser.setName(response.getData().getUser().getName());
        authUser.setToken(response.getData().getToken());
    }
}
