package com.warframefarm.activities.farm;

import static com.warframefarm.data.WarframeConstants.BLUEPRINT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.component.ComponentFragment;
import com.warframefarm.activities.details.prime.PrimeFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.Item;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.databinding.RecyclerItemBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final Context context;
    private final ItemListener listener;
    private List<Item> items = new ArrayList<>();

    public ItemAdapter(Context context, ItemListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(RecyclerItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemViewHolder holder, int position) {
        Item item = items.get(position);
        String fullName = "";

        if (item instanceof PrimeComplete) {
            PrimeComplete prime = (PrimeComplete) item;
            fullName = prime.getFullName();
            holder.imageItem.setBackgroundResource(R.color.transparent);
            FirestoreHelper.loadPrimeImage(prime.getName(), context, holder.imageItem);
        }
        else {
            if (item instanceof ComponentComplete) {
                ComponentComplete component = (ComponentComplete) item;
                fullName = component.getFullName();

                holder.imageItem.setBackgroundResource(component.isBlueprint() ? R.drawable.blueprint_bg : R.color.transparent);

                if (component.getType().equals(BLUEPRINT))
                    FirestoreHelper.loadPrimeImage(component.getPrime(), context, holder.imageItem);
                else
                    holder.imageItem.setImageResource(component.getImage());
            }
        }

        holder.textItemName.setText(fullName);

        holder.layoutItem.setOnClickListener(v -> {
            Item i = items.get(holder.getAdapterPosition());
            if (i instanceof PrimeComplete)
                showPrimeDetails(i.getId());
            else if (i instanceof ComponentComplete)
                showComponentDetails(i.getId());
        });

        holder.buttonDelete.setOnClickListener(v -> removeItem(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void removeItem(int pos) {
        listener.removeItem(items.get(pos));
    }

    private void showPrimeDetails(String prime_name) {
        ((MainActivity) context).multipleStackNavigator.start(new PrimeFragment(prime_name));
    }

    private void showComponentDetails(String component_id) {
        ((MainActivity) context).multipleStackNavigator.start(new ComponentFragment(component_id));
    }

    public interface ItemListener {
        void removeItem(Item item);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layoutItem;
        TextView textItemName;
        ImageView imageItem;
        ImageButton buttonDelete;

        public ItemViewHolder(@NonNull @NotNull RecyclerItemBinding binding) {
            super(binding.getRoot());
            layoutItem = binding.layoutItem;
            textItemName = binding.textItemName;
            imageItem = binding.imageItem;
            buttonDelete = binding.buttonDelete;
        }
    }
}
