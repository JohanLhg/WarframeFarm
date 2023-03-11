package com.warframefarm.activities.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.Item;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.databinding.RecyclerPrimeBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.PrimeViewHolder> {

    private final Context context;
    private final ItemListener listener;
    private List<Item> items = new ArrayList<>(),
            selectedItems = new ArrayList<>();
    private boolean selectionMode = false;

    //Payloads
    private static final int OWNED = 0, SELECT = 1;

    public ItemAdapter(Context context, ItemListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public PrimeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new PrimeViewHolder(RecyclerPrimeBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PrimeViewHolder holder, int position) {
        Item item = items.get(position);
        String primeName = item.getName();

        holder.textPrimeName.setText(primeName);

        item.displayImage(context, holder.imagePrime);

        if (item instanceof PrimeComplete) {
            PrimeComplete prime = (PrimeComplete) item;
            holder.imageType.setImageResource(prime.getImageType());

            holder.imagePrimeVault.setVisibility(View.VISIBLE);
            holder.imagePrimeVault.setVisibility(prime.isVaulted() ? View.VISIBLE : View.GONE);
        }

        holder.imageOwned.setOnClickListener(v -> listener.switchOwned(item));

        holder.imageOwned.setImageResource(item.isOwned() ? R.drawable.owned : R.drawable.not_owned);

        holder.layoutPrime.setOnClickListener(v -> {
            if (selectionMode)
                selectItem(holder.getAdapterPosition());
            else
                listener.showDetails(primeName);
        });

        holder.layoutPrime.setOnLongClickListener(v -> {
            if (selectionMode) {
                if (selectedItems.contains(item))
                    unselectItem(holder.getAdapterPosition());
                else {
                    int from = items.lastIndexOf(selectedItems.get(selectedItems.size() - 1));
                    if (from == -1) {
                        selectItem(holder.getAdapterPosition());
                        return true;
                    }
                    int length = position - from;
                    if (length < 0) {
                        from += length;
                        length *= -1;
                    }
                    else length++;
                    int to = from + length;
                    Item tmp;
                    for (int i=from; i<to; i++) {
                        tmp = items.get(i);
                        if (!selectedItems.contains(tmp))
                            selectedItems.add(tmp);
                    }
                    notifyItemRangeChanged(from, length, SELECT);
                    setSelectionParams();
                }
            }
            else selectItem(holder.getAdapterPosition());
            return true;
        });

        if (selectionMode && selectedItems.contains(item))
            holder.imageSelect.setVisibility(View.VISIBLE);
        else
            holder.imageSelect.setVisibility(View.GONE);
    }

    @Override
    public void onBindViewHolder(@NonNull PrimeViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.contains(OWNED)) {
            Item item = items.get(position);

            holder.imageOwned.setImageResource(item.isOwned() ? R.drawable.owned : R.drawable.not_owned);
        }
        if (payloads.contains(SELECT)) {
            Item item = items.get(position);

            if (selectionMode && selectedItems.contains(item))
                holder.imageSelect.setVisibility(View.VISIBLE);
            else
                holder.imageSelect.setVisibility(View.GONE);
        }
        else onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private void selectItem(int position) {
        Item item = items.get(position);

        boolean wasEmpty = selectedItems.isEmpty();

        if (selectedItems.contains(item))
            selectedItems.remove(item);
        else selectedItems.add(item);
        notifyItemChanged(position, SELECT);

        if (selectedItems.isEmpty()) {
            selectionMode = false;
            listener.setSelectionMode(false);
        }
        else {
            setSelectionParams();

            if (wasEmpty) {
                selectionMode = true;
                listener.setSelectionMode(true);
            }
        }
    }

    private void unselectItem(int position) {
        Item item = items.get(position);

        selectedItems.remove(item);
        notifyItemChanged(position, SELECT);

        setSelectionParams();
        if (selectedItems.isEmpty()) {
            selectionMode = false;
            listener.setSelectionMode(false);
        }
    }

    private void setSelectionParams() {
        boolean check = false;
        for (Item item : selectedItems) {
            if (!item.isOwned()) {
                check = true;
                break;
            }
        }
        listener.setSelectionParams(check, selectedItems.size());
    }

    public List<String> getSelectedItems() {
        List<String> names = new ArrayList<>();
        for (Item item : selectedItems)
            names.add(item.getName());
        return names;
    }

    public void selectAll() {
        List<Item> tmp = new ArrayList<>(items);
        for (Item item : selectedItems) {
            if (!tmp.contains(item))
                tmp.add(item);
        }
        selectedItems = tmp;
        setSelectionParams();
        notifyItemRangeChanged(0, getItemCount());
    }

    public void cancelSelection() {
        selectedItems.clear();
        selectionMode = false;
        listener.setSelectionMode(false);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void endSelection() {
        selectedItems.clear();
        selectionMode = false;
        listener.setSelectionMode(false);
    }

    public interface ItemListener {
        void showDetails(String id);
        void switchOwned(Item item);
        void setSelectionMode(boolean selectionMode);
        void setSelectionParams(boolean check, int nb);
    }

    public static class PrimeViewHolder extends RecyclerView.ViewHolder {

        public final ConstraintLayout layoutPrime;
        public final TextView textPrimeName;
        public final ImageView imageSelect, imagePrime, imageType, imagePrimeVault, imageOwned;

        public PrimeViewHolder(@NonNull @NotNull RecyclerPrimeBinding binding) {
            super(binding.getRoot());
            layoutPrime = binding.layoutPrime;
            textPrimeName = binding.textPrimeName;
            imageSelect = binding.imageSelect;
            imagePrime = binding.imagePrime;
            imageType = binding.imageType;
            imagePrimeVault = binding.imagePrimeVault;
            imageOwned = binding.imageOwned;
        }
    }
}
