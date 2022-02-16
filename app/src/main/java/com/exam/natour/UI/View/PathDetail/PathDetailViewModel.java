package com.exam.natour.UI.View.PathDetail;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.exam.natour.Model.PathDetailResponse.PathDetail;
import com.exam.natour.Model.PathsResponse.Path;
import com.exam.natour.Network.Repository.PathRepository;

import java.util.List;

public class PathDetailViewModel extends ViewModel {

    private PathRepository pathRepository;

    public PathDetailViewModel() {
        this.pathRepository = PathRepository.getInstance();
    }

    public LiveData<List<Path>> getPaths(Context context){
        return this.pathRepository.getPaths(context);
    }

    public LiveData<PathDetail> getLoadedPath(Context context,String id){
        return pathRepository.getPathDetail(context,id);
    }


}
