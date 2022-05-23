package com.warframefarm.activities.details.mission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.BountyRewardComplete;
import com.warframefarm.databinding.RecyclerMissionRewardCategoryBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BountyCategoryAdapter extends RecyclerView.Adapter<MissionRewardCategoryViewHolder> {

    private final Context context;
    private final int categoryType;
    private final ArrayList<String> categories = new ArrayList<>();
    private HashMap<String, List<BountyRewardComplete>> bountyCategories = new HashMap<>();

    public static final int LEVELS = 0, STAGES = 1, ROTATIONS = 2;

    public BountyCategoryAdapter(Context context, int categoryType) {
        this.context = context;
        this.categoryType = categoryType;
    }

    public BountyCategoryAdapter(Context context, ArrayList<BountyRewardComplete> rewards, int categoryType) {
        this.context = context;
        this.categoryType = categoryType;
        updateRewards(rewards);
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
        String category = categories.get(position);
        List<BountyRewardComplete> rewards = bountyCategories.get(category);

        RecyclerView.Adapter adapter;

        switch (categoryType) {
            case LEVELS:
                holder.textCategory.setText(category);
                holder.textCategory.setVisibility(View.VISIBLE);
                adapter = new BountyCategoryAdapter(context, new ArrayList<>(rewards), STAGES);
                break;

            case STAGES:
                holder.textCategory.setText(category);
                holder.textCategory.setVisibility(View.VISIBLE);
                adapter = new BountyCategoryAdapter(context, new ArrayList<>(rewards), ROTATIONS);
                break;

            case ROTATIONS:
                holder.textCategory.setText(context.getString(R.string.title_rotation, category));
                holder.textCategory.setVisibility(View.VISIBLE);
                adapter = new MissionRewardAdapter(context, new ArrayList<>(rewards));
                break;

            default:
                return;
        }

        holder.recyclerMissionRewards.setAdapter(adapter);
        holder.recyclerMissionRewards.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateRewards(List<BountyRewardComplete> rewards) {
        categories.clear();
        this.bountyCategories.clear();

        HashMap<String, List<BountyRewardComplete>> bountyCategories = new HashMap<>();
        List<BountyRewardComplete> categoryRewards = new ArrayList<>();
        String prev_category = "", category = "";
        for (BountyRewardComplete reward : rewards) {
            switch (categoryType) {
                case LEVELS:
                    category = reward.getLevel();
                    break;
                case STAGES:
                    category = reward.getStage();
                    break;
                case ROTATIONS:
                    category = reward.getRotation();
                    break;
                default:
                    return;
            }
            if (!category.equals(prev_category)) {
                if (!prev_category.equals("")) {
                    categories.add(prev_category);
                    bountyCategories.put(prev_category, categoryRewards);
                }

                prev_category = category;
                categoryRewards = new ArrayList<>();
            }
            categoryRewards.add(reward);
        }
        if (!categoryRewards.isEmpty()) {
            categories.add(category);
            bountyCategories.put(category, categoryRewards);
        }

        this.bountyCategories = bountyCategories;
        notifyDataSetChanged();
    }
}
