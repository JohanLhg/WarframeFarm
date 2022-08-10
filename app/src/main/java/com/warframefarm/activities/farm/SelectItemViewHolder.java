package com.warframefarm.activities.farm;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.databinding.RecyclerSelectItemBinding;

import org.jetbrains.annotations.NotNull;

public class SelectItemViewHolder extends RecyclerView.ViewHolder {

    public final ConstraintLayout layoutItem;
    public final TextView textItemName;
    public final ImageView imageItem, imagePrimeVault;
    public final LinearLayout itemSeparator;

    public SelectItemViewHolder(@NonNull @NotNull RecyclerSelectItemBinding binding) {
        super(binding.getRoot());
        layoutItem = binding.layoutItem;
        textItemName = binding.textItemName;
        imageItem = binding.imageItem;
        imagePrimeVault = binding.imagePrimeVault;
        itemSeparator = binding.itemSeparator;
    }
}
