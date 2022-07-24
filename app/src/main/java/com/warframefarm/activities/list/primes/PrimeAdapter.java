package com.warframefarm.activities.list.primes;

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
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.databinding.RecyclerPrimeBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PrimeAdapter extends RecyclerView.Adapter<PrimeAdapter.PrimeViewHolder> {

    private final Context context;
    private final PrimeListener listener;
    private List<PrimeComplete> primes = new ArrayList<>(),
            selectedPrimes = new ArrayList<>();
    private boolean selectionMode = false;

    //Payloads
    private static final int OWNED = 0, SELECT = 1;

    public PrimeAdapter(Context context, PrimeListener listener) {
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
        PrimeComplete prime = primes.get(position);
        String primeName = prime.getName();

        holder.textPrimeName.setText(primeName);

        FirestoreHelper.loadPrimeImage(primeName, context, holder.imagePrime);

        holder.imageType.setImageResource(prime.getImageType());

        holder.imagePrimeVault.setVisibility(prime.isVaulted() ? View.VISIBLE : View.GONE);

        holder.imageOwned.setOnClickListener(v -> listener.switchPrimeOwned(prime));

        holder.imageOwned.setImageResource(prime.isOwned() ? R.drawable.owned : R.drawable.not_owned);

        holder.layoutPrime.setOnClickListener(v -> {
            if (selectionMode)
                selectPrime(holder.getAdapterPosition());
            else
                listener.showPrimeDetails(primeName);
        });

        holder.layoutPrime.setOnLongClickListener(v -> {
            if (selectionMode) {
                if (selectedPrimes.contains(prime))
                    unselectPrime(holder.getAdapterPosition());
                else {
                    int from = primes.lastIndexOf(selectedPrimes.get(selectedPrimes.size() - 1));
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
                        p = primes.get(i);
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
            PrimeComplete prime = primes.get(position);

            holder.imageOwned.setImageResource(prime.isOwned() ? R.drawable.owned : R.drawable.not_owned);
        }
        if (payloads.contains(SELECT)) {
            PrimeComplete prime = primes.get(position);

            if (selectionMode && selectedPrimes.contains(prime))
                holder.imageSelect.setVisibility(View.VISIBLE);
            else
                holder.imageSelect.setVisibility(View.GONE);
        }
        else onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return primes.size();
    }

    public void updatePrimes(List<PrimeComplete> primes) {
        this.primes = primes;
        notifyDataSetChanged();
    }

    private void selectPrime(int position) {
        PrimeComplete prime = primes.get(position);

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
        PrimeComplete prime = primes.get(position);

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
        List<PrimeComplete> tmp = new ArrayList<>(primes);
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
        void showPrimeDetails(String prime);
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
