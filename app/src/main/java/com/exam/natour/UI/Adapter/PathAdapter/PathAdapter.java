package com.exam.natour.UI.Adapter.PathAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.exam.natour.R;
import com.exam.natour.Model.PathsResponse.Path;

import java.util.List;

public class PathAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Path> mPath;

    public PathAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.path_list_item,parent,false);
        return new PathViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((PathViewHolder) holder).pathTitle.setText(mPath.get(position).getTitle());
        //((PathViewHolder) holder).pathDescription.setText(mPath.get(position).getDescription());
        ((PathViewHolder) holder).pathDifficulty.setText(mPath.get(position).getDifficulty());
        //((PathViewHolder) holder).pathLength.setText(String.valueOf(mPath.get(position).getLength())+"Km");
        //((PathViewHolder) holder).pathDuration.setText(String.valueOf(mPath.get(position).getDuration())+'h');
        ((PathViewHolder) holder).pathUser.setText("@"+mPath.get(position).getUsername());
        ((PathViewHolder) holder).pathLocation.setText(mPath.get(position).getLocation());
        ((PathViewHolder) holder).pathId.setText(String.valueOf(mPath.get(position).getId()));

    }

    @Override
    public int getItemCount() {
        if (mPath != null)
            return mPath.size();
        return 0;
    }

    public void setPaths(List<Path> mPath) {
        this.mPath = mPath;
        notifyDataSetChanged();
    }
}
