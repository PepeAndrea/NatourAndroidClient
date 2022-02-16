package com.exam.natour.UI.Adapter.PathAdapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.exam.natour.Activity.MainActivity;
import com.exam.natour.R;
import com.exam.natour.UI.View.PathDetail.PathDetailActivity;

public class PathViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView pathTitle,pathDescription,pathDifficulty,pathLength,pathDuration,pathUser,pathLocation,pathId;
    ImageView pathImage;

    public PathViewHolder(@NonNull View itemView) {
        super(itemView);

        pathTitle = itemView.findViewById(R.id.pathTitle);

        //pathDescription = itemView.findViewById(R.id.pathDescription);
        pathDifficulty = itemView.findViewById(R.id.pathDifficulty);
        //pathLength = itemView.findViewById(R.id.pathLength);
        //pathDuration = itemView.findViewById(R.id.pathDuration);
        pathUser = itemView.findViewById(R.id.pathUser);
        pathLocation = itemView.findViewById(R.id.pathLocation);
        pathId = itemView.findViewById(R.id.pathId);
        pathImage = itemView.findViewById(R.id.pathImage);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), PathDetailActivity.class);
                // Pass data object in the bundle and populate details activity.
                intent.putExtra("pathTitle", pathTitle.getText().toString());
                intent.putExtra("pathId", pathId.getText().toString());

                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation((Activity) view.getContext(),pathImage,"pathImageTransition");

                view.getContext().startActivity(intent, options.toBundle());

                Log.i("Percorso selezionato", "id percorso: "+pathId.getText().toString());

            }
        });

    }

    @Override
    public void onClick(View view) {
    }
}
