package com.warframefarm.activities.details;

import static com.warframefarm.database.WarframeFarmDatabase.REWARD_COMMON;
import static com.warframefarm.database.WarframeFarmDatabase.REWARD_RARE;
import static com.warframefarm.database.WarframeFarmDatabase.REWARD_UNCOMMON;

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
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.RelicRewardComplete;
import com.warframefarm.databinding.RecyclerTinyRelicRewardBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TinyRelicRewardAdapter extends RecyclerView.Adapter<TinyRelicRewardAdapter.RelicRewardViewHolder> {

    private final Context context;
    private final List<RelicRewardComplete> rewards;

    public TinyRelicRewardAdapter(Context context, List<RelicRewardComplete> rewards) {
        this.context = context;
        this.rewards = rewards;
    }

    @NonNull
    @NotNull
    @Override
    public RelicRewardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerTinyRelicRewardBinding binding = RecyclerTinyRelicRewardBinding.inflate(inflater, parent, false);
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
        }
        else {
            holder.layoutRelicReward.setOnClickListener(v -> showComponentDetails(reward.getId()));

            holder.imageRelicReward.setImageResource(reward.getImage());

            if (reward.isBlueprint())
                holder.imageRelicReward.setBackgroundResource(R.drawable.blueprint_bg);
            else
                holder.imageRelicReward.setBackgroundResource(R.color.transparent);

            holder.textRelicRewardName.setText(reward.getFullName());
        }
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    private void showComponentDetails(String component_id) {
        ((MainActivity) context).multipleStackNavigator.start(new ComponentFragment(component_id));
    }

    public static class RelicRewardViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layoutRelicReward;
        ImageView imageRelicReward;
        TextView textRelicRewardName;

        public RelicRewardViewHolder(@NonNull @NotNull RecyclerTinyRelicRewardBinding binding) {
            super(binding.getRoot());
            layoutRelicReward = binding.layoutRelicReward;
            imageRelicReward = binding.imageRelicReward;
            textRelicRewardName = binding.textRelicRewardName;
        }
    }
}
