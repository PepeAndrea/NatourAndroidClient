package com.exam.natour.Service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.exam.natour.BuildConfig;
import com.exam.natour.Model.PathDetailResponse.Coordinate;
import com.exam.natour.Model.PathDetailResponse.PathDetail;
import com.exam.natour.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PathRecorderService extends Service{

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private PathDetail createdPath;
    List<Coordinate> coordinates;
    private Instant startTime,endTime;


    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (createdPath == null)
                createdPath = new PathDetail();
            if (coordinates == null)
               coordinates  = new ArrayList<Coordinate>();

            coordinates.add(new Coordinate(String.valueOf(locationResult.getLastLocation().getLatitude()),String.valueOf(locationResult.getLastLocation().getLongitude())));
            createdPath.setCoordinates(coordinates);
            sendMessage(createdPath);
        }
    };


    public PathRecorderService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("PathRecorder","Service partito");
        super.onStartCommand(intent, flags, startId);
        locationRequest = LocationRequest.create().setInterval(10000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        this.startTime = Instant.now();
        this.startLocationUpdates();
        this.showNotificationAndStartForegroundService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("PathRecorder","Service interrotto");
        this.stopLocationUpdates();
        this.endTime = Instant.now();
        Log.i("Tempo registrato",calculateDuration(this.startTime,this.endTime));
    }

    private void showNotificationAndStartForegroundService() {

        final String CHANNEL_ID = BuildConfig.APPLICATION_ID.concat("_notification_id");
        final String CHANNEL_NAME = BuildConfig.APPLICATION_ID.concat("_notification_name");
        final int NOTIFICATION_ID = 100;

        NotificationCompat.Builder builder;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_NONE;
            assert notificationManager != null;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (mChannel == null) {
                mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
        }
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name));
        startForeground(NOTIFICATION_ID, builder.build());
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Servizio registrazione percorso non disponibile","Non è possibile avviare il servizio di registrazione in mancaza dei permessi necessari");
            return;
        }
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void sendMessage(PathDetail path) {
        Intent intent = new Intent("PathRecordingUpdate");
        // You can also include some extra data.
        intent.putExtra("Path", new Gson().toJson(path));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopForeground(true);
        stopSelf();
    }

    private String calculateDuration(Instant start, Instant end){
        long timeElapsed = Duration.between(start, end).toMillis();
        return String.valueOf(String.format("%dh:%dmin:%dsec",
                TimeUnit.MILLISECONDS.toHours(timeElapsed),
                TimeUnit.MILLISECONDS.toMinutes(timeElapsed),
                TimeUnit.MILLISECONDS.toSeconds(timeElapsed)
        ));
    }
}