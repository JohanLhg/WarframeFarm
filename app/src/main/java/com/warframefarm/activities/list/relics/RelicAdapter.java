package com.warframefarm.activities.list.relics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.activities.details.relic.RelicFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.databinding.RecyclerRelicBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RelicAdapter extends RecyclerView.Adapter<RelicAdapter.RelicViewHolder> {

    private final Context context;
    private List<RelicComplete> relics = new ArrayList<>();

    public RelicAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public RelicViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new RelicViewHolder(RecyclerRelicBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RelicViewHolder holder, int position) {
        RelicComplete relic = relics.get(position);

        holder.textRelicName.setText(relic.getShortName());

        holder.imageVault.setVisibility(relic.isVaulted() ? View.VISIBLE : View.GONE);

        int rarityImage = relic.getImageRarity();
        if (rarityImage == -1) {
            holder.imageRarityNeeded.setVisibility(View.GONE);
            holder.imageEra.setImageResource(relic.getImage());
        } else {
            holder.imageRarityNeeded.setVisibility(View.VISIBLE);
            holder.imageRarityNeeded.setImageResource(rarityImage);
            holder.imageEra.setImageResource(relic.getImageRadiant());
        }

        holder.layoutRelic.setOnClickListener(v -> showRelicDetails(relic.getId()));
    }

    @Override
    public int getItemCount() {
        return relics.size();
    }

    public void updateRelics(List<RelicComplete> relics) {
        this.relics = relics;
        notifyDataSetChanged();
    }

    private void showRelicDetails(String relic_id) {
        ((MainActivity) context).multipleStackNavigator.start(new RelicFragment(relic_id));
    }

    public static class RelicViewHolder extends RecyclerView.ViewHolder {

        public final ConstraintLayout layoutRelic;
        public final TextView textRelicName;
        public final ImageView imageEra, imageVault, imageRarityNeeded;

        public RelicViewHolder(@NonNull @NotNull RecyclerRelicBinding binding) {
            super(binding.getRoot());
            layoutRelic = binding.layoutRelic;
            textRelicName = binding.textRelicName;
            imageEra = binding.imageEra;
            imageVault = binding.imageVault;
            imageRarityNeeded = binding.imageRarityNeeded;
        }
    }
}
