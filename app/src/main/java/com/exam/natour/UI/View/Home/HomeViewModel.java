package com.exam.natour.UI.View.Home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.exam.natour.Model.PathsResponse.Path;
import com.exam.natour.Network.Repository.PathRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private PathRepository pathRepository;

    public HomeViewModel() {
        this.pathRepository = PathRepository.getInstance();
    }

    public LiveData<List<Path>> getPaths(Context context){
        return this.pathRepository.getPaths(context);
    }

    public void filterPathResult(Context context, String raggio, String distanza, String durata, boolean disability, List<String> difficultiesOptionSelected, LatLng currentPos) {
        pathRepository.filterPathResult(context,raggio,distanza,durata,disability,difficultiesOptionSelected,currentPos);
    }
}