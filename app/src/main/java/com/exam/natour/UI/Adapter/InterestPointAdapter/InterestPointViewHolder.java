package com.exam.natour.UI.Adapter.InterestPointAdapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.exam.natour.R;
import com.exam.natour.UI.View.PathDetail.PathDetailActivity;

public class InterestPointViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView iterestPointTitle,iterestPointDescription,iterestPointCategory;

    public InterestPointViewHolder(@NonNull View itemView) {
        super(itemView);

        iterestPointTitle = itemView.findViewById(R.id.iterestPointTitle);
        iterestPointDescription = itemView.findViewById(R.id.iterestPointDescription);
        iterestPointCategory = itemView.findViewById(R.id.iterestPointCategory);



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
