package com.warframefarm.activities.farm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.PartComplete;
import com.warframefarm.databinding.RecyclerSelectItemBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectPartAdapter extends RecyclerView.Adapter<SelectItemViewHolder> {

    private final Context context;
    private List<PartComplete> parts = new ArrayList<>();
    private final List<String> selectedPartNames = new ArrayList<>();
    private final List<PartComplete> selectedParts = new ArrayList<>();

    //PAYLOAD
    private static final int SELECT = 0;

    public SelectPartAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public SelectItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new SelectItemViewHolder(RecyclerSelectItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectItemViewHolder holder, int position) {
        PartComplete part = parts.get(position);
        String fullName = part.getFullName();
        String itemId = part.getId();
        int image = part.getImage();

        holder.textItemName.setText(fullName);

        if (image == 0)
            holder.imageItem.setImageResource(R.drawable.primes);
        else
            holder.imageItem.setImageResource(image);

        if (part.isBlueprint())
            holder.imageItem.setBackgroundResource(R.drawable.blueprint_bg);
        else
            holder.imageItem.setBackgroundResource(R.color.transparent);

        holder.layoutItem.setOnClickListener(v -> {
            String id = part.getId();

            if (selectedPartNames.contains(id)) {
                selectedPartNames.remove(id);
                selectedParts.remove(part);
            }
            else {
                selectedPartNames.add(id);
                selectedParts.add(part);
            }
            notifyItemChanged(holder.getAdapterPosition());
        });

        if (part.isVaulted())
            holder.imagePrimeVault.setVisibility(View.VISIBLE);
        else
            holder.imagePrimeVault.setVisibility(View.GONE);

        if (selectedPartNames.contains(itemId)) {
            holder.layoutItem.setBackgroundColor(context.getColor(R.color.textBackground));
            holder.itemSeparator.setBackgroundColor(context.getColor(R.color.colorBackgroundDark));
        }
        else {
            holder.layoutItem.setBackgroundColor(context.getColor(R.color.transparent));
            holder.itemSeparator.setBackgroundColor(context.getColor(R.color.textBackground));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SelectItemViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.contains(SELECT)) {
            PartComplete part = parts.get(position);

            if (selectedPartNames.contains(part.getId())) {
                holder.layoutItem.setBackgroundColor(context.getColor(R.color.textBackground));
                holder.itemSeparator.setBackgroundColor(context.getColor(R.color.colorBackgroundDark));
            }
            else {
                holder.layoutItem.setBackgroundColor(context.getColor(R.color.transparent));
                holder.itemSeparator.setBackgroundColor(context.getColor(R.color.textBackground));
            }
        }
        else onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return parts.size();
    }

    public void updateItems(List<PartComplete> parts) {
        this.parts = parts;
        notifyDataSetChanged();
    }

    public List<String> getSelectedPartNames() {
        return selectedPartNames;
    }

    public List<PartComplete> getSelectedParts() {
        return selectedParts;
    }
}
