package com.warframefarm.database;

import static com.warframefarm.activities.details.mission.MissionViewModel.BOUNTIES;
import static com.warframefarm.activities.details.mission.MissionViewModel.CACHES;
import static com.warframefarm.activities.details.mission.MissionViewModel.REWARDS;
import static com.warframefarm.data.WarframeLists.FactionImage;
import static com.warframefarm.data.WarframeLists.MissionTypeImage;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_FACTION;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_OBJECTIVE;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_PLANET;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_TYPE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.warframefarm.R;

import java.util.ArrayList;
import java.util.List;

public class MissionWithRewardTypes {

    @NonNull
    @ColumnInfo(name = MISSION_NAME)
    private final String name;
    @ColumnInfo(name = MISSION_PLANET)
    private final String planet;
    @ColumnInfo(name = MISSION_OBJECTIVE)
    private final String objective;
    @ColumnInfo(name = MISSION_FACTION)
    private final String faction;
    @ColumnInfo(name = MISSION_TYPE)
    private final int type;
    @ColumnInfo(name = "rewards")
    private boolean rewards = false;
    @ColumnInfo(name = "bounties")
    private boolean bounties = false;
    @ColumnInfo(name = "caches")
    private boolean caches = false;

    @Ignore
    private final List<Integer> rewardTypes = new ArrayList<>();
    @Ignore
    private final List<MissionReward> missionRewards = new ArrayList<>();
    @Ignore
    private int imageFaction = -2, imageType = -2;

    public MissionWithRewardTypes(@NonNull String name, String planet, String objective, String faction, int type, boolean rewards, boolean bounties, boolean caches) {
        this.name = name;
        this.planet = planet;
        this.objective = objective;
        this.faction = faction;
        this.type = type;
        this.rewards = rewards;
        this.bounties = bounties;
        this.caches = caches;
        if (rewards) rewardTypes.add(REWARDS);
        if (bounties) rewardTypes.add(BOUNTIES);
        if (caches) rewardTypes.add(CACHES);
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getPlanet() {
        return planet;
    }

    public String getObjective() {
        return objective;
    }

    public String getFaction() {
        return faction;
    }

    public int getType() {
        return type;
    }

    public boolean hasRewards() {
        return rewards;
    }

    public boolean hasBounties() {
        return bounties;
    }

    public boolean hasCaches() {
        return caches;
    }

    public List<Integer> getRewardTypes() {
        return rewardTypes;
    }

    public List<MissionReward> getMissionRewards() {
        return missionRewards;
    }

    public int getImageFaction() {
        if (imageFaction == -2)
            imageFaction = FactionImage.getOrDefault(faction, R.color.transparent);
        return imageFaction;
    }

    public int getImageType() {
        if (imageType == -2)
            imageType = MissionTypeImage.getOrDefault(type, R.color.transparent);
        return imageType;
    }
}
