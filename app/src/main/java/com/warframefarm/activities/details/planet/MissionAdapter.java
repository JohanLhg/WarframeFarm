package com.warframefarm.activities.details.planet;

import static com.warframefarm.database.WarframeFarmDatabase.TYPE_ARCHWING;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_EMPYREAN;

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
import com.warframefarm.activities.details.mission.MissionFragment;
import com.warframefarm.activities.list.relics.RewardChanceAdapter;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.Mission;
import com.warframefarm.database.MissionReward;
import com.warframefarm.databinding.RecyclerMissionBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.MissionViewHolder> {

    private final Context context;
    private List<Mission> missions = new ArrayList<>();

    public MissionAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MissionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerMissionBinding binding = RecyclerMissionBinding.inflate(inflater, parent, false);
        return new MissionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MissionViewHolder holder, int position) {
        Mission mission = missions.get(position);

        switch (mission.getType()) {
            case TYPE_ARCHWING:
            case TYPE_EMPYREAN:
                holder.imageMissionType.setVisibility(View.VISIBLE);
                holder.imageMissionType.setImageResource(mission.getImageType());
                break;

            default:
                holder.imageMissionType.setVisibility(View.GONE);
                break;
        }

        holder.textMissionName.setText(mission.getName());
        
        holder.textMissionObjective.setText(mission.getObjective());

        holder.imageMissionFaction.setImageResource(mission.getImageFaction());

        List<MissionReward> missionRewards = mission.getMissionRewards();
        if (missionRewards.isEmpty()) {
            holder.recyclerRewardChances.setVisibility(View.GONE);
            holder.textMissionName.setTextColor(context.getColor(R.color.colorBackgroundLight));
            holder.textMissionObjective.setTextColor(context.getColor(R.color.colorBackgroundLight));
        }
        else {
            RewardChanceAdapter adapter = new RewardChanceAdapter(context, mission.getMissionRewards());
            holder.recyclerRewardChances.setAdapter(adapter);
            holder.recyclerRewardChances.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.recyclerRewardChances.setVisibility(View.VISIBLE);
            holder.textMissionName.setTextColor(context.getColor(R.color.colorBackgroundDark));
            holder.textMissionObjective.setTextColor(context.getColor(R.color.colorBackgroundDark));
        }

        holder.layoutMission.setOnClickListener(v ->
                ((MainActivity) context).multipleStackNavigator.start(new MissionFragment(mission.getName()))
        );
    }

    @Override
    public int getItemCount() {
        return missions.size();
    }

    public void updateMissions(List<Mission> missions) {
        this.missions = missions;
        notifyDataSetChanged();
    }

    public static class MissionViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layoutMission;
        TextView textMissionName, textMissionObjective;
        RecyclerView recyclerRewardChances;
        ImageView imageMissionType, imageMissionFaction;

        public MissionViewHolder(@NonNull @NotNull RecyclerMissionBinding binding) {
            super(binding.getRoot());
            layoutMission = binding.layoutMission;
            textMissionName = binding.textMissionName;
            textMissionObjective = binding.textMissionObjective;
            recyclerRewardChances = binding.recyclerRewardChances;
            imageMissionType = binding.imageMissionType;
            imageMissionFaction = binding.imageMissionFaction;
        }
    }
}
