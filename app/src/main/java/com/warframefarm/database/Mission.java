package com.warframefarm.database;

import static com.warframefarm.data.WarframeLists.FactionImage;
import static com.warframefarm.data.WarframeLists.MissionTypeImage;
import static com.warframefarm.data.WarframeLists.PlanetImage;
import static com.warframefarm.data.WarframeLists.PlanetTopImage;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_FACTION;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_OBJECTIVE;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_PLANET;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_NORMAL;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.warframefarm.R;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = MISSION_TABLE)
public class Mission {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = MISSION_NAME)
    private String name;
    @ColumnInfo(name = MISSION_PLANET)
    private String planet;
    @ColumnInfo(name = MISSION_OBJECTIVE)
    private String objective;
    @ColumnInfo(name = MISSION_FACTION)
    private String faction;
    @ColumnInfo(name = MISSION_TYPE, defaultValue =  "" + TYPE_NORMAL)
    private int type;

    @Ignore
    private final List<MissionReward> missionRewards = new ArrayList<>();

    @Ignore
    private int imagePlanet = -2, imagePlanetTop = -2, imageFaction = -2, imageType = -2;

    public Mission(@NonNull String name, String planet, String objective, String faction, int type) {
        this.name = name;
        this.planet = planet;
        this.objective = objective;
        this.faction = faction;
        this.type = type;
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

    @Override
    public String toString() {
        return "Mission{" +
                "name='" + name + '\'' +
                ", planet='" + planet + '\'' +
                ", objective='" + objective + '\'' +
                ", faction='" + faction + '\'' +
                ", type=" + type +
                '}';
    }
}
