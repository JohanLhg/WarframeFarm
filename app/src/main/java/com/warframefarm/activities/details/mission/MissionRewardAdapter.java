package com.warframefarm.activities.details.mission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.activities.details.relic.RelicFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.RewardComplete;
import com.warframefarm.databinding.RecyclerMissionRewardBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MissionRewardAdapter extends RecyclerView.Adapter<MissionRewardAdapter.MissionRewardViewHolder> {

    private final Context context;
    private List<RewardComplete> missionRewards;

    public MissionRewardAdapter(Context context, List<RewardComplete> missionRewards) {
        this.context = context;
        this.missionRewards = missionRewards;
    }

    @NonNull
    @NotNull
    @Override
    public MissionRewardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerMissionRewardBinding binding =
                RecyclerMissionRewardBinding.inflate(inflater, parent, false);
        return new MissionRewardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MissionRewardViewHolder holder, int position) {
        RewardComplete missionReward = missionRewards.get(position);

        holder.imageItem.setImageResource(missionReward.getImage());

        holder.textItemName.setText(missionReward.getFullName());

        int imageRarity = missionReward.getImageRarity();
        if (imageRarity == -1)
            holder.imageRarityNeeded.setVisibility(View.GONE);
        else {
            holder.imageRarityNeeded.setImageResource(imageRarity);
            holder.imageRarityNeeded.setVisibility(View.VISIBLE);
        }

        holder.textItemChance.setText(missionReward.getFormattedDropChance());

        holder.layoutRelic.setOnClickListener(v -> showRelicDetails(missionReward.getId()));
    }

    @Override
    public int getItemCount() {
        if (missionRewards == null)
            return 0;
        else
            return missionRewards.size();
    }

    public void updateMissionRewards(List<RewardComplete> missionRewards) {
        this.missionRewards = missionRewards;
        notifyDataSetChanged();
    }

    private void showRelicDetails(String relic_id) {
        ((MainActivity) context).multipleStackNavigator.start(new RelicFragment(relic_id));
    }

    public static class MissionRewardViewHolder extends RecyclerView.ViewHolder {

        public final LinearLayout layoutRelic;
        public final ImageView imageItem, imageRarityNeeded;
        public final TextView textItemName, textItemChance;

        public MissionRewardViewHolder(@NonNull @NotNull RecyclerMissionRewardBinding binding) {
            super(binding.getRoot());
            layoutRelic = binding.layoutRelic;
            imageItem = binding.imageItem;
            imageRarityNeeded = binding.imageRarityNeeded;
            textItemName = binding.textItemName;
            textItemChance = binding.textItemChance;
        }
    }
}
