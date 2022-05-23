package com.warframefarm.activities.list.relics;

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

import com.warframefarm.activities.details.mission.MissionFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.Mission;
import com.warframefarm.databinding.RecyclerPlanetMissionFarmBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlanetMissionFarmAdapter extends RecyclerView.Adapter<PlanetMissionFarmAdapter.MissionFarmViewHolder> {

    private final Context context;
    private List<Mission> missions = new ArrayList<>();

    public PlanetMissionFarmAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MissionFarmViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerPlanetMissionFarmBinding binding = RecyclerPlanetMissionFarmBinding.inflate(inflater, parent, false);
        return new MissionFarmViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MissionFarmViewHolder holder, int position) {
        Mission mission = missions.get(position);

        holder.imagePlanet.setImageResource(mission.getImagePlanetTop());

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

        holder.textPlanetName.setText(mission.getPlanet());

        holder.textMissionName.setText(mission.getName());

        holder.textMissionObjective.setText(mission.getObjective());

        RewardChanceAdapter adapter = new RewardChanceAdapter(context, mission.getMissionRewards());
        holder.recyclerRewardChances.setAdapter(adapter);
        holder.recyclerRewardChances.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

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

    public static class MissionFarmViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layoutMission;
        ImageView imagePlanet, imageMissionType;
        TextView textPlanetName, textMissionName, textMissionObjective;
        RecyclerView recyclerRewardChances;

        public MissionFarmViewHolder(@NonNull @NotNull RecyclerPlanetMissionFarmBinding binding) {
            super(binding.getRoot());
            layoutMission = binding.layoutMission;
            imagePlanet = binding.imagePlanet;
            imageMissionType = binding.imageMissionType;
            textPlanetName = binding.textPlanetName;
            textMissionName = binding.textMissionName;
            textMissionObjective = binding.textMissionObjective;
            recyclerRewardChances = binding.recyclerRewardChances;
        }
    }
}
