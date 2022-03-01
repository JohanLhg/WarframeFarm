package com.warframefarm.activities.inventory;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class InventoryViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout layoutInventory;
    ImageView imagePart, imageOwned, imageVault;
    TextView textName;

    public InventoryViewHolder(com.warframefarm.databinding.RecyclerInventoryBinding binding) {
        super(binding.getRoot());
        layoutInventory = binding.layoutInventory;
        imagePart = binding.imagePart;
        imageOwned = binding.imageOwned;
        imageVault = binding.imageVault;
        textName = binding.textPart;
    }
}
