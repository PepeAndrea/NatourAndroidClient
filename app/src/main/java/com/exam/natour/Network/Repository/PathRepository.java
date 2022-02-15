package com.exam.natour.Network.Repository;

import androidx.lifecycle.LiveData;

import com.exam.natour.Model.PathsResponse.Path;
import com.exam.natour.Network.APIClient.PathApiClient;

import java.util.List;

public class PathRepository {
    private PathApiClient pathApiClient;
    private static PathRepository pathRepository;


    public PathRepository() {
        this.pathApiClient = PathApiClient.getInstance();
    }

    public static PathRepository getInstance(){
        if(pathRepository == null){
            pathRepository = new PathRepository();
        }
        return pathRepository;
    }

    public LiveData<List<Path>> getPaths(){
        return pathApiClient.getPaths();
    }
}
