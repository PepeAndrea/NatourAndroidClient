package com.exam.natour.UI.Adapter.InterestPointAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exam.natour.Model.PathDetailResponse.InterestPoint;
import com.exam.natour.Model.PathsResponse.Path;
import com.exam.natour.R;
import com.exam.natour.UI.Adapter.PathAdapter.PathViewHolder;

import java.util.List;

public class InterestPointAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<InterestPoint> interestPoints;

    public InterestPointAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_point_item,parent,false);
        return new InterestPointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((InterestPointViewHolder) holder).iterestPointTitle.setText(interestPoints.get(position).getTitle());
        ((InterestPointViewHolder) holder).iterestPointDescription.setText(interestPoints.get(position).getDescription());
        ((InterestPointViewHolder) holder).iterestPointCategory.setText(interestPoints.get(position).getCategoryId());

    }

    @Override
    public int getItemCount() {
        if (interestPoints != null)
            return interestPoints.size();
        return 0;
    }

    public void setInterestPoints(List<InterestPoint> interestPoints) {
        this.interestPoints = interestPoints;
        notifyDataSetChanged();
    }
}
