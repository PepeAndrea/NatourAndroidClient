package com.exam.natour.UI.Adapter.PathAdapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.exam.natour.R;

public class PathViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView pathTitle,pathDescription,pathDifficulty,pathLength,pathDuration,pathUser,pathLocation;

    public PathViewHolder(@NonNull View itemView) {
        super(itemView);

        pathTitle = itemView.findViewById(R.id.pathTitle);

        //pathDescription = itemView.findViewById(R.id.pathDescription);
        pathDifficulty = itemView.findViewById(R.id.pathDifficulty);
        //pathLength = itemView.findViewById(R.id.pathLength);
        //pathDuration = itemView.findViewById(R.id.pathDuration);
        pathUser = itemView.findViewById(R.id.pathUser);
        pathLocation = itemView.findViewById(R.id.pathLocation);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public void onClick(View view) {

    }
}
