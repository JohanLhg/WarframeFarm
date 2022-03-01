package com.warframefarm.activities.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.part.PartFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.PartComplete;
import com.warframefarm.databinding.RecyclerInventoryBinding;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryViewHolder> {

    private final Context context;
    private final InventoryListener listener;
    private List<PartComplete> parts = new ArrayList<>();
    private List<PartComplete> partsAfterChanges = new ArrayList<>();

    //PAYLOAD
    private static final int OWNED = 0;

    public InventoryAdapter(Context context, InventoryListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InventoryViewHolder(RecyclerInventoryBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryViewHolder holder, final int position) {
        PartComplete part = parts.get(position);

        final String id = part.getId();
        holder.layoutInventory.setOnClickListener(v -> onClickPart(id));

        //Set image for the type of prime
        holder.imagePart.setImageResource(part.getImage());

        holder.imagePart.setBackgroundResource(part.isBlueprint() ? R.drawable.blueprint_bg : R.color.transparent);

        holder.imageVault.setVisibility(part.isVaulted() ? View.VISIBLE : View.GONE);

        //Set name and number needed
        holder.textName.setText(part.getFullName());

        //Set owned
        boolean owned;
        int part_index = partsAfterChanges.indexOf(part);
        if (part_index != -1)
            owned = partsAfterChanges.get(part_index).isOwned();
        else owned = part.isOwned();
        holder.imageOwned.setImageResource(owned ? R.drawable.owned : R.drawable.not_owned);

        holder.imageOwned.setOnClickListener(v -> {
            listener.modifyPart(part.clone());
        });
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.contains(OWNED)) {
            PartComplete part = parts.get(position);

            boolean owned;
            int part_index = partsAfterChanges.indexOf(part);
            if (part_index != -1)
                owned = partsAfterChanges.get(part_index).isOwned();
            else owned = part.isOwned();
            holder.imageOwned.setImageResource(owned ? R.drawable.owned : R.drawable.not_owned);
        }
        else super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return parts.size();
    }

    public void updateChangedParts(List<PartComplete> partsAfterChanges) {
        this.partsAfterChanges = partsAfterChanges;
        notifyItemRangeChanged(0, parts.size(), OWNED);
    }

    public void updateParts(List<PartComplete> parts){
        this.parts = parts;
        notifyDataSetChanged();
    }

    private void onClickPart(String part_id) {
        ((MainActivity) context).multipleStackNavigator.start(new PartFragment(part_id));
    }

    public interface InventoryListener {
        void modifyPart(PartComplete part);
    }
}
