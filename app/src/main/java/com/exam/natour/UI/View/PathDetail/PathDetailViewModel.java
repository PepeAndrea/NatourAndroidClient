package com.exam.natour.UI.View.PathDetail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.exam.natour.Model.AuthUser;
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


    public void exportDetail(Context context,String fileType, String pathId) {
        Log.i("Download dettaglio percorso", "Esporto dettagli in formato "+fileType);
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW, Uri.parse("https://natour.pepeandrea.it/api/export/"+fileType+"/"+pathId));
        Bundle bundle = new Bundle();
        bundle.putString("Authorization", "Bearer "+ AuthUser.getInstance().getToken());
        browserIntent.putExtra(Browser.EXTRA_HEADERS, bundle);
        context.startActivity(browserIntent);

    }
}
