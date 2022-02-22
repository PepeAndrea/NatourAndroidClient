package com.exam.natour.UI.View.PathDetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.transition.TransitionInflater;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.exam.natour.Model.PathDetailResponse.Coordinate;
import com.exam.natour.Model.PathDetailResponse.InterestPoint;
import com.exam.natour.Model.PathDetailResponse.PathDetail;
import com.exam.natour.R;
import com.exam.natour.UI.Adapter.InterestPointAdapter.InterestPointAdapter;
import com.exam.natour.databinding.ActivityPathDetailBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PathDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityPathDetailBinding binding;
    private PathDetailViewModel pathDetailViewModel;
    private GoogleMap map;


    String imageTransitionName = "pathImageTransition";

    TextView pathTitle,pathDescription,pathDifficulty,pathLength,pathDuration,pathUser,pathLocation,pathId;
    ImageView pathImage;
    RecyclerView interestPointList;

    InterestPointAdapter interestPointAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //setup data binding
        pathDetailViewModel = new ViewModelProvider(this).get(PathDetailViewModel.class);
        binding = ActivityPathDetailBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        //Recupero informazioni da Intent e imposto i dat a disposizione
        Bundle extras = getIntent().getExtras();
        binding.pathTitle.setText(extras.getString("pathTitle",""));

        //animazione di apertura
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                                                    .inflateTransition(R.transition.shared_element_transaction));

        binding.pathImage.setTransitionName(imageTransitionName);

        this.setupInterestPointList();
        this.ObserveChange(extras.getString("pathId"));

        //Creo fragment di supporto per GMaps
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mapContainer, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

    }


    private void ObserveChange(String id){
        this.pathDetailViewModel.getLoadedPath(this,id).observe(this, new Observer<PathDetail>() {
            @Override
            public void onChanged(PathDetail pathDetail) {
                Log.i("Percorso caricato",pathDetail.getTitle());
                //Imposto i dati
                binding.pathDescription.setText(pathDetail.getDescription());
                binding.pathDifficulty.setText(pathDetail.getDifficulty());
                binding.pathLength.setText(String.valueOf(pathDetail.getLength()));
                binding.pathDuration.setText(formatDuration(pathDetail.getDuration()));
                binding.pathLocation.setText(pathDetail.getLocation());

                binding.pathDifficulty.setTextColor(Color.parseColor(selectDifficultyColor(pathDetail.getDifficulty())));


                //Li rendo visibili
                /*
                pathDescription.setVisibility(View.VISIBLE);
                pathDifficulty.setVisibility(View.VISIBLE);
                pathLength.setVisibility(View.VISIBLE);
                pathDuration.setVisibility(View.VISIBLE);
                pathLocation.setVisibility(View.VISIBLE);

                 */

                setMapCoordinate(pathDetail.getCoordinates(),pathDetail.getInterestPoints());
                interestPointAdapter.setInterestPoints(pathDetail.getInterestPoints());

            }
        });
    }

    private void setupInterestPointList(){
        this.interestPointAdapter = new InterestPointAdapter();
        binding.interestPointList.setAdapter(this.interestPointAdapter);
        binding.interestPointList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setMapCoordinate(List<Coordinate> coordinates, List<InterestPoint> interestPoints) {

        if(map != null){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            PolylineOptions path = new PolylineOptions();
            coordinates.forEach((coordinate -> {
                LatLng latLng = new LatLng(Double.valueOf(coordinate.getLatitude()),Double.valueOf(coordinate.getLongitude()));
                path.add(latLng).clickable(false);
                builder.include(latLng);
            }));
            map.addPolyline(path);
            interestPoints.forEach(interestPoint -> {
                Log.i("Analisi coordinate punto interesse",interestPoint.getLatitude()+" "+interestPoint.getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.valueOf(interestPoint.getLatitude()),Double.valueOf(interestPoint.getLongitude())))
                        .title(interestPoint.getTitle())
                        .draggable(false));
            });
            map.getUiSettings().setZoomGesturesEnabled(true);
            map.getUiSettings().setScrollGesturesEnabled(false);

            map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),20));
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }


    private String formatDuration(Long duration){

        long HH = duration / 3600;
        long MM = (duration % 3600) / 60;
        long SS = duration % 60;
        return String.valueOf(String.format("%dh:%dmin:%dsec",
                TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) % 60,
                TimeUnit.MILLISECONDS.toSeconds(duration) % 60
        ));
    }

    private String selectDifficultyColor(String diffuculty){
        switch (diffuculty){
            case "T":
                return "#669944";
            case "EEA":
                return "#DD4444";
            case "E":
                return "#EEBB00";
            case "EE":
                return "#E68433";
            default:
                return "#ffffff";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}