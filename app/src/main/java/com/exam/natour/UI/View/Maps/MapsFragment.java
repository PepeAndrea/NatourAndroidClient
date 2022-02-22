package com.exam.natour.UI.View.Maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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

import com.exam.natour.Model.PathDetailResponse.InterestPoint;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment {

    private boolean isFabOpen = false;

    private FusedLocationProviderClient fusedLocationClient;
    private MapsViewModel mapsViewModel;
    private FragmentMapsBinding binding;
    private GoogleMap map;
    private Polyline polyline;
    private LatLng currentPos;
    private String currentCity;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            //Verifia permessi
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setUserLocation();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle("Permessi mancanti")
                        .setMessage("Per utilizzare la funzionalità di tracciamento hai bisogno di abilitare i permessi per la localizzazione.\n\nPer motivi di privacy non sempre vi è la possibilità di attivare il tracciamento anche quando l'app non è in utilizzo, si prega di attivare tale funzionalità dai permessi dell'app nelle impostazioni per garantire un corretto funzionamento")
                        .setNegativeButton("Annulla", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setPositiveButton("Concedi", (dialogInterface, i) -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 101))
                        .show();
            }


        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
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
        mapsViewModel.checkUserRecording(getActivity().getApplicationContext());
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
                            //Ricerca città
                            String cityName = null;
                            Geocoder gcd = new Geocoder(getContext(),
                                    Locale.getDefault());
                            List<Address> addresses;
                            try {
                                addresses = gcd.getFromLocation(location.getLatitude(), location
                                        .getLongitude(), 1);
                                if (addresses.size() > 0)
                                    System.out.println(addresses.get(0).getLocality());
                                currentCity = addresses.get(0).getLocality();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

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
            if (polyline != null)
                polyline.remove();
            PolylineOptions path = new PolylineOptions();
            pathDetail.getCoordinates().forEach((coordinate -> {
                path.add(new LatLng(Double.valueOf(coordinate.getLatitude()), Double.valueOf(coordinate.getLongitude()))).clickable(false);
            }));
            polyline = map.addPolyline(path);
        });
    }



    //User Interface

    private void setupUserInterface() {
        this.setFabButtons();
        this.setRecordingButtons();
        this.setInterestPointButtons();
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
                setRecordingInterface();
                mapsViewModel.startPathRecording(getContext());
            }
        });

        binding.fabInserisciManualmente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                mapsViewModel.stopPathRecording(getContext());
            }
        });

    }

    private void openFab() {
        isFabOpen = true;
        binding.fabRegistraPercorso.animate().translationY(-380).alpha(1.0f);
        binding.fabUploadFile.animate().translationY(-280).alpha(1.0f);
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
                mapsViewModel.saveRecordedPath(getContext(),currentCity);
                map.clear();
                unsetRecordingInterface();
            }
        });

        binding.recordingActionButtonsPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Funzione di pausa non ancora disponibile", Toast.LENGTH_SHORT).show();
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
        binding.interestPointForm.animate().translationY(1000);
    }

    private void setInterestPointButtons() {
        binding.interestPointSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInterestPointInput(binding.interestPointName.getText().toString())){
                    mapsViewModel.addInterestPoint(
                            new InterestPoint(binding.interestPointName.getText().toString(),
                                    binding.interestPointDescription.getText().toString(),
                                    binding.interestPointCategory.getSelectedItem().toString(),
                                    String.valueOf(currentPos.latitude),
                                    String.valueOf(currentPos.longitude)));

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
                unsetInterestPointInterface();
            }
        });

    }

    private boolean validateInterestPointInput(String name) {
        boolean validated = true;

        if(name.length() == 0){
            binding.interestPointName.setError("Il campo Titolo non può essere vuoto");
            validated = false;
        }
        return validated;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapsViewModel.unsetReceiver(getContext());
        binding = null;
    }
}
