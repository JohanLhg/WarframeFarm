package com.warframefarm.activities.list.components;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ComponentViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout layoutComponent;
    ImageView imageComponent, imageOwned, imageVault;
    TextView textName;

    public ComponentViewHolder(com.warframefarm.databinding.RecyclerComponentBinding binding) {
        super(binding.getRoot());
        layoutComponent = binding.layoutComponent;
        imageComponent = binding.imageComponent;
        imageOwned = binding.imageOwned;
        imageVault = binding.imageVault;
        textName = binding.textName;
    }
}
