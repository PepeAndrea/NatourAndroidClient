package com.exam.natour.Network.APIClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.exam.natour.Activity.AuthActivity;
import com.exam.natour.Model.PathDetailResponse.PathDetail;
import com.exam.natour.Model.PathDetailResponse.PathDetailResponse;
import com.exam.natour.Model.PathsResponse.Path;
import com.exam.natour.Model.PathsResponse.PathsResponse;
import com.exam.natour.Network.APICaller;
import com.exam.natour.Network.RetroInstance;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PathApiClient {

    private APICaller service;
    private static PathApiClient pathApiClient;
    private MutableLiveData<List<Path>> mPaths;
    private MutableLiveData<PathDetail> mPathDetail;

    public PathApiClient() {
        mPaths = new MutableLiveData<>();
        mPathDetail = new MutableLiveData<>();
        this.service = RetroInstance.getRetrofitClient().create(APICaller.class);
    }

    public static PathApiClient getInstance(){
        if(pathApiClient == null){
            pathApiClient = new PathApiClient();
        }
        return pathApiClient;
    }


    private void LoadPaths(Context context){
        Call<PathsResponse> call = service.getAllPaths();
        call.enqueue(new Callback<PathsResponse>() {
            @Override
            public void onResponse(Call<PathsResponse> call, Response<PathsResponse> response) {
                if(response.isSuccessful()){
                    mPaths.setValue(response.body().getData().getPaths());
                }else if(response.code() == 401){
                    Log.i("API 401","Il token fornito è scaduto o non è valido");
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
            public void onFailure(Call<PathsResponse> call, Throwable t) {
                Log.i("API Error",t.toString());
            }
        });
    }

    public LiveData<List<Path>> getPaths(Context context){
        this.LoadPaths(context);
        return mPaths;
    }

    private void LoadPathDetail(Context context, String id){
        Call<PathDetailResponse> call = service.getPath(id);
        call.enqueue(new Callback<PathDetailResponse>() {
            @Override
            public void onResponse(Call<PathDetailResponse> call, Response<PathDetailResponse> response) {
                if(response.isSuccessful()){
                    mPathDetail.setValue(response.body().getPathDetail());
                }else if(response.code() == 401){
                    Log.i("API 401","Il token fornito è scaduto o non è valido");
                    context.startActivity(new Intent(context, AuthActivity.class));
                    ((Activity) context).finish();
                }else if(response.code() == 404){
                    Log.i("API 404","Il percorso richiesto non è stato trovato");
                    new AlertDialog.Builder(context)
                            .setTitle("Percorso non trovato")
                            .setMessage("Non è stato possibile recuperare il percorso.\nSi prega di riprovare.")
                            .show();
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
            public void onFailure(Call<PathDetailResponse> call, Throwable t) {
                Log.i("API Error",t.toString());
            }
        });
    }

    public LiveData<PathDetail> getPathDetail(Context context, String id){
        mPathDetail = new MutableLiveData<>();
        this.LoadPathDetail(context,id);
        return mPathDetail;
    }


    public void savePath(PathDetail newPath, Context context) {

        Call<PathDetailResponse> call = service.savePath(
                newPath.getTitle(),
                newPath.getDescription(),
                newPath.getLocation(),
                newPath.getDifficulty(),
                newPath.getDisability(),
                newPath.getLength(),
                newPath.getDuration(),
                newPath.getCoordinates(),
                newPath.getInterestPoints()
        );
        call.enqueue(new Callback<PathDetailResponse>() {
            @Override
            public void onResponse(Call<PathDetailResponse> call, Response<PathDetailResponse> response) {
                try {
                    if(response.isSuccessful()){
                        //Risposta ok
                    }else if(response.code() == 401){
                        Log.i("API 401","Il token fornito è scaduto o non è valido");
                        context.startActivity(new Intent(context, AuthActivity.class));
                        ((Activity) context).finish();
                    }else if(response.code() == 422){
                        Log.i("API 422",new JSONObject(response.errorBody().string()).toString());
                        new AlertDialog.Builder(context)
                                .setTitle("Errore di inserimento")
                                .setMessage("Non è stato possibile salvare il percorso.\nSi prega di riprovare.")
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
            public void onFailure(Call<PathDetailResponse> call, Throwable t) {
                Log.i("API Error",t.toString());
            }
        });

    }
}
