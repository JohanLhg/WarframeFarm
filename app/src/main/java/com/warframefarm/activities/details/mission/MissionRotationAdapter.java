package com.warframefarm.activities.details.mission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.MissionRewardComplete;
import com.warframefarm.databinding.RecyclerMissionRewardCategoryBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MissionRotationAdapter extends RecyclerView.Adapter<MissionRewardCategoryViewHolder> {

    private final Context context;
    private final ArrayList<String> rotations = new ArrayList<>();
    private HashMap<String, List<MissionRewardComplete>> missionRotations = new HashMap<>();

    public MissionRotationAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MissionRewardCategoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        RecyclerMissionRewardCategoryBinding binding =
                RecyclerMissionRewardCategoryBinding.inflate(
                        LayoutInflater.from(context), parent, false);
        return new MissionRewardCategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MissionRewardCategoryViewHolder holder, int position) {
        String rotation = rotations.get(position);
        List<MissionRewardComplete> rewards = missionRotations.get(rotation);

        if (rotation.equals("Z")) holder.textCategory.setVisibility(View.GONE);
        else {
            holder.textCategory.setText(context.getString(R.string.title_rotation, rotation));
            holder.textCategory.setVisibility(View.VISIBLE);
        }

        MissionRewardAdapter adapter = new MissionRewardAdapter(context, new ArrayList<>(rewards));
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
}

