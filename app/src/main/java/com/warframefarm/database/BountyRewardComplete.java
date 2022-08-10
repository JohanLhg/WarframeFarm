package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_LEVEL;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_ROTATION;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_STAGE;

import androidx.room.ColumnInfo;

import java.text.DecimalFormat;

public class BountyRewardComplete extends RelicComplete implements RewardComplete {

    @ColumnInfo(name = B_REWARD_MISSION)
    private final String mission;
    @ColumnInfo(name = B_REWARD_LEVEL)
    private final String level;
    @ColumnInfo(name = B_REWARD_STAGE)
    private final String stage;
    @ColumnInfo(name = B_REWARD_ROTATION)
    private final String rotation;
    @ColumnInfo(name = B_REWARD_DROP_CHANCE)
    private final double dropChance;

    public BountyRewardComplete(String mission, String level, String stage, String rotation, double dropChance, String id, String era, String name, boolean vaulted, int rarityNeeded) {
        super(id, era, name, vaulted, rarityNeeded);
        this.mission = mission;
        this.level = level;
        this.stage = stage;
        this.rotation = rotation;
        this.dropChance = dropChance;
    }

    public String getMission() {
        return mission;
    }

    public String getLevel() {
        return level;
    }

    public String getStage() {
        return stage;
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
