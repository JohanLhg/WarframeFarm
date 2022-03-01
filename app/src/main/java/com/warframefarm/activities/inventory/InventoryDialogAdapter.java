package com.warframefarm.activities.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.part.PartFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.PartComplete;
import com.warframefarm.databinding.RecyclerInventoryBinding;

import java.util.List;

public class InventoryDialogAdapter extends RecyclerView.Adapter<InventoryViewHolder> {

    private final Context context;

    private List<PartComplete> partsAfterChanges;

    public InventoryDialogAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InventoryViewHolder(RecyclerInventoryBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryViewHolder holder, final int position) {
        PartComplete part = partsAfterChanges.get(position);

        final String id = part.getId();
        holder.layoutInventory.setOnClickListener(v -> onClickPart(id));

        //Set image for the type of prime
        holder.imagePart.setImageResource(part.getImage());
        holder.imagePart.setBackgroundResource(part.isBlueprint() ? R.drawable.blueprint_bg : R.color.transparent);

        //Set name and number needed
        holder.textName.setText(part.getFullName());

        //Owned
        holder.imageOwned.setImageResource(part.isOwned() ? R.drawable.owned : R.drawable.not_owned);
    }

    @Override
    public int getItemCount() {
        return partsAfterChanges.size();
    }

    public void updateParts(List<PartComplete> partsAfterChanges){
        this.partsAfterChanges = partsAfterChanges;
        notifyDataSetChanged();
    }

    private void onClickPart(String part_id) {
        ((MainActivity) context).multipleStackNavigator.start(new PartFragment(part_id));
    }
}
