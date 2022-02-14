package com.exam.natour.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.exam.natour.R;
import com.exam.natour.UI.View.AuthMainPage.AuthMainPage;
import com.exam.natour.UI.View.LoginPage.LoginPageViewModel;

public class AuthActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //Verifica se è salvato l'accesso nel dispositivo
        sharedPreferences = getSharedPreferences("AUTH",MODE_PRIVATE);
        Log.i("Token",sharedPreferences.getString("Token",""));

        if(sharedPreferences.contains("Token")){
            Log.i("Token",sharedPreferences.getString("Token",""));
            new LoginPageViewModel().checkSavedToken(this,sharedPreferences.getString("Token",""));
        }


        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            // Let's first dynamically add a fragment into a frame container
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.AuthContainer, new AuthMainPage()).
                    commit();
        }


    }
}