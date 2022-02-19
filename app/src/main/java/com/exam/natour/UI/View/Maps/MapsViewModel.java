package com.exam.natour.UI.View.Maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.exam.natour.Model.PathDetailResponse.PathDetail;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;

public class MapsViewModel extends ViewModel {

    private FusedLocationProviderClient fusedLocationClient;
    private boolean isUserRecording;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private PathDetail createdPath;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.i("Coordinate registrate","Lat:"+locationResult.getLastLocation().getLatitude()+" Long:"+locationResult.getLastLocation().getLongitude());
        }
    };


    public MapsViewModel() {
        locationRequest = LocationRequest.create().setInterval(10000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.i("Coordinate viewmode","creato");
    }

    public boolean isUserRecording() {
        return isUserRecording;
    }

    private void setUserRecording(boolean userRecording) {
        isUserRecording = userRecording;
    }

    public void startLocationUpdates(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Servizio registrazione percorso non disponibile","Non è possibile avviare il servizio di registrazione in mancaza dei permessi necessari");
            return;
        }
        if (fusedLocationClient == null){
            this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        }
        setUserRecording(true);
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }



}