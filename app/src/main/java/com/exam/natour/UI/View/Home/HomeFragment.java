package com.exam.natour.UI.View.Home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.exam.natour.Model.PathsResponse.Path;
import com.exam.natour.R;
import com.exam.natour.UI.Adapter.PathAdapter.PathAdapter;
import com.exam.natour.databinding.FragmentHomeBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationClient;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private RecyclerView pathList;
    private PathAdapter pathAdapter;
    private boolean isFilterOpen = false;
    private LatLng currentPos;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.setupUserInterface();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        this.pathList = root.findViewById(R.id.pathList);
        this.setupPathList();
        this.ObserveChange();

        this.setCurrentPositionForFilter();

        //Probabilmente inutile dal momento che il metodo è chiamato in ObserveChange()
        //this.homeViewModel.getPaths(root.getContext());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void ObserveChange(){
        homeViewModel.getPaths(getContext()).observe(getViewLifecycleOwner(), new Observer<List<Path>>() {
            @Override
            public void onChanged(List<Path> paths) {
                pathAdapter.setPaths(paths);
            }
        });
    }


    private void setupPathList(){
        this.pathAdapter = new PathAdapter();
        pathList.setAdapter(this.pathAdapter);
        pathList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setCurrentPositionForFilter() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentPos = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
    }


    //Gestione interfaccia utente

    private void setupUserInterface(){
        binding.filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFilterOpen) {
                    closeFilter();
                } else {
                    openFilter();
                }
            }
        });

        setupFilterButton();
    }

    private void openFilter(){
        this.isFilterOpen = true;
        //binding.filterTab.setVisibility(View.VISIBLE);
        binding.filterTab.animate().alpha(1.0f).setDuration(0);
        binding.filterTab.animate().translationX(0.0f).setDuration(300);
    }

    private void closeFilter(){
        this.isFilterOpen = false;
        binding.filterTab.animate().translationX(-6000f).setDuration(300);

        //binding.filterTab.animate().alpha(0.0f).setDuration(00);

        //binding.filterTab.setVisibility(View.GONE);
    }

    private void setupFilterButton(){
        binding.updateFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateFilterInput()){
                    String raggio = (binding.raggioFilterInput.getText().toString().length() > 0) ? binding.raggioFilterInput.getText().toString() : null;
                    String distanza = (binding.distanzaFilterInput.getText().toString().length() > 0) ? binding.distanzaFilterInput.getText().toString() : null;
                    String durata = (binding.durataFilterInput.getText().toString().length() > 0) ? binding.durataFilterInput.getText().toString() : null;
                    homeViewModel.filterPathResult(getContext(),raggio,distanza,durata,binding.disabiliFilterInput.isChecked(),difficultiesOptionSelected(),currentPos);
                    closeFilter();
                }
            }
        });


        binding.clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilterInput();
                homeViewModel.filterPathResult(getContext(),null,null,null,false, null,null);
                closeFilter();
            }
        });
    }

    private void resetFilterInput() {
        binding.durataFilterInput.setText("");
        binding.distanzaFilterInput.setText("");
        binding.raggioFilterInput.setText("");
        binding.eDifficultyCheckbox.setChecked(false);
        binding.eeDifficultyCheckbox.setChecked(false);
        binding.eeaDifficultyCheckbox.setChecked(false);
        binding.tDifficultyCheckbox.setChecked(false);
        binding.disabiliFilterInput.setChecked(false);
    }


    private boolean validateFilterInput(){
        boolean validated = true;

        try {
            Integer.parseInt(binding.raggioFilterInput.getText().toString());
        } catch (NumberFormatException e) {
            if (binding.raggioFilterInput.getText().toString().length() > 0){
                binding.raggioFilterInput.setError("Sono accettati solo numeri interi");
                validated = false;
            }
        }

        try {
            Integer.parseInt(binding.distanzaFilterInput.getText().toString());
        } catch (NumberFormatException e) {
            if (binding.distanzaFilterInput.getText().toString().length() > 0){
                binding.distanzaFilterInput.setError("Sono accettati solo numeri interi");
                validated = false;
            }
        }

        try {
            Integer.parseInt(binding.durataFilterInput.getText().toString());
        } catch (NumberFormatException e) {
            if (binding.durataFilterInput.getText().toString().length() > 0){
                binding.durataFilterInput.setError("Sono accettati solo numeri interi");
                validated = false;
            }
        }

        return validated;
    }

    private List<String> difficultiesOptionSelected(){
        List<String> response = new ArrayList<>();

        if (binding.eeaDifficultyCheckbox.isChecked())
            response.add("EEA");
        if (binding.eeDifficultyCheckbox.isChecked())
            response.add("EE");
        if (binding.eDifficultyCheckbox.isChecked())
            response.add("E");
        if (binding.tDifficultyCheckbox.isChecked())
            response.add("T");

        return response;
    }
}