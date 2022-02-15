package com.exam.natour.UI.View.Home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.exam.natour.Model.PathsResponse.Path;
import com.exam.natour.Network.Repository.PathRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private PathRepository pathRepository;
    private MutableLiveData<String> mText;

    public HomeViewModel() {
        this.pathRepository = PathRepository.getInstance();
    }

    public LiveData<List<Path>> getPaths(Context context){
        return this.pathRepository.getPaths(context);
    }
}