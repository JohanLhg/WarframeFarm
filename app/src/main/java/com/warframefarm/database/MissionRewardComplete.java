package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_ROTATION;

import androidx.room.ColumnInfo;

import java.text.DecimalFormat;

public class MissionRewardComplete extends RelicComplete implements RewardComplete {

    @ColumnInfo(name = M_REWARD_MISSION)
    private String mission;
    @ColumnInfo(name = M_REWARD_ROTATION)
    private String rotation;
    @ColumnInfo(name = M_REWARD_DROP_CHANCE)
    private double dropChance;

    public MissionRewardComplete(String mission, String rotation, double dropChance, String id, String era, String name, boolean vaulted, int rarityNeeded) {
        super(id, era, name, vaulted, rarityNeeded);
        this.mission = mission;
        this.rotation = rotation;
        if (dropChance > 99)
            this.dropChance = 100;
        else this.dropChance = dropChance;
    }

    public String getMission() {
        return mission;
    }

    public String getRotation() {
        return rotation;
    }

    public double getDropChance() {
        return dropChance;
    }

    public String getFormattedDropChance() {
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        return numberFormat.format(dropChance) + "%";
    }

    @Override
    public String toString() {
        return "MissionRewardComplete{" +
                "mission='" + mission + '\'' +
                ", rotation='" + rotation + '\'' +
                ", dropChance=" + dropChance +
                '}';
    }
}
