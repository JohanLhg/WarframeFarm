package com.warframefarm.activities.details.mission;

import static com.warframefarm.database.WarframeFarmDatabase.TYPE_EMPYREAN;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_NORMAL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.CacheRewardComplete;
import com.warframefarm.databinding.RecyclerMissionRewardCategoryBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CacheRotationAdapter extends RecyclerView.Adapter<MissionRewardCategoryViewHolder> {

    private final Context context;
    private int type = TYPE_EMPYREAN;
    private final ArrayList<String> rotations = new ArrayList<>();
    private HashMap<String, List<CacheRewardComplete>> cacheRotations = new HashMap<>();

    public CacheRotationAdapter(Context context) {
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
        List<CacheRewardComplete> rewards = cacheRotations.get(rotation);

        switch (type) {
            case TYPE_NORMAL:
                switch (rotation) {
                    case "A" :
                        holder.textCategory.setText(R.string.title_first_cache);
                        holder.textCategory.setVisibility(View.VISIBLE);
                        break;
                    case "B" :
                        holder.textCategory.setText(R.string.title_second_cache);
                        holder.textCategory.setVisibility(View.VISIBLE);
                        break;
                    case "C" :
                        holder.textCategory.setText(R.string.title_third_cache);
                        holder.textCategory.setVisibility(View.VISIBLE);
                        break;
                    default:
                        holder.textCategory.setVisibility(View.GONE);
                        break;
                }
                break;

            case TYPE_EMPYREAN:
                switch (rotation) {
                    case "A":
                        holder.textCategory.setText(R.string.title_points_of_interest);
                        holder.textCategory.setVisibility(View.VISIBLE);
                        break;
                    case "B":
                        holder.textCategory.setText(R.string.title_abandoned_derelict);
                        holder.textCategory.setVisibility(View.VISIBLE);
                        break;
                    default:
                        holder.textCategory.setVisibility(View.GONE);
                        break;
                }
                break;
        }

        MissionRewardAdapter adapter = new MissionRewardAdapter(context, new ArrayList<>(rewards));
        holder.recyclerMissionRewards.setAdapter(adapter);
        holder.recyclerMissionRewards.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return rotations.size();
    }

    public void setType(int type) {
        this.type = type;
        notifyDataSetChanged();
    }

    public void updateCacheRotations(List<CacheRewardComplete> rewards) {
        rotations.clear();
        this.cacheRotations.clear();

        HashMap<String, List<CacheRewardComplete>> cacheRotations = new HashMap<>();
        List<CacheRewardComplete> rotationRewards = new ArrayList<>();
        String prev_rotation = "", rotation = "";
        for (CacheRewardComplete reward : rewards) {
            rotation = reward.getRotation();
            if (!rotation.equals(prev_rotation)) {
                if (!prev_rotation.equals("")) {
                    rotations.add(prev_rotation);
                    cacheRotations.put(prev_rotation, rotationRewards);
                }

                prev_rotation = rotation;
                rotationRewards = new ArrayList<>();
            }
            rotationRewards.add(reward);
        }
        if (!rotationRewards.isEmpty()) {
            rotations.add(rotation);
            cacheRotations.put(rotation, rotationRewards);
        }

        this.cacheRotations = cacheRotations;
        notifyDataSetChanged();
    }
}
