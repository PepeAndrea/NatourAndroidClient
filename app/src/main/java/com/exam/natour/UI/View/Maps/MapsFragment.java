package com.exam.natour.UI.View.Maps;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class MapsFragment extends Fragment {

    private boolean isFabOpen = false;

    private FusedLocationProviderClient fusedLocationClient;
    private MapsViewModel mapsViewModel;
    private FragmentMapsBinding binding;
    private GoogleMap map;

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
                        .setMessage("Per utilizzare la funzionalità di tracciamento hai bisogno di abilitare i permessi per la localizzazione")
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
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFabOpen){
                    closeFab();
                }else{
                    openFab();
                }

            }
        });
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
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
        Log.i("Coordinate viewmode",String.valueOf(mapsViewModel.isUserRecording()));

        //TODO Creare service così da far lavorare il dispositivo anche in background, quindio trasportare il codice scritto qui

        /*
        if (!mapsViewModel.isUserRecording()){
            mapsViewModel.startLocationUpdates(getContext());
        }
        Log.i("Coordinate viewmode",String.valueOf(mapsViewModel.isUserRecording()));
        */
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
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),12f),3000,null);
                            //Ricerca città
                            String cityName=null;
                            Geocoder gcd = new Geocoder(getContext(),
                                    Locale.getDefault());
                            List<Address> addresses;
                            try {
                                addresses = gcd.getFromLocation(location.getLatitude(), location
                                        .getLongitude(), 1);
                                if (addresses.size() > 0)
                                    System.out.println(addresses.get(0).getLocality());
                                cityName=addresses.get(0).getLocality();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getContext(),cityName,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void openFab() {
        isFabOpen = true;
        binding.fabRegistraPercorso.animate().translationY(-700).alpha(1.0f);
        binding.fabUploadFile.animate().translationY(-500).alpha(1.0f);
        binding.fabInserisciManualmente.animate().translationY(-300).alpha(1.0f);
    }

    private void closeFab() {
        isFabOpen = false;
        binding.fabRegistraPercorso.animate().translationY(0).alpha(0.0f);
        binding.fabUploadFile.animate().translationY(0).alpha(0.0f);
        binding.fabInserisciManualmente.animate().translationY(0).alpha(0.0f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
