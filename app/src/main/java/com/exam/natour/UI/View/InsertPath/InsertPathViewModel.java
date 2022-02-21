package com.exam.natour.UI.View.InsertPath;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.exam.natour.Model.PathDetailResponse.PathDetail;
import com.exam.natour.Network.Repository.PathRepository;

public class InsertPathViewModel extends ViewModel {

    private PathRepository pathRepository;

    public InsertPathViewModel() {
        this.pathRepository = PathRepository.getInstance();
    }

    public void savePath(PathDetail newPath, Context context) {
        this.pathRepository.savePath(newPath,context);
    }
}
