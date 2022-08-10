package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_ROTATION;

import androidx.room.ColumnInfo;

import java.text.DecimalFormat;

public class CacheRewardComplete extends RelicComplete implements RewardComplete {

    @ColumnInfo(name = C_REWARD_MISSION)
    private final String mission;
    @ColumnInfo(name = C_REWARD_ROTATION)
    private final String rotation;
    @ColumnInfo(name = C_REWARD_DROP_CHANCE)
    private final double dropChance;

    public CacheRewardComplete(String mission, String rotation, double dropChance, String id, String era, String name, boolean vaulted, int rarityNeeded) {
        super(id, era, name, vaulted, rarityNeeded);
        this.mission = mission;
        this.rotation = rotation;
        this.dropChance = dropChance;
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
}
