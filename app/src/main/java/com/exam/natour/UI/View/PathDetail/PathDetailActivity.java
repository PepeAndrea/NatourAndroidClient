package com.exam.natour.UI.View.PathDetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.transition.TransitionInflater;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.exam.natour.Model.PathDetailResponse.PathDetail;
import com.exam.natour.Model.PathsResponse.Path;
import com.exam.natour.R;
import com.exam.natour.UI.View.Home.HomeViewModel;
import com.exam.natour.databinding.ActivityPathDetailBinding;
import com.exam.natour.databinding.FragmentHomeBinding;

import java.util.List;

public class PathDetailActivity extends AppCompatActivity {

    private ActivityPathDetailBinding binding;
    private PathDetailViewModel pathDetailViewModel;


    String imageTransitionName = "pathImageTransition";
    String titleTransition = "pathTitleTransition";

    TextView pathTitle,pathDescription,pathDifficulty,pathLength,pathDuration,pathUser,pathLocation,pathId;
    ImageView pathImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //setup data binding
        pathDetailViewModel = new ViewModelProvider(this).get(PathDetailViewModel.class);
        binding = ActivityPathDetailBinding.inflate(getLayoutInflater());

        setContentView(R.layout.activity_path_detail);

        //Bind delle viewa
        pathImage = findViewById(R.id.pathImage);
        pathTitle = findViewById(R.id.pathTitle);
        pathDescription = findViewById(R.id.pathDescription);
        pathDifficulty = findViewById(R.id.pathDifficulty);
        pathLength = findViewById(R.id.pathLength);
        pathDuration = findViewById(R.id.pathDuration);
        pathLocation = findViewById(R.id.pathLocation);


        //Recupero informazioni da Intent e imposto i dat a disposizione
        Bundle extras = getIntent().getExtras();
        pathTitle.setText(extras.getString("pathTitle",""));

        //animazione di apertura
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                                                    .inflateTransition(R.transition.shared_element_transaction));

        pathImage.setTransitionName(imageTransitionName);

        this.ObserveChange(extras.getString("pathId"));

    }


    private void ObserveChange(String id){
        this.pathDetailViewModel.getLoadedPath(this,id).observe(this, new Observer<PathDetail>() {
            @Override
            public void onChanged(PathDetail pathDetail) {
                Log.i("Percorso caricato",pathDetail.getTitle());
                //Imposto i dati
                pathDescription.setText(pathDetail.getDescription());
                pathDifficulty.setText(pathDetail.getDifficultyId());
                pathLength.setText(String.valueOf(pathDetail.getLength()));
                pathDuration.setText(String.valueOf(pathDetail.getDuration()));
                pathLocation.setText(pathDetail.getLocation());
                //Li rendo visibili
                pathDescription.setVisibility(View.VISIBLE);
                pathDifficulty.setVisibility(View.VISIBLE);
                pathLength.setVisibility(View.VISIBLE);
                pathDuration.setVisibility(View.VISIBLE);
                pathLocation.setVisibility(View.VISIBLE);

            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}