package com.warframefarm.activities.farm;

import static com.warframefarm.data.WarframeConstants.BLUEPRINT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.databinding.RecyclerSelectItemBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectComponentAdapter extends RecyclerView.Adapter<SelectItemViewHolder> {

    private final Context context;
    private List<ComponentComplete> components = new ArrayList<>();
    private final List<String> selectedComponentNames = new ArrayList<>();
    private final List<ComponentComplete> selectedComponents = new ArrayList<>();

    //PAYLOAD
    private static final int SELECT = 0;

    public SelectComponentAdapter(Context context) {
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
        ComponentComplete component = components.get(position);
        String fullName = component.getFullName();
        String itemId = component.getId();

        holder.textItemName.setText(fullName);

        holder.imageItem.setBackgroundResource(component.isBlueprint() ? R.drawable.blueprint_bg : R.color.transparent);

        if (component.getType().equals(BLUEPRINT))
            FirestoreHelper.loadPrimeImage(component.getPrime(), context, holder.imageItem);
        else
            holder.imageItem.setImageResource(component.getImage());

        holder.layoutItem.setOnClickListener(v -> {
            String id = component.getId();

            if (selectedComponentNames.contains(id)) {
                selectedComponentNames.remove(id);
                selectedComponents.remove(component);
            }
            else {
                selectedComponentNames.add(id);
                selectedComponents.add(component);
            }
            notifyItemChanged(holder.getAdapterPosition());
        });

        if (component.isVaulted())
            holder.imagePrimeVault.setVisibility(View.VISIBLE);
        else
            holder.imagePrimeVault.setVisibility(View.GONE);

        if (selectedComponentNames.contains(itemId)) {
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
            ComponentComplete component = components.get(position);

            if (selectedComponentNames.contains(component.getId())) {
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
        return components.size();
    }

    public void updateItems(List<ComponentComplete> components) {
        this.components = components;
        notifyDataSetChanged();
    }

    public List<String> getSelectedComponentNames() {
        return selectedComponentNames;
    }

    public List<ComponentComplete> getSelectedComponents() {
        return selectedComponents;
    }
}
