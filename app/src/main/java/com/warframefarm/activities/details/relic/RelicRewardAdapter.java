package com.warframefarm.activities.details.relic;

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
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.part.PartFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.RelicRewardComplete;
import com.warframefarm.databinding.RecyclerRelicRewardBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RelicRewardAdapter extends RecyclerView.Adapter<RelicRewardAdapter.RelicRewardViewHolder> {

    private final Context context;
    private List<RelicRewardComplete> rewards = new ArrayList<>();

    public RelicRewardAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public RelicRewardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerRelicRewardBinding binding = RecyclerRelicRewardBinding.inflate(inflater, parent, false);
        return new RelicRewardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RelicRewardViewHolder holder, int position) {
        RelicRewardComplete reward = rewards.get(position);

        switch (reward.getRarity()) {
            case REWARD_COMMON:
                holder.layoutRelicReward.setBackgroundResource(R.drawable.frame_bg_common);
                holder.textRelicRewardName.setTextColor(context.getColor(R.color.common));
                break;
            case REWARD_UNCOMMON:
                holder.layoutRelicReward.setBackgroundResource(R.drawable.frame_bg_uncommon);
                holder.textRelicRewardName.setTextColor(context.getColor(R.color.uncommon));
                break;
            case REWARD_RARE:
                holder.layoutRelicReward.setBackgroundResource(R.drawable.frame_bg_rare);
                holder.textRelicRewardName.setTextColor(context.getColor(R.color.rare));
                break;
        }

        if (reward.getId() == null) {
            holder.imageRelicReward.setImageResource(R.drawable.forma);
            holder.textRelicRewardName.setText(R.string.forma);
            holder.textRelicRewardNbNeeded.setVisibility(View.GONE);
            holder.imageRelicRewardOwned.setVisibility(View.GONE);
        }
        else {
            holder.layoutRelicReward.setOnClickListener(v -> showPartDetails(reward.getId()));

            if (reward.isBlueprint())
                holder.imageRelicReward.setBackgroundResource(R.drawable.blueprint_bg);
            else
                holder.imageRelicReward.setBackgroundResource(R.color.transparent);

            holder.imageRelicReward.setImageResource(reward.getImage());

            holder.textRelicRewardName.setText(reward.getFullName());

            if (reward.isOwned()) holder.imageRelicRewardOwned.setImageResource(R.drawable.owned);
            else holder.imageRelicRewardOwned.setImageResource(R.drawable.not_owned);

            if (reward.getNeeded() > 1)
                holder.textRelicRewardNbNeeded.setVisibility(View.VISIBLE);
            else
                holder.textRelicRewardNbNeeded.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    private void showPartDetails(String part_id) {
        ((MainActivity) context).multipleStackNavigator.start(new PartFragment(part_id));
    }

    public void setRewards(List<RelicRewardComplete> rewards) {
        this.rewards = rewards;
        notifyDataSetChanged();
    }

    public static class RelicRewardViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layoutRelicReward;
        ImageView imageRelicReward, imageRelicRewardOwned;
        TextView textRelicRewardName, textRelicRewardNbNeeded;

        public RelicRewardViewHolder(@NonNull @NotNull RecyclerRelicRewardBinding binding) {
            super(binding.getRoot());
            layoutRelicReward = binding.layoutRelicReward;
            imageRelicReward = binding.imageRelicReward;
            imageRelicRewardOwned = binding.imageRelicRewardOwned;
            textRelicRewardName = binding.textRelicRewardName;
            textRelicRewardNbNeeded = binding.textRelicRewardNbNeeded;
        }
    }
}
