package com.warframefarm.activities.farm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.databinding.RecyclerSelectItemBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectPrimeAdapter extends RecyclerView.Adapter<SelectItemViewHolder> {

    private final Context context;
    private List<PrimeComplete> primes;
    private final List<String> selectedPrimeNames = new ArrayList<>();
    private final List<PrimeComplete> selectedPrimes = new ArrayList<>();

    //PAYLOAD
    private static final int SELECT = 0;

    public SelectPrimeAdapter(Context context) {
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
        PrimeComplete prime = primes.get(position);
        String fullName = prime.getFullName();
        String itemId = prime.getName();
        int image = prime.getImage();

        holder.textItemName.setText(fullName);

        if (image == 0)
            holder.imageItem.setImageResource(R.drawable.primes);
        else
            holder.imageItem.setImageResource(image);

        holder.layoutItem.setOnClickListener(v -> {
            String id = prime.getName();

            if (selectedPrimeNames.contains(id)) {
                selectedPrimeNames.remove(id);
                selectedPrimes.remove(prime);
            }
            else {
                selectedPrimeNames.add(id);
                selectedPrimes.add(prime);
            }
            notifyItemChanged(holder.getAdapterPosition());
        });

        if (prime.isVaulted())
            holder.imagePrimeVault.setVisibility(View.VISIBLE);
        else
            holder.imagePrimeVault.setVisibility(View.GONE);

        if (selectedPrimeNames.contains(itemId)) {
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
            PrimeComplete prime = primes.get(position);

            if (selectedPrimeNames.contains(prime.getName())) {
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
        return primes.size();
    }

    public void updateItems(List<PrimeComplete> primes) {
        this.primes = primes;
        notifyDataSetChanged();
    }

    public List<String> getSelectedPrimeNames() {
        return selectedPrimeNames;
    }

    public List<PrimeComplete> getSelectedPrimes() {
        return selectedPrimes;
    }
}
