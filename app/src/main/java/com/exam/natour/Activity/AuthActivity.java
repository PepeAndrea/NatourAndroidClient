package com.exam.natour.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.exam.natour.R;
import com.exam.natour.UI.View.Auth.AuthMainPage;
import com.exam.natour.UI.View.Auth.AuthViewModel;

public class AuthActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //Verifica se è salvato l'accesso nel dispositivo

        sharedPreferences = getSharedPreferences("AUTH",MODE_PRIVATE);
        if(sharedPreferences.contains("Token")){
            Log.i("Token salvato",sharedPreferences.getString("Token",""));
            new AuthViewModel().checkSavedToken(this,sharedPreferences.getString("Token",""));
        }


        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            // Let's first dynamically add a fragment into a frame container
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.AuthContainer, new AuthMainPage()).
                    commit();
        }


    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


}