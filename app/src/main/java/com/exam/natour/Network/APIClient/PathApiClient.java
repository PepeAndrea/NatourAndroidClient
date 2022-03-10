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
import com.exam.natour.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                    Log.i("API 200","Ho ricevuto i percorsi salvati");
                    mPaths.setValue(response.body().getData().getPaths());
                }else if(response.code() == 401){
                    Log.i("API 401","Il token fornito è scaduto o non è valido");
                    Intent i = new Intent(context, AuthActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
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
                    Log.i("API 200","Ho ricevuto i dettagli del percorso");
                    mPathDetail.setValue(response.body().getPathDetail());
                }else if(response.code() == 401){
                    Log.i("API 401","Il token fornito è scaduto o non è valido");
                    Intent i = new Intent(context, AuthActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
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
        Map<String,String> coordinates = new LinkedHashMap<>();
        Map<String,String> interestPoints = new HashMap<>();

        if (newPath.getCoordinates() != null){
            for (int i = 0;i<newPath.getCoordinates().size();i++){
                coordinates.put("coordinates["+i+"][latitude]",newPath.getCoordinates().get(i).getLatitude());
                coordinates.put("coordinates["+i+"][longitude]",newPath.getCoordinates().get(i).getLongitude());
            }
        }

        if (newPath.getInterestPoints() != null){
            for (int i = 0;i<newPath.getInterestPoints().size();i++){
                interestPoints.put("interest_points["+i+"][title]",newPath.getInterestPoints().get(i).getTitle());
                interestPoints.put("interest_points["+i+"][description]",newPath.getInterestPoints().get(i).getDescription());
                interestPoints.put("interest_points["+i+"][category]",newPath.getInterestPoints().get(i).getCategory());
                interestPoints.put("interest_points["+i+"][latitude]",newPath.getInterestPoints().get(i).getLatitude());
                interestPoints.put("interest_points["+i+"][longitude]",newPath.getInterestPoints().get(i).getLongitude());
            }
        }


        Call<PathDetailResponse> call = service.savePath(
                newPath.getTitle(),
                newPath.getDescription(),
                newPath.getLocation(),
                newPath.getDifficulty(),
                newPath.getDisability(),
                newPath.getLength(),
                newPath.getDuration(),
                coordinates,
                interestPoints
        );
        call.enqueue(new Callback<PathDetailResponse>() {
            @Override
            public void onResponse(Call<PathDetailResponse> call, Response<PathDetailResponse> response) {
                try {
                    if(response.isSuccessful()){
                        Log.i("API 200","Percorso caricato correttamente");
                        new AlertDialog.Builder(context)
                                .setTitle("Percorso caricato con successo")
                                .setMessage("Congratulazioni, il percorso è stato caricato correttamente.\n")
                                .setOnCancelListener(dialogInterface -> ((Activity) context).finish())
                                .setNeutralButton("Continua",(dialogInterface, i) ->((Activity) context).finish())
                                .show();
                    }else if(response.code() == 401){
                        Log.i("API 401","Il token fornito è scaduto o non è valido");
                        Intent i = new Intent(context, AuthActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }else if(response.code() == 422){
                        Log.i("API 422",new JSONObject(response.errorBody().string()).toString());
                        new AlertDialog.Builder(context)
                                .setTitle("Errore di inserimento")
                                .setMessage("Non è stato possibile salvare il percorso.\nSi prega di riprovare.")
                                .show();
                        ((Activity) context).findViewById(R.id.InsertPath_Save).setEnabled(true);
                    }else if(response.code() == 500|| response.code() == 502){
                        Log.i("API 500/502",new JSONObject(response.errorBody().string()).toString());
                        new AlertDialog.Builder(context)
                                .setTitle("Errore con il server remoto")
                                .setMessage("Attualmente la piattaforma non è disponibile.\nRiprovare più tardi.")
                                .show();
                        ((Activity) context).findViewById(R.id.InsertPath_Save).setEnabled(true);
                    }
                }catch (JSONException | IOException e) {
                    Log.e("Errore durante chiamata al backend","Messaggio di errore: "+e.getMessage());
                    ((Activity) context).findViewById(R.id.InsertPath_Save).setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<PathDetailResponse> call, Throwable t) {
                Log.i("API Error",t.toString());
                ((Activity) context).findViewById(R.id.InsertPath_Save).setEnabled(true);
            }
        });

    }

    public void filterPathResult(Context context, String raggio, String distanza, String durata, boolean disability, List<String> difficultiesOptionSelected, LatLng currentPos) {
        Map<String,String> difficulties = new HashMap<>();
        Map<String, Double> userCoordinates = new HashMap<>();

        if (difficultiesOptionSelected != null){
            for (int i = 0;i<difficultiesOptionSelected.size();i++){
                Log.i("Difficoltà nell'array", "filterPathResult: "+difficultiesOptionSelected.get(i));
                difficulties.put("difficulty["+i+"]",difficultiesOptionSelected.get(i));
            }
        }

        if (currentPos != null){
                userCoordinates.put("userCoordinate[latitude]",currentPos.latitude);
                userCoordinates.put("userCoordinate[longitude]", currentPos.longitude);
        }

        Call<PathsResponse> call = service.filterPath(
               raggio,
               distanza,
               durata,
               disability ? 1 : null,
               difficulties,
               userCoordinates
        );
        call.enqueue(new Callback<PathsResponse>() {
            @Override
            public void onResponse(Call<PathsResponse> call, Response<PathsResponse> response) {
                if(response.isSuccessful()){
                    Log.i("API 200","Ho ricevuto i percorsi filtrati");
                    mPaths.setValue(response.body().getData().getPaths());
                }else if(response.code() == 401){
                    Log.i("API 401","Il token fornito è scaduto o non è valido");
                    Intent i = new Intent(context, AuthActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
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
}
