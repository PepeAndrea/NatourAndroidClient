package com.exam.natour.UI.View.InsertPath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.exam.natour.Model.LiveRecordingData;
import com.exam.natour.Model.PathDetailResponse.Coordinate;
import com.exam.natour.Model.PathDetailResponse.InterestPoint;
import com.exam.natour.Model.PathDetailResponse.PathDetail;
import com.exam.natour.R;
import com.exam.natour.UI.View.PathDetail.PathDetailViewModel;
import com.exam.natour.databinding.ActivityInsertPathBinding;
import com.exam.natour.databinding.ActivityPathDetailBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.List;

public class InsertPathActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityInsertPathBinding binding;
    private InsertPathViewModel insertPathViewModel;
    private GoogleMap map;
    private PathDetail newPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //setup data binding
        insertPathViewModel = new ViewModelProvider(this).get(InsertPathViewModel.class);
        binding = ActivityInsertPathBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        //Faccio il parsing del Json
        newPath = new Gson().fromJson(getIntent().getExtras().getString("Path"),PathDetail.class);

        if (getIntent().getExtras().containsKey("updateCoordinateAfter")){
            newPath.setCoordinates(LiveRecordingData.getInstance().getCoordinates());
            //Log.i("Coordinate lette", "uploadGpxPath: "+trackPoint.getLatitude()+"  "+trackPoint.getLongitude());


            newPath.calculateLength();
            LiveRecordingData.getInstance().destroy();
        }

        if (newPath.getDuration() == null){
            binding.InsertPathDuration.setVisibility(View.VISIBLE);
        }

        binding.InsertPathSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInsertPathInput(binding.InsertPathName.getText().toString(),binding.InsertPathDescription.getText().toString())){
                    newPath.setTitle(binding.InsertPathName.getText().toString());
                    newPath.setDescription(binding.InsertPathDescription.getText().toString());
                    newPath.setDifficulty(binding.InsertPathDifficulty.getSelectedItem().toString());
                    newPath.setDisability(Boolean.compare(binding.InsertPathDisability.isChecked(),false));
                    if (newPath.getDuration() == null && binding.InsertPathDuration.getVisibility() == View.VISIBLE){
                        newPath.setDuration(Long.valueOf(Integer.parseInt(binding.InsertPathDuration.getText().toString())*60000));
                    }
                    insertPathViewModel.savePath(newPath,view.getContext());
                }
            }
        });

        binding.InsertPathCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Attenzione")
                        .setMessage("Sicuro di voler tornare indietro?\nTutti i dati andranno persi.")
                        .setPositiveButton("Torna indietro", (dialogInterface, i) -> finish())
                        .setNegativeButton("Annulla", (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
            }
        });

        //Creo fragment di supporto per GMaps
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.InsertPath_mapContainer, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        this.setMapCoordinate(newPath.getCoordinates(),newPath.getInterestPoints());
    }

    private void setMapCoordinate(List<Coordinate> coordinates, List<InterestPoint> interestPoints) {

        if(map != null){
            PolylineOptions path = new PolylineOptions();
            if (coordinates != null){
                coordinates.forEach((coordinate -> {
                    path.add(new LatLng(Double.valueOf(coordinate.getLatitude()),Double.valueOf(coordinate.getLongitude()))).clickable(false);
                }));
                map.addPolyline(path);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(coordinates.get(0).getLatitude()),Double.valueOf(coordinates.get(0).getLongitude())),13f));
            }
            if (interestPoints != null){
                interestPoints.forEach(interestPoint -> {
                    Log.i("Analisi coordinate punto interesse",interestPoint.getLatitude()+" "+interestPoint.getLongitude());
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.valueOf(interestPoint.getLatitude()),Double.valueOf(interestPoint.getLongitude())))
                            .title(interestPoint.getTitle())
                            .draggable(false));
                });
            }
        }

    }

    private boolean validateInsertPathInput(String title, String description) {
        boolean validated = true;

        if(title.length() == 0){
            binding.InsertPathName.setError("Il campo Nome non può essere vuoto");
            validated = false;
        }
        if(description.length() == 0){
            binding.InsertPathDescription.setError("Il campo Descrizione non può essere vuoto");
            validated = false;
        }
        if (binding.InsertPathDuration.getVisibility() == View.VISIBLE){
            try {
                Integer.parseInt(binding.InsertPathDuration.getText().toString());
            } catch (NumberFormatException e) {
                    binding.InsertPathDuration.setError("Sono accettati solo numeri interi");
                    validated = false;
                }
        }

        return validated;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}