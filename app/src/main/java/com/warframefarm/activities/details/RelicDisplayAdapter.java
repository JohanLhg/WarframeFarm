package com.warframefarm.activities.details;

import static com.warframefarm.database.WarframeFarmDatabase.REWARD_COMMON;
import static com.warframefarm.database.WarframeFarmDatabase.REWARD_RARE;
import static com.warframefarm.database.WarframeFarmDatabase.REWARD_UNCOMMON;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.relic.RelicFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.RelicRewardComplete;
import com.warframefarm.databinding.RecyclerRelicDisplayBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RelicDisplayAdapter extends RecyclerView.Adapter<RelicDisplayAdapter.RelicDisplayViewHolder> {

    private final Context context;
    private List<RelicComplete> relics = new ArrayList<>();

    public RelicDisplayAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public RelicDisplayViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerRelicDisplayBinding binding = RecyclerRelicDisplayBinding.inflate(inflater, parent, false);
        return new RelicDisplayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RelicDisplayAdapter.RelicDisplayViewHolder holder, int position) {
        RelicComplete relic = relics.get(position);

        holder.imageEra.setImageResource(relic.getImage());

        holder.textRelic.setText(relic.getShortName());

        List<RelicRewardComplete> wantedRewards = relic.getNeededRewards();
        int rarity = relic.getRarityNeeded();

        if (!wantedRewards.isEmpty()) {
            TinyRelicRewardAdapter rewardAdapter = new TinyRelicRewardAdapter(context, wantedRewards);
            holder.recyclerRewards.setAdapter(rewardAdapter);
            holder.recyclerRewards.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true));
            holder.recyclerRewards.setVisibility(View.VISIBLE);
        }
        else
            holder.recyclerRewards.setVisibility(View.GONE);

        switch (rarity) {
            case REWARD_COMMON:
                holder.imageRarity.setImageResource(R.drawable.reward_common);
                break;
            case REWARD_UNCOMMON:
                holder.imageRarity.setImageResource(R.drawable.reward_uncommon);
                break;
            case REWARD_RARE:
                holder.imageRarity.setImageResource(R.drawable.reward_rare);
                break;
        }

        if (relic.isVaulted())
            holder.imageVault.setVisibility(View.VISIBLE);
        else
            holder.imageVault.setVisibility(View.GONE);

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

    public static class RelicDisplayViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layoutRelic;
        TextView textRelic;
        ImageView imageEra, imageVault, imageRarity;
        RecyclerView recyclerRewards;

        public RelicDisplayViewHolder(@NonNull @NotNull RecyclerRelicDisplayBinding binding) {
            super(binding.getRoot());
            layoutRelic = binding.layoutRelicDisplay;
            textRelic = binding.textRelic;
            imageEra = binding.imageEra;
            imageVault = binding.imageVault;
            imageRarity = binding.imageRarity;
            recyclerRewards = binding.recyclerRewards;
        }
    }
}
