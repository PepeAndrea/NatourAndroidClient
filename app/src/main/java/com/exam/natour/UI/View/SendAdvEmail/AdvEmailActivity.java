package com.exam.natour.UI.View.SendAdvEmail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.exam.natour.UI.View.Setting.SettingViewModel;
import com.exam.natour.databinding.ActivityAdvEmailBinding;
import com.exam.natour.databinding.FragmentSettingBinding;

public class AdvEmailActivity extends AppCompatActivity {

    private AdvEmailViewModel advEmailViewModel;
    private ActivityAdvEmailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        advEmailViewModel =
                new ViewModelProvider(this).get(AdvEmailViewModel.class);

        binding = ActivityAdvEmailBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        binding.sendEmailBtn.setOnClickListener(view -> {
            Log.i("EmailADV", "Inizio invio email promozionale");
            binding.sendEmailBtn.setEnabled(false);
            binding.goBackBtn.setEnabled(false);
            if(validateEmailAdvInput()){
                advEmailViewModel.sendEmail(view.getContext(),binding.emailTitle.getText().toString(),binding.emailContent.getText().toString());
            }else{
                Log.e("Validazione input email adv", "I campi non risultano validi");
                binding.sendEmailBtn.setEnabled(true);
                binding.goBackBtn.setEnabled(true);
            }
        });



        binding.goBackBtn.setOnClickListener(view -> onBackPressed());

    }

    private boolean validateEmailAdvInput() {
        boolean validated = true;

        if(binding.emailTitle.getText().toString().length() == 0){
            binding.emailTitle.setError("Il campo Titolo non può essere vuoto");
            validated = false;
        }
        if(binding.emailContent.getText().toString().length() == 0){
            binding.emailContent.setError("Il campo Contenuto non può essere vuoto");
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