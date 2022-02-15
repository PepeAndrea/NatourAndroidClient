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
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        this.pathRepository = PathRepository.getInstance();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Path>> getPaths(Context context){
        return this.pathRepository.getPaths(context);
    }
}