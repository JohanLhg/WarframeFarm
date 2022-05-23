package com.warframefarm.database;

import static com.warframefarm.activities.details.mission.MissionRepository.BOUNTIES;
import static com.warframefarm.activities.details.mission.MissionRepository.CACHES;
import static com.warframefarm.activities.details.mission.MissionRepository.REWARDS;
import static com.warframefarm.data.WarframeLists.FactionImage;
import static com.warframefarm.data.WarframeLists.MissionTypeImage;
import static com.warframefarm.data.WarframeLists.PlanetImage;
import static com.warframefarm.data.WarframeLists.PlanetTopImage;
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
    private String name;
    @ColumnInfo(name = MISSION_PLANET)
    private String planet;
    @ColumnInfo(name = MISSION_OBJECTIVE)
    private String objective;
    @ColumnInfo(name = MISSION_FACTION)
    private String faction;
    @ColumnInfo(name = MISSION_TYPE)
    private int type;
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
    private int imagePlanet = -2, imagePlanetTop = -2, imageFaction = -2, imageType = -2;

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

    public void addMissionReward(MissionReward missionReward) {
        missionRewards.add(missionReward);
    }

    public int getImagePlanet() {
        if (imagePlanet == -2) {
            if (PlanetImage.containsKey(planet))
                imagePlanet = PlanetImage.get(planet);
            else imagePlanet = R.color.transparent;
        }
        return imagePlanet;
    }

    public int getImagePlanetTop() {
        if (imagePlanetTop == -2) {
            if (PlanetTopImage.containsKey(planet))
                imagePlanetTop = PlanetTopImage.get(planet);
            else imagePlanetTop = R.color.transparent;
        }
        return imagePlanetTop;
    }

    public int getImageFaction() {
        if (imageFaction == -2) {
            if (FactionImage.containsKey(faction))
                imageFaction = FactionImage.get(faction);
            else imageFaction = R.color.transparent;
        }
        return imageFaction;
    }

    public int getImageType() {
        if (imageType == -2) {
            if (MissionTypeImage.containsKey(type))
                imageType = MissionTypeImage.get(type);
            else imageType = R.color.transparent;
        }
        return imageType;
    }
}
