package com.exam.natour.Network.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.exam.natour.Model.PathDetailResponse.PathDetail;
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

    public LiveData<List<Path>> getPaths(Context context){
        return pathApiClient.getPaths(context);
    }

    public LiveData<PathDetail> getPathDetail(Context context,String id){
        return pathApiClient.getPathDetail(context,id);
    }

    public void savePath(PathDetail newPath, Context context) {
        pathApiClient.savePath(newPath,context);
    }
}
