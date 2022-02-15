package com.exam.natour.UI.View.Setting;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.exam.natour.Model.AuthUser;
import com.exam.natour.Network.Repository.AuthRepository;

public class SettingViewModel extends ViewModel {

    private AuthRepository authRepository;

    public SettingViewModel() {
        this.authRepository = AuthRepository.getInstance();

    }

    public AuthUser authUser() {
        return this.authRepository.getAuthUser();
    }

    public void logout(Context context) {
        this.authRepository.logout(context);
    }
}