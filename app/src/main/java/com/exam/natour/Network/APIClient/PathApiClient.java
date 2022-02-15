package com.exam.natour.Network.APIClient;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    public PathApiClient() {
        mPaths = new MutableLiveData<>();
        this.service = RetroInstance.getRetrofitClient().create(APICaller.class);
    }

    public static PathApiClient getInstance(){
        if(pathApiClient == null){
            pathApiClient = new PathApiClient();
        }
        return pathApiClient;
    }

    private void LoadPath(){
        Call<PathsResponse> call = service.getAllPaths();
        call.enqueue(new Callback<PathsResponse>() {
            @Override
            public void onResponse(Call<PathsResponse> call, Response<PathsResponse> response) {
                try {
                    if(response.isSuccessful()){
                        mPaths.setValue(response.body().getData().getPaths());
                    }else if(response.code() == 422){
                        Log.i("API 422",new JSONObject(response.errorBody().string()).toString());
                    }else if(response.code() == 401){
                        Log.i("API 401",new JSONObject(response.errorBody().string()).toString());
                    }
                }catch (JSONException | IOException e) {
                    Log.e("Errore","Messaggio di errore: "+e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PathsResponse> call, Throwable t) {
                Log.i("API Error",t.toString());
            }
        });
    }

    public LiveData<List<Path>> getPaths(){
        this.LoadPath();
        return mPaths;
    }


}
