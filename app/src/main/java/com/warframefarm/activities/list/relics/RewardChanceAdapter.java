package com.warframefarm.activities.list.relics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.MissionReward;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RewardChanceAdapter extends RecyclerView.Adapter<RewardChanceAdapter.RewardChanceViewHolder> {

    private final Context context;
    private List<MissionReward> rewards;

    public RewardChanceAdapter(Context context, List<MissionReward> rewards) {
        this.context = context;
        this.rewards = rewards;
    }

    @NonNull
    @NotNull
    @Override
    public RewardChanceViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_reward_chance, parent, false);
        return new RewardChanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RewardChanceViewHolder holder, int position) {
        MissionReward reward = rewards.get(position);

        String rotation = reward.getRotation();
        if (rotation.equals("Z"))
            holder.textRewardChance.setText(reward.getFormattedDropChance());
        else
            holder.textRewardChance.setText(reward.getFormattedRotationDropChance());
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    public void updateMissions(ArrayList<MissionReward> rewards) {
        this.rewards = rewards;
        notifyDataSetChanged();
    }

    public static class RewardChanceViewHolder extends RecyclerView.ViewHolder {

        TextView textRewardChance;

        public RewardChanceViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textRewardChance = itemView.findViewById(R.id.textRewardChance);
        }
    }
}
