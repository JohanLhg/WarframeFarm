package com.warframefarm.activities.list.primes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.prime.PrimeFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.databinding.RecyclerPrimeBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PrimeListAdapter extends ListAdapter<PrimeComplete, PrimeListAdapter.PrimeViewHolder> {

    private final Context context;
    private final PrimeListener listener;
    private List<PrimeComplete> selectedPrimes = new ArrayList<>();
    private boolean selectionMode = false;

    //Payloads
    private static final int OWNED = 0, SELECT = 1;

    private static final DiffUtil.ItemCallback<PrimeComplete> DIFF_CALLBACK = new DiffUtil.ItemCallback<PrimeComplete>() {
        @Override
        public boolean areItemsTheSame(@NonNull PrimeComplete oldItem, @NonNull PrimeComplete newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PrimeComplete oldItem, @NonNull PrimeComplete newItem) {
            return oldItem.isOwned() == newItem.isOwned() &&
                    oldItem.isVaulted() == newItem.isVaulted();
        }
    };

    protected PrimeListAdapter(Context context, PrimeListener listener) {
        super(DIFF_CALLBACK);
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
        PrimeComplete prime = getItem(position);
        String primeName = prime.getName();

        holder.textPrimeName.setText(primeName);

        holder.imagePrime.setImageResource(prime.getImage());

        holder.imageType.setImageResource(prime.getImageType());

        holder.imagePrimeVault.setVisibility(prime.isVaulted() ? View.VISIBLE : View.GONE);

        holder.imageOwned.setOnClickListener(v -> listener.switchPrimeOwned(prime));

        holder.imageOwned.setImageResource(prime.isOwned() ? R.drawable.owned : R.drawable.not_owned);

        holder.layoutPrime.setOnClickListener(v -> {
            if (selectionMode)
                selectPrime(holder.getAdapterPosition());
            else
                showPrimeDetails(primeName);
        });

        holder.layoutPrime.setOnLongClickListener(v -> {
            if (selectionMode) {
                if (selectedPrimes.contains(prime))
                    unselectPrime(holder.getAdapterPosition());
                else {
                    int from = getCurrentList().lastIndexOf(selectedPrimes.get(selectedPrimes.size() - 1));
                    if (from == -1) {
                        selectPrime(holder.getAdapterPosition());
                        return true;
                    }
                    int length = position - from;
                    if (length < 0) {
                        from += length;
                        length *= -1;
                    }
                    else length++;
                    int to = from + length;
                    PrimeComplete p;
                    for (int i=from; i<to; i++) {
                        p = getItem(i);
                        if (!selectedPrimes.contains(p))
                            selectedPrimes.add(p);
                    }
                    notifyItemRangeChanged(from, length, SELECT);
                    setSelectionParams();
                }
            }
            else selectPrime(holder.getAdapterPosition());
            return true;
        });

        if (selectionMode && selectedPrimes.contains(prime))
            holder.imageSelect.setVisibility(View.VISIBLE);
        else
            holder.imageSelect.setVisibility(View.GONE);
    }

    @Override
    public void onBindViewHolder(@NonNull PrimeViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.contains(OWNED)) {
            PrimeComplete prime = getItem(position);

            holder.imageOwned.setImageResource(prime.isOwned() ? R.drawable.owned : R.drawable.not_owned);
        }
        if (payloads.contains(SELECT)) {
            PrimeComplete prime = getItem(position);

            if (selectionMode && selectedPrimes.contains(prime))
                holder.imageSelect.setVisibility(View.VISIBLE);
            else
                holder.imageSelect.setVisibility(View.GONE);
        }
        else onBindViewHolder(holder, position);
    }

    private void showPrimeDetails(String prime_name) {
        ((MainActivity) context).multipleStackNavigator.start(new PrimeFragment(prime_name));
    }

    private void selectPrime(int position) {
        PrimeComplete prime = getItem(position);

        boolean wasEmpty = selectedPrimes.isEmpty();

        if (selectedPrimes.contains(prime))
            selectedPrimes.remove(prime);
        else selectedPrimes.add(prime);
        notifyItemChanged(position, SELECT);

        if (selectedPrimes.isEmpty()) {
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

    private void unselectPrime(int position) {
        PrimeComplete prime = getItem(position);

        selectedPrimes.remove(prime);
        notifyItemChanged(position, SELECT);

        setSelectionParams();
        if (selectedPrimes.isEmpty()) {
            selectionMode = false;
            listener.setSelectionMode(false);
        }
    }

    private void setSelectionParams() {
        boolean check = false;
        for (PrimeComplete p : selectedPrimes) {
            if (!p.isOwned()) {
                check = true;
                break;
            }
        }
        listener.setSelectionParams(check, selectedPrimes.size());
    }

    public List<String> getSelectedPrimes() {
        List<String> names = new ArrayList<>();
        for (PrimeComplete prime : selectedPrimes)
            names.add(prime.getName());
        return names;
    }

    public void selectAll() {
        List<PrimeComplete> tmp = new ArrayList<>(getCurrentList());
        for (PrimeComplete prime : selectedPrimes) {
            if (!tmp.contains(prime))
                tmp.add(prime);
        }
        selectedPrimes = tmp;
        setSelectionParams();
        notifyItemRangeChanged(0, getItemCount());
    }

    public void cancelSelection() {
        selectedPrimes.clear();
        selectionMode = false;
        listener.setSelectionMode(false);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void endSelection() {
        selectedPrimes.clear();
        selectionMode = false;
        listener.setSelectionMode(false);
    }

    public interface PrimeListener {
        void switchPrimeOwned(PrimeComplete prime);
        void setSelectionMode(boolean selectionMode);
        void setSelectionParams(boolean check, int nb);
    }

    public static class PrimeViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layoutPrime;
        TextView textPrimeName;
        ImageView imageSelect, imagePrime, imageType, imagePrimeVault, imageOwned;

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
