package com.exam.natour.UI.View.PathReport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
            if (validateReportInput()){
                pathReportViewModel.reportPath(view.getContext(),extras.getString("pathId",""));
            }else{
                binding.sendReportBtn.setEnabled(true);
                binding.goBackBtn.setEnabled(true);
                Log.e("Errore validazione input report", "Input non valido");
            }
        });



        binding.goBackBtn.setOnClickListener(view -> onBackPressed());
    }


    private boolean validateReportInput() {
        boolean validated = true;

        if(binding.reportTitle.length() == 0){
            binding.reportTitle.setError("Il campo titolo non può essere vuoto");
            validated = false;
        }
        if(binding.reportContent.length() == 0){
            binding.reportContent.setError("Il campo descrizione non può essere vuoto");
            validated = false;
        }

        return validated;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}