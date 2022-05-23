package com.warframefarm.activities.details.mission;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.databinding.RecyclerMissionRewardCategoryBinding;

import org.jetbrains.annotations.NotNull;

public class MissionRewardCategoryViewHolder extends RecyclerView.ViewHolder {

    public TextView textCategory;
    public RecyclerView recyclerMissionRewards;

    public MissionRewardCategoryViewHolder(@NonNull @NotNull RecyclerMissionRewardCategoryBinding binding) {
        super(binding.getRoot());
        textCategory = binding.textCategory;
        recyclerMissionRewards = binding.recyclerMissionRewards;
    }
}
