package com.warframefarm.activities.details.mission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.MissionRewardComplete;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MissionRotationAdapter extends RecyclerView.Adapter<MissionRotationAdapter.MissionRotationViewHolder> {

    private final Context context;
    private final ArrayList<String> rotations = new ArrayList<>();
    private HashMap<String, List<MissionRewardComplete>> missionRotations = new HashMap<>();

    public MissionRotationAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MissionRotationViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_mission_rotation_rewards, parent, false);
        return new MissionRotationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MissionRotationViewHolder holder, int position) {
        String rotation = rotations.get(position);
        List<MissionRewardComplete> rewards = missionRotations.get(rotation);

        if (rotation.equals("Z")) holder.textRotation.setVisibility(View.GONE);
        else {
            holder.textRotation.setText(context.getString(R.string.title_rotation, rotation));
            holder.textRotation.setVisibility(View.VISIBLE);
        }

        MissionRewardAdapter adapter = new MissionRewardAdapter(context, rewards);
        holder.recyclerMissionRewards.setAdapter(adapter);
        holder.recyclerMissionRewards.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return rotations.size();
    }

    public void updateMissionRotations(List<MissionRewardComplete> rewards) {
        rotations.clear();
        this.missionRotations.clear();

        HashMap<String, List<MissionRewardComplete>> missionRotations = new HashMap<>();
        List<MissionRewardComplete> rotationRewards = new ArrayList<>();
        String prev_rotation = "", rotation = "";
        for (MissionRewardComplete reward : rewards) {
            rotation = reward.getRotation();
            if (!rotation.equals(prev_rotation)) {
                if (!prev_rotation.equals("")) {
                    rotations.add(prev_rotation);
                    missionRotations.put(prev_rotation, rotationRewards);
                }

                prev_rotation = rotation;
                rotationRewards = new ArrayList<>();
            }
            rotationRewards.add(reward);
        }
        if (!rotationRewards.isEmpty()) {
            rotations.add(rotation);
            missionRotations.put(rotation, rotationRewards);
        }

        this.missionRotations = missionRotations;
        notifyDataSetChanged();
    }

    public static class MissionRotationViewHolder extends RecyclerView.ViewHolder {

        TextView textRotation;
        RecyclerView recyclerMissionRewards;

        public MissionRotationViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textRotation = itemView.findViewById(R.id.textRotation);
            recyclerMissionRewards = itemView.findViewById(R.id.recyclerMissionRewards);
        }
    }
}
