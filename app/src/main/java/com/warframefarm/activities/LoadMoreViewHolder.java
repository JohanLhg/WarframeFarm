package com.warframefarm.activities;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.databinding.RecyclerLoadMoreBinding;

import org.jetbrains.annotations.NotNull;

public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

    public ConstraintLayout layoutLoadMore;

    public LoadMoreViewHolder(@NonNull @NotNull RecyclerLoadMoreBinding binding) {
        super(binding.getRoot());
        layoutLoadMore = binding.layoutLoadMore;
    }
}