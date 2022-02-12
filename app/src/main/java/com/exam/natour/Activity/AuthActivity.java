package com.exam.natour.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.exam.natour.R;
import com.exam.natour.UI.View.AuthMainPage.AuthMainPage;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            // Let's first dynamically add a fragment into a frame container
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.AuthContainer, new AuthMainPage()).
                    commit();
        }


    }
}