package com.exam.natour.UI.View.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.exam.natour.Model.PathsResponse.Path;
import com.exam.natour.R;
import com.exam.natour.UI.Adapter.PathAdapter.PathAdapter;
import com.exam.natour.databinding.FragmentHomeBinding;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private RecyclerView pathList;
    private PathAdapter pathAdapter;
    private boolean isFilterOpen = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.setupUserInterface();

        this.pathList = root.findViewById(R.id.pathList);
        this.setupPathList();
        this.ObserveChange();
        this.homeViewModel.getPaths(root.getContext());

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
    }

    private void openFilter(){
        this.isFilterOpen = true;
        binding.filterTab.setVisibility(View.VISIBLE);
        binding.filterTab.animate().alpha(1.0f).setDuration(100);
    }

    private void closeFilter(){
        this.isFilterOpen = false;
        binding.filterTab.animate().alpha(0.0f).setDuration(100);
        binding.filterTab.setVisibility(View.GONE);
    }
}