package com.exam.natour.Network.Repository;

import android.content.Context;

import com.exam.natour.Network.APIClient.AdminApiClient;
import com.exam.natour.Network.APIClient.AuthApiClient;

public class AdminRepository {

    private static AdminRepository adminRepository;
    private AdminApiClient adminApiClient;


    public AdminRepository() {
        this.adminApiClient = AdminApiClient.getInstance();
    }

    public static AdminRepository getInstance(){
        if(adminRepository == null){
            adminRepository = new AdminRepository();
        }
        return adminRepository;
    }


    public void sendEmail(Context context, String title, String content) {
        this.adminApiClient.sendEmail(context,title,content);
    }
}
