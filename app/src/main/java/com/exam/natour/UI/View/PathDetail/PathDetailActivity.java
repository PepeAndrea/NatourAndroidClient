package com.exam.natour.UI.View.PathDetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

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

        setContentView(R.layout.activity_path_detail);

        //Bind delle views
        pathImage = findViewById(R.id.pathImage);
        pathTitle = findViewById(R.id.pathTitle);
        pathDescription = findViewById(R.id.pathDescription);
        pathDifficulty = findViewById(R.id.pathDifficulty);
        pathLength = findViewById(R.id.pathLength);
        pathDuration = findViewById(R.id.pathDuration);
        pathLocation = findViewById(R.id.pathLocation);
        interestPointList = findViewById(R.id.interestPointList);


        //Recupero informazioni da Intent e imposto i dat a disposizione
        Bundle extras = getIntent().getExtras();
        pathTitle.setText(extras.getString("pathTitle",""));

        //animazione di apertura
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                                                    .inflateTransition(R.transition.shared_element_transaction));

        pathImage.setTransitionName(imageTransitionName);

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
                pathDescription.setText(pathDetail.getDescription());
                pathDifficulty.setText(pathDetail.getDifficulty());
                pathLength.setText(String.valueOf(pathDetail.getLength()));
                pathDuration.setText(String.valueOf(pathDetail.getDuration()));
                pathLocation.setText(pathDetail.getLocation());
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
        interestPointList.setAdapter(this.interestPointAdapter);
        interestPointList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setMapCoordinate(List<Coordinate> coordinates, List<InterestPoint> interestPoints) {

        if(map != null){
            PolylineOptions path = new PolylineOptions();
            coordinates.forEach((coordinate -> {
                path.add(new LatLng(Double.valueOf(coordinate.getLatitude()),Double.valueOf(coordinate.getLongitude()))).clickable(false);
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
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}