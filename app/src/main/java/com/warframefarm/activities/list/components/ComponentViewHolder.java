package com.warframefarm.activities.list.components;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ComponentViewHolder extends RecyclerView.ViewHolder {

    public final ConstraintLayout layoutComponent;
    public final ImageView imageComponent, imageOwned, imageVault;
    public final TextView textName;

    public ComponentViewHolder(com.warframefarm.databinding.RecyclerComponentBinding binding) {
        super(binding.getRoot());
        layoutComponent = binding.layoutComponent;
        imageComponent = binding.imageComponent;
        imageOwned = binding.imageOwned;
        imageVault = binding.imageVault;
        textName = binding.textName;
    }
}
