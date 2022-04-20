package com.exam.natour.UI.View.SendAdvEmail;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.exam.natour.Network.Repository.AdminRepository;
import com.exam.natour.Network.Repository.AuthRepository;

public class AdvEmailViewModel extends ViewModel {

    private AdminRepository adminRepository;


    public AdvEmailViewModel() {
        this.adminRepository = AdminRepository.getInstance();
    }

    public void sendEmail(Context context, String title, String content) {
        this.adminRepository.sendEmail(context,title,content);
    }
}
