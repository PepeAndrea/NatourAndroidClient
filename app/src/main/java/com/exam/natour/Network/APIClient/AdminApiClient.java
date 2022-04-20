package com.exam.natour.Network.APIClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.exam.natour.Activity.AuthActivity;
import com.exam.natour.Activity.MainActivity;
import com.exam.natour.Model.LoginResponse.LoginResponse;
import com.exam.natour.Network.APICaller;
import com.exam.natour.Network.RetroInstance;
import com.exam.natour.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminApiClient {

    private APICaller apiCaller;
    private static AdminApiClient adminApiClient;

    public AdminApiClient() {
        this.apiCaller = RetroInstance.getRetrofitClient().create(APICaller.class);
    }

    public static AdminApiClient getInstance(){
        if(adminApiClient == null){
            adminApiClient = new AdminApiClient();
        }
        return adminApiClient;
    }

    public void sendEmail(Context context, String title, String content) {
        Call<JSONObject> call = apiCaller.sendEmail(title, content);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    if(response.isSuccessful()){
                        Log.i("API 200","Invio email riuscito correttamente");
                        new AlertDialog.Builder(context)
                                .setTitle("Email inviata con successo")
                                .setMessage("Congratulazioni, il messaggio verrà consegnato a tutti gli iscritti.\n")
                                .setOnCancelListener(dialogInterface -> ((Activity) context).finish())
                                .setNeutralButton("Continua",(dialogInterface, i) ->((Activity) context).finish())
                                .show();
                    }else if(response.code() == 422){
                        Log.i("API 422",new JSONObject(response.errorBody().string()).toString());
                        new AlertDialog.Builder(context)
                                .setTitle("Errore di inserimento")
                                .setMessage("Non è stato possibile inviare il messaggio.\nSi prega di riprovare.")
                                .show();
                        ((Activity) context).findViewById(R.id.sendEmailBtn).setEnabled(true);
                        ((Activity) context).findViewById(R.id.goBackBtn).setEnabled(true);
                    }else if(response.code() == 401){
                        Log.i("API 401",new JSONObject(response.errorBody().string()).toString());
                        Log.i("API 401","Il token fornito è scaduto o non è valido");
                        Intent i = new Intent(context, AuthActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }else if(response.code() == 500|| response.code() == 502){
                        Log.i("API 500/502",new JSONObject(response.errorBody().string()).getString("message"));
                        new AlertDialog.Builder(context)
                                .setTitle("Errore con il server remoto")
                                .setMessage("Attualmente la piattaforma non è disponibile.\nRiprovare più tardi.")
                                .show();
                        ((Activity) context).findViewById(R.id.sendEmailBtn).setEnabled(true);
                        ((Activity) context).findViewById(R.id.goBackBtn).setEnabled(true);
                    }
                }catch (JSONException | IOException e) {
                    Log.e("Errore durante chiamata al backend","Messaggio di errore: "+e.getMessage());
                    ((Activity) context).findViewById(R.id.sendEmailBtn).setEnabled(true);
                    ((Activity) context).findViewById(R.id.goBackBtn).setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.i("API Error",t.toString());
                new AlertDialog.Builder(context)
                        .setTitle("Errore con il server remoto")
                        .setMessage("Attualmente la piattaforma non è disponibile.\nRiprovare più tardi.")
                        .show();
                ((Activity) context).findViewById(R.id.sendEmailBtn).setEnabled(true);
                ((Activity) context).findViewById(R.id.goBackBtn).setEnabled(true);
            }
        });
    }
}
