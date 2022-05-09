package com.exam.natour.UI.View.PathReport;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.exam.natour.Network.Repository.PathRepository;

public class PathReportViewModel extends ViewModel {

    private PathRepository pathRepository;

    public PathReportViewModel() {
        this.pathRepository = PathRepository.getInstance();
    }

    public void reportPath(Context context,String id){
        this.pathRepository.reportPath(context,id);
    }
}
