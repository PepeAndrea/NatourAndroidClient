package com.exam.natour.UI.View.Maps;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.service.controls.templates.ControlButton;
import android.util.Log;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.exam.natour.Model.LiveRecordingData;
import com.exam.natour.Model.PathDetailResponse.Coordinate;
import com.exam.natour.Model.PathDetailResponse.InterestPoint;
import com.exam.natour.Model.PathDetailResponse.PathDetail;
import com.exam.natour.Service.PathRecorderService;
import com.exam.natour.UI.View.InsertPath.InsertPathActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;

public class MapsViewModel extends ViewModel{

    private PathDetail pathUpdateContainer;
    private MutableLiveData<PathDetail> createdPath;


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.i("Coordinate registrate","Lat:"+locationResult.getLastLocation().getLatitude()+" Long:"+locationResult.getLastLocation().getLongitude());
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pathUpdateContainer.setInterestPoints(LiveRecordingData.getInstance().getInterestPoints());
                pathUpdateContainer.setCoordinates(LiveRecordingData.getInstance().getCoordinates());
                createdPath.setValue(pathUpdateContainer);
            }
    };


    public MapsViewModel() {
        createdPath = new MutableLiveData<>();
    }

    public void setReceiver(Context context){
        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver, new IntentFilter("PathRecordingUpdate"));
    }

    public void unsetReceiver(Context context){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
    }


    public String checkUserRecording(Context context) {
        if (isServiceRunning(context)){
            this.pathUpdateContainer = new PathDetail();
            return "gpsRecording";
        }
        if (LiveRecordingData.getInstance().isManualRecording()){
            this.pathUpdateContainer = new PathDetail();
            this.pathUpdateContainer.setCoordinates(LiveRecordingData.getInstance().getCoordinates());
            this.pathUpdateContainer.setInterestPoints(LiveRecordingData.getInstance().getInterestPoints());
            this.createdPath.setValue(this.pathUpdateContainer);
            return "manualRecording";
        }
        return "";
    }

    private boolean isServiceRunning(Context context){
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals("com.exam.natour.Service.PathRecorderService")){
                Log.i("Service PathRecorder","Il service è attivo");
                return true;
            }
        }
        Log.i("PathRecorder","Il service è disattivato");

        return false;
    }

    public void startPathRecording(Context context){
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(new Intent(context, PathRecorderService.class));
        else
            context.startService(new Intent(context, PathRecorderService.class));
         */
        LiveRecordingData.getInstance().destroy();
        pathUpdateContainer = new PathDetail();
        LiveRecordingData.getInstance().setStartTime();
        context.startService(new Intent(context, PathRecorderService.class));
    }

    public void stopPathRecording(Context context){
        context.stopService(new Intent(context, PathRecorderService.class));
    }

    public void saveRecordedPath(Context context,String location) {
        this.stopPathRecording(context);
        LiveRecordingData.getInstance().setEndTime();
        PathDetail newPath = createdPath.getValue();
        if (newPath != null){
            newPath.setLocation(location);
            if (newPath.getCoordinates() == null){
                new AlertDialog.Builder(context)
                        .setTitle("Errore")
                        .setMessage("Impossibile salvare percorso senza coordinate")
                        .show();
                return;
            }
            newPath.calculateLength();
            newPath.setDuration(Duration.between(LiveRecordingData.getInstance().getStartTime(), LiveRecordingData.getInstance().getEndTime()).toMillis());
            String jsonParsedPath = new Gson().toJson(newPath);
            LiveRecordingData.getInstance().destroy();
            pathUpdateContainer = null;
            Intent intent = new Intent(context, InsertPathActivity.class);
            intent.putExtra("Path",jsonParsedPath);
            context.startActivity(intent);
        }
    }

    public void startManualPathRecording(){
        LiveRecordingData.getInstance().destroy();
        pathUpdateContainer = new PathDetail();
        createdPath.setValue(pathUpdateContainer);
        LiveRecordingData.getInstance().setManualRecording(true);
    }

    public void saveManualPathRecording(Context context,String location){
        PathDetail newPath = createdPath.getValue();
        if (newPath != null){
            newPath.setLocation(location);
            if (newPath.getCoordinates() == null){
                new AlertDialog.Builder(context)
                        .setTitle("Errore")
                        .setMessage("Impossibile salvare percorso senza coordinate")
                        .show();
                return;
            }
            newPath.calculateLength();
            String jsonParsedPath = new Gson().toJson(newPath);
            LiveRecordingData.getInstance().destroy();
            pathUpdateContainer = null;
            Intent intent = new Intent(context, InsertPathActivity.class);
            intent.putExtra("Path",jsonParsedPath);
            context.startActivity(intent);
        }
    }


    public MutableLiveData<PathDetail> getCreatedPath() {
        return this.createdPath;
    }


    public void addInterestPoint(InterestPoint interestPoint) {
        LiveRecordingData.getInstance().addInterestPoint(interestPoint);
        this.createdPath.getValue().setInterestPoints(LiveRecordingData.getInstance().getInterestPoints());
    }

    public void addCoordinate(Coordinate coordinate) {
        LiveRecordingData.getInstance().addCoordinate(coordinate);
        this.createdPath.getValue().setCoordinates(LiveRecordingData.getInstance().getCoordinates());
    }


    public void uploadGpxPath(Context context, String path){
        GPXParser parser = new GPXParser();
        Gpx parsedGpx = null;
        try{
            InputStream input = new FileInputStream(path);
            parsedGpx = parser.parse(input);

            if (parsedGpx == null) {
                new AlertDialog.Builder(context)
                        .setTitle("Errore apertura file")
                        .setMessage("Non è stato possibile aprire il file!\n\nSi prega di riprovare")
                        .show();
            } else {
                LiveRecordingData.getInstance().destroy();
                PathDetail newPath = new PathDetail();

                if (parsedGpx.getTracks().get(0)!=null && parsedGpx.getTracks().get(0).getTrackSegments() != null){
                    for (int i = 0;i<parsedGpx.getTracks().get(0).getTrackSegments().size();i++){
                        if (i%10 == 0 && parsedGpx.getTracks().get(0).getTrackSegments().get(i).getTrackPoints() != null){
                            parsedGpx.getTracks().get(0).getTrackSegments().get(i).getTrackPoints().forEach(trackPoint ->
                                    LiveRecordingData.getInstance().addCoordinate(
                                            new Coordinate(String.valueOf(trackPoint.getLatitude()),
                                                    String.valueOf(trackPoint.getLongitude())))
                            );
                        }
                    }
                }


                if (LiveRecordingData.getInstance().getCoordinates() != null){
                    newPath.setLocation(this.getGpxCity(context,Double.valueOf(LiveRecordingData.getInstance().getCoordinates().get(0).getLatitude()),Double.valueOf(LiveRecordingData.getInstance().getCoordinates().get(0).getLongitude())));
                }

                String jsonParsedPath = new Gson().toJson(newPath);
                Intent intent = new Intent(context, InsertPathActivity.class);
                intent.putExtra("Path",jsonParsedPath);
                intent.putExtra("updateCoordinateAfter","updateCoordinateAfter");
                Log.i("JSON", "uploadGpxPath: "+jsonParsedPath);
                context.startActivity(intent);
            }

        } catch (IOException | XmlPullParserException e) {
            new AlertDialog.Builder(context)
                    .setTitle("Si è verificato un errore")
                    .setMessage("Si è verificato un errore durante la lettura del file.\n\nSi prega di riprovare")
                    .show();
        }

    }

    private String getGpxCity(Context context,Double latitude,Double longitude){
        Geocoder gcd = new Geocoder(context,
                Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            return addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
            return "null";
        }
    }

}