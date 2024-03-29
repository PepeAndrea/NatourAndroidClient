package com.exam.natour.UI.View.Maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.exam.natour.Model.PathDetailResponse.Coordinate;
import com.exam.natour.Model.PathDetailResponse.InterestPoint;
import com.exam.natour.Model.PathDetailResponse.PathDetail;
import com.exam.natour.R;
import com.exam.natour.databinding.FragmentMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment {

    private final int COORDINATE = 1;
    private final int INTERESTPOINT = 0;

    private Integer insertManualMode = COORDINATE;

    private boolean isFabOpen = false;

    private FusedLocationProviderClient fusedLocationClient;
    private MapsViewModel mapsViewModel;
    private FragmentMapsBinding binding;
    private GoogleMap map;
    private PolylineOptions polyline;
    private LatLng currentPos;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            //Verifia permessi
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setUserLocation();
            } else {
                Log.e("Permessi mancanti", "Mancano permessi di localizzazione e mostro popup di richiesta");
                new AlertDialog.Builder(getContext())
                        .setTitle("Permessi mancanti")
                        .setMessage("Per utilizzare la funzionalità di tracciamento è necessario abilitare i permessi per la localizzazione.\nPer fare in modo che l'app funzioni anche in background, seleziona \"Consenti sempre\"")
                        .setPositiveButton("Concedi", (dialogInterface, i) -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 101))
                        .setNegativeButton("Annulla", (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
            }

            switch (mapsViewModel.checkUserRecording(getActivity().getApplicationContext())){
                case "gpsRecording":
                    setRecordingInterface();
                    break;
                case "manualRecording":
                    setMapListener();
                    setManualRecordingInterface();
                    break;
                default:
                    break;
            }


        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            Log.i("Permessi concessi", "Permessi concessi: procedo a localizzare l'utente sulla mappa");
            setUserLocation();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mapsViewModel = new ViewModelProvider(this).get(MapsViewModel.class);
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.setupUserInterface();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        this.setupMapRecordingUpdater();
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.map, mapFragment)
                .commit();
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        mapsViewModel.setReceiver(getContext());
    }

    private void setUserLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            //Animazione mappa
                            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),12f),3000,null);
                            //Spostamento mappa senza animazione
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12f));
                        }
                    }
                });
    }

    private void setupMapRecordingUpdater() {
        mapsViewModel.getCreatedPath().observe(getViewLifecycleOwner(), pathDetail -> {
            Log.i("MapRecordingUpdater", "Ho ricevuto un aggiornamento dal viewmodel dell'oggetto PathDetail");
            if (map != null){
                map.clear();
                polyline = new PolylineOptions();
                if (pathDetail.getCoordinates() != null){
                    pathDetail.getCoordinates().forEach((coordinate -> {
                        polyline.add(new LatLng(Double.valueOf(coordinate.getLatitude()), Double.valueOf(coordinate.getLongitude()))).clickable(false);
                    }));
                    map.addPolyline(polyline);
                }

                if (pathDetail.getInterestPoints() != null){
                    pathDetail.getInterestPoints().forEach(interestPoint -> {
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.valueOf(interestPoint.getLatitude()),Double.valueOf(interestPoint.getLongitude())))
                                .title(interestPoint.getTitle())
                                .draggable(false));
                    });
                }
            }
        });
    }




    //User Interface

    private void setupUserInterface() {
        this.setFabButtons();
        this.setRecordingButtons();
        this.setInterestPointButtons();
        this.setManualRecordingButtons();
    }

    private void setFabButtons() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFabOpen) {
                    closeFab();
                } else {
                    openFab();
                }

            }
        });

        binding.fabRegistraPercorso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                setRecordingInterface();
                mapsViewModel.startPathRecording(getContext());
            }
        });

        binding.fabInserisciManualmente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                polyline = new PolylineOptions();
                mapsViewModel.startManualPathRecording();
                setMapListener();
                setManualRecordingInterface();
            }
        });

        binding.fabUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    uploadGpxFile();
                } else {
                    Log.e("Permessi mancanti", "Mancano permessi di accesso alla memoria e mostro popup di richiesta");
                    new AlertDialog.Builder(getContext())
                            .setTitle("Permessi mancanti")
                            .setMessage("Per utilizzare la funzionalità di upload del percorso è necessario abilitare i permessi per l'accesso alla memoria.\nSi prega di selezionare l'accesso a tutti i file")
                            .setPositiveButton("Concedi", (dialogInterface, i) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            })
                            .setNegativeButton("Annulla", (dialogInterface, i) -> dialogInterface.dismiss())
                            .show();
                }
            }
        });

    }

    private void openFab() {
        isFabOpen = true;
        binding.fabRegistraPercorso.animate().translationY(-480).alpha(1.0f);
        binding.fabUploadFile.animate().translationY(-330).alpha(1.0f);
        binding.fabInserisciManualmente.animate().translationY(-180).alpha(1.0f);
    }

    private void closeFab() {
        isFabOpen = false;
        binding.fabRegistraPercorso.animate().translationY(0).alpha(0.0f);
        binding.fabUploadFile.animate().translationY(0).alpha(0.0f);
        binding.fabInserisciManualmente.animate().translationY(0).alpha(0.0f);
    }

    private void setRecordingButtons() {
        binding.recordingActionButtonsSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapsViewModel.saveRecordedPath(getContext());
                map.clear();
                unsetRecordingInterface();
            }
        });

        binding.recordingActionButtonsInterestpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    currentPos = new LatLng(location.getLatitude(),location.getLongitude());
                                }
                            }
                        });
                binding.interestPointDescription.setText("");
                binding.interestPointName.setText("");
                setInterestPointInterface();
            }
        });

    }

    private void setRecordingInterface(){
        closeFab();
        binding.fab.animate().alpha(0.0f);
        binding.fab.setVisibility(View.GONE);
        binding.fabRegistraPercorso.setVisibility(View.GONE);
        binding.fabInserisciManualmente.setVisibility(View.GONE);
        binding.fabUploadFile.setVisibility(View.GONE);
        binding.recordingActionButtons.setVisibility(View.VISIBLE);
        binding.recordingActionButtons.animate().alpha(1.0f);
    }

    private void unsetRecordingInterface(){
        binding.recordingActionButtons.animate().alpha(0.0f);
        binding.recordingActionButtons.setVisibility(View.GONE);
        binding.fab.animate().alpha(1.0f);
        binding.fab.setVisibility(View.VISIBLE);
        binding.fabRegistraPercorso.setVisibility(View.VISIBLE);
        binding.fabInserisciManualmente.setVisibility(View.VISIBLE);
        binding.fabUploadFile.setVisibility(View.VISIBLE);
    }

    private void setInterestPointInterface(){
        binding.interestPointForm.animate().translationY(0);
    }

    private void unsetInterestPointInterface(){
        binding.interestPointForm.animate().translationY(2000);
    }

    private void setInterestPointButtons() {
        binding.interestPointSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInterestPointInput(binding.interestPointName.getText().toString(),binding.interestPointDescription.getText().toString())){
                    mapsViewModel.addInterestPoint(
                            new InterestPoint(binding.interestPointName.getText().toString(),
                                    binding.interestPointDescription.getText().toString(),
                                    binding.interestPointCategory.getSelectedItem().toString(),
                                    String.valueOf(currentPos.latitude),
                                    String.valueOf(currentPos.longitude)));

                    //Lascio la riga sotto solo per non fare aspettare l'utente l'aggiornamento della mappa per vedere il marker creato
                    map.addMarker(new MarkerOptions().position(currentPos).title(binding.interestPointName.getText().toString()));
                    unsetInterestPointInterface();
                }


            }
        });

        binding.interestPointCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.interestPointName.setText("");
                binding.interestPointDescription.setText("");
                binding.interestPointName.setError(null);
                binding.interestPointDescription.setError(null);
                unsetInterestPointInterface();
            }
        });

    }

    private void setManualRecordingInterface() {
        closeFab();
        binding.fab.animate().alpha(0.0f);
        binding.fab.setVisibility(View.GONE);
        binding.fabRegistraPercorso.setVisibility(View.GONE);
        binding.fabInserisciManualmente.setVisibility(View.GONE);
        binding.fabUploadFile.setVisibility(View.GONE);
        binding.manualRecordingActionButtons.setVisibility(View.VISIBLE);
        binding.manualRecordingActionButtons.animate().alpha(1.0f);
    }

    private void unsetManualRecordingInterface() {
        binding.manualRecordingActionButtons.animate().alpha(0.0f);
        binding.manualRecordingActionButtons.setVisibility(View.GONE);
        binding.fab.animate().alpha(1.0f);
        binding.fab.setVisibility(View.VISIBLE);
        binding.fabRegistraPercorso.setVisibility(View.VISIBLE);
        binding.fabInserisciManualmente.setVisibility(View.VISIBLE);
        binding.fabUploadFile.setVisibility(View.VISIBLE);
    }

    private void setMapListener(){
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                currentPos = latLng;

                if(insertManualMode == INTERESTPOINT){
                    binding.interestPointDescription.setText("");
                    binding.interestPointName.setText("");
                    setInterestPointInterface();
                }
                if (insertManualMode == COORDINATE){
                    polyline.add(latLng);
                    map.addPolyline(polyline);
                    mapsViewModel.addCoordinate(new Coordinate(String.valueOf(latLng.latitude),String.valueOf(latLng.longitude)));
                }
            }
        });
    }

    private void unsetMapListener(){
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                return;
            }
        });
    }

    private void setManualRecordingButtons() {
        binding.manualRecordingActionButtonsSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                polyline = null;
                unsetMapListener();
                mapsViewModel.saveManualPathRecording(getContext());
                map.clear();
                unsetManualRecordingInterface();
            }
        });

        binding.manualRecordingActionButtonsCoordinate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertManualMode = COORDINATE;
            }
        });

        binding.manualRecordingActionButtonsInterestpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertManualMode = INTERESTPOINT;
                binding.interestPointDescription.setText("");
                binding.interestPointName.setText("");
            }
        });

    }

    private boolean validateInterestPointInput(String name,String description) {
        boolean validated = true;

        if(name.length() == 0){
            binding.interestPointName.setError("Il campo Nome non può essere vuoto");
            validated = false;
        }
        if(description.length() == 0){
            binding.interestPointDescription.setError("Il campo Descrizione non può essere vuoto");
            validated = false;
        }
        return validated;
    }

    private void uploadGpxFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        startActivityForResult(
                Intent.createChooser(intent, "Seleziona file da caricare"),
                1232);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapsViewModel.unsetReceiver(getContext());
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == 1232 && resultCode == Activity.RESULT_OK) {
            Log.i("Permessi concessi", "Permessi concessi: l'utente può ora selezionare un file");
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    String path = Environment.getExternalStorageDirectory()+"/"+new File(uri.getPath()).getPath().split(":")[1];
                    InputStream input = new FileInputStream(path);
                    Log.i("GPXFILE", "File caricato: "+path);
                    mapsViewModel.uploadGpxPath(getContext(),path);
                } catch (FileNotFoundException e) {
                    Log.e("Errore lettura file", "Errore nel caricare il file: "+e.getMessage());
                    new AlertDialog.Builder(getContext())
                            .setTitle("Permessi mancanti")
                            .setMessage("Per utilizzare la funzionalità di upload del tracciato è necessario abilitare i permessi per l'accesso alla memoria.\nSi prega di selezionare l'accesso a tutti i file")
                            .setPositiveButton("Concedi", (dialogInterface, i) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri settinguri = Uri.fromParts("package", getContext().getPackageName(), null);
                                intent.setData(settinguri);
                                startActivity(intent);
                            })
                            .setNegativeButton("Annulla", (dialogInterface, i) -> dialogInterface.dismiss())
                            .show();
                }
            }
        }
    }

}
