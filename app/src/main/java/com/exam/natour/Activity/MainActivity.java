package com.exam.natour.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.exam.natour.Model.AuthUser;
import com.exam.natour.R;
import com.exam.natour.UI.View.Auth.AuthViewModel;
import com.exam.natour.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Verifica se è presente il token nel dispositivo e se presente inserirlo nel Model AuthUser
        sharedPreferences = getSharedPreferences("AUTH",MODE_PRIVATE);
        if(sharedPreferences.contains("Token")){
            Log.i("Token salvato",sharedPreferences.getString("Token",""));
            AuthUser.getInstance().setToken(sharedPreferences.getString("Token",""));
            new AuthViewModel().checkSavedToken(this,sharedPreferences.getString("Token",""));
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_path, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}