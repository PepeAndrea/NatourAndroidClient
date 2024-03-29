package com.exam.natour.Network.APIClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.exam.natour.Activity.AuthActivity;
import com.exam.natour.Activity.MainActivity;
import com.exam.natour.Model.AuthUser;
import com.exam.natour.Model.LoginResponse.LoginResponse;
import com.exam.natour.Network.APICaller;
import com.exam.natour.Network.RetroInstance;
import com.exam.natour.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthApiClient {

    private APICaller apiCaller;
    private static AuthApiClient authApiClient;
    private SharedPreferences sharedPreferences;
    private FirebaseAnalytics mFirebaseAnalytics;

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Call<LoginResponse> call = apiCaller.login(email, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                try {
                    if(response.isSuccessful()){
                        Log.i("API 200","Login riuscito correttamente per: "+email);
                        saveLogin(context,response.body());
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Login riuscito");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                        context.startActivity(new Intent(context, MainActivity.class));
                        ((Activity) context).finish();
                    }else if(response.code() == 422){
                        Log.i("API 422",new JSONObject(response.errorBody().string()).toString());
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Login fallito");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                        new AlertDialog.Builder(context)
                                .setTitle("Si è verificato un errore")
                                .setMessage("Controlla i dati inseriti e riprova.")
                                .show();
                    }else if(response.code() == 401){
                        Log.i("API 401",new JSONObject(response.errorBody().string()).toString());
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Login fallito");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
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
        Log.i("AuthApiClient", "Salvo nelle preferenze il token di accesso dell'utente appena autenticato");
        AuthUser authUser = AuthUser.getInstance();
        authUser.setEmail(response.getData().getUser().getEmail());
        authUser.setName(response.getData().getUser().getName());
        authUser.setToken(response.getData().getToken());
        authUser.setAdmin((response.getData().getUser().isAdmin() != null) ? response.getData().getUser().isAdmin() : 0);
        sharedPreferences = context.getSharedPreferences("AUTH",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Token",response.getData().getToken());
        editor.apply();
    }

    public void checkSavedToken(Context context, String token) {
        Log.i("AuthApiClient", "Verifico token salvato");
        Call<LoginResponse> call = apiCaller.checkToken();
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    Log.i("API 200","Login riuscito correttamente per il token: "+token);
                    setSavedUser(response.body());
                }else if(response.code() == 404){
                    deleteLogin(context);
                    Log.i("API 404","Il token fornito è scaduto o non è valido");
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.i("API Error",t.toString());
                new AlertDialog.Builder(context)
                        .setTitle("Errore con il server remoto")
                        .setMessage("Attualmente la piattaforma non è disponibile.\nRiprovare più tardi.")
                        .show();
            }
        });
    }

    private void setSavedUser(LoginResponse response) {
        AuthUser authUser = AuthUser.getInstance();
        authUser.setEmail(response.getData().getUser().getEmail());
        authUser.setName(response.getData().getUser().getName());
        authUser.setToken(response.getData().getToken());
        authUser.setAdmin((response.getData().getUser().isAdmin() != null) ? response.getData().getUser().isAdmin() : 0);

    }

    public void signup(Context context, String email, String username, String password, String passwordConfirmation) {
        Call<LoginResponse> call = apiCaller.signup(email,username,password,passwordConfirmation);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                try {
                    if(response.isSuccessful()){
                        Log.i("API 200","Registrazione avvenuta correttamente per: "+email);
                        saveLogin(context,response.body());
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Registrazione riuscita");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
                        context.startActivity(new Intent(context, MainActivity.class));
                        ((Activity) context).finish();
                    }else if(response.code() == 422){
                        Log.i("API 422",new JSONObject(response.errorBody().string()).toString());
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Registrazione fallita");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
                        new AlertDialog.Builder(context)
                                .setTitle("Errore di registrazione")
                                .setMessage("L'indirizzo email risulta già registrato!\nAccedere premendo il tasto \"Accedi\"")
                                .show();
                    }else if(response.code() == 500|| response.code() == 502){
                        Log.i("API 500/502",new JSONObject(response.errorBody().string()).toString());
                        new AlertDialog.Builder(context)
                                .setTitle("Errore con il server remoto")
                                .setMessage("Attualmente la piattaforma non è disponibile.\nRiprovare più tardi.")
                                .show();
                    }
                    ((Activity) context).findViewById(R.id.signup_button).setEnabled(true);
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
                ((Activity) context).findViewById(R.id.signup_button).setEnabled(true);
            }
        });
    }

    public void loginProvider(Context context, String provider, String token) {
        Call<LoginResponse> call = apiCaller.loginProvider(provider, token);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                try {
                    if(response.isSuccessful()){
                        Log.i("API 200","Login con provider "+provider+" riuscito correttamente con token: "+token);
                        saveLogin(context,response.body());
                        context.startActivity(new Intent(context, MainActivity.class));
                        ((Activity) context).finish();
                    }else if(response.code() == 422){
                        Log.i("API 422",new JSONObject(response.errorBody().string()).toString());
                        new AlertDialog.Builder(context)
                                .setTitle("Errore con il trasferimento dei dati")
                                .setMessage("Si è verificato un errore di comnicazione con il provider, riprova più tardi")
                                .show();
                    }else if(response.code() == 500|| response.code() == 502){
                        Log.i("API 500/502",new JSONObject(response.errorBody().string()).toString());
                        new AlertDialog.Builder(context)
                                .setTitle("Errore con il server remoto")
                                .setMessage("Attualmente la piattaforma non è disponibile.\nRiprovare più tardi.")
                                .show();
                    }
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
            }
        });
    }

    public void logout(Context context) {
        Call<JSONObject> call = apiCaller.logout();
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if(response.isSuccessful()){
                    Log.i("API 200","Logout eseguito corretamente per l'utente: "+AuthUser.getInstance().getEmail());
                    deleteLogin(context);
                    context.startActivity(new Intent(context, AuthActivity.class));
                    ((Activity) context).finish();
                }else if(response.code() == 401){
                    Log.i("API 401","Il token fornito è scaduto o non è valido");
                    deleteLogin(context);
                    context.startActivity(new Intent(context, AuthActivity.class));
                    ((Activity) context).finish();
                }else if(response.code() == 500|| response.code() == 502){
                    try {
                        Log.i("API 500/502",new JSONObject(response.errorBody().string()).toString());
                    } catch (JSONException | IOException e) {
                        Log.e("Errore durante chiamata al backend","Messaggio di errore: "+e.getMessage());
                    }
                    new AlertDialog.Builder(context)
                            .setTitle("Errore con il server remoto")
                            .setMessage("Attualmente la piattaforma non è disponibile.\nRiprovare più tardi.")
                            .show();
                }

            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.i("API Error",t.toString());
                new AlertDialog.Builder(context)
                        .setTitle("Errore con il server remoto")
                        .setMessage("Attualmente la piattaforma non è disponibile.\nRiprovare più tardi.")
                        .show();
            }
        });
    }

    private void deleteLogin(Context context) {
        AuthUser.getInstance().inizialize();
        sharedPreferences = context.getSharedPreferences("AUTH",Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Token")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("Token");
            editor.apply();
        }
    }
}
