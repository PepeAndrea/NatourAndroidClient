package com.exam.natour.UI.View.PathReport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.exam.natour.databinding.ActivityPathReportBinding;

public class PathReportActivity extends AppCompatActivity {

    private PathReportViewModel pathReportViewModel;
    private ActivityPathReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        pathReportViewModel = new ViewModelProvider(this).get(PathReportViewModel.class);
        binding = ActivityPathReportBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        //Recupero informazioni da Intent e imposto i dat a disposizione
        Bundle extras = getIntent().getExtras();


        binding.sendReportBtn.setOnClickListener(view -> {
            Log.i("Report Inviato", "Inizio invio report");
            binding.sendReportBtn.setEnabled(false);
            binding.goBackBtn.setEnabled(false);
            pathReportViewModel.reportPath(view.getContext(),extras.getString("pathId",""));
        });



        binding.goBackBtn.setOnClickListener(view -> onBackPressed());
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}