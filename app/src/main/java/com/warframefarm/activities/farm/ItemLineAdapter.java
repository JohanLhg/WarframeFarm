package com.warframefarm.activities.farm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.component.ComponentFragment;
import com.warframefarm.activities.details.prime.PrimeFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.Item;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.databinding.RecyclerItemLineBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemLineAdapter extends RecyclerView.Adapter<ItemLineAdapter.ItemViewHolder> {

    private final Context context;
    private final ItemListener listener;
    private List<Item> items = new ArrayList<>();

    public ItemLineAdapter(Context context, ItemListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(RecyclerItemLineBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemViewHolder holder, int position) {
        Item item = items.get(position);

        holder.textItemName.setText(item.getFullName());

        holder.imageItem.setBackgroundResource(R.color.transparent);
        item.displayImage(context, holder.imageItem);

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

        public final ConstraintLayout layoutItem;
        public final TextView textItemName;
        public final ImageView imageItem, buttonDelete;

        public ItemViewHolder(@NonNull @NotNull RecyclerItemLineBinding binding) {
            super(binding.getRoot());
            layoutItem = binding.layoutItem;
            textItemName = binding.textItemName;
            imageItem = binding.imageItem;
            buttonDelete = binding.buttonDelete;
        }
    }
}
