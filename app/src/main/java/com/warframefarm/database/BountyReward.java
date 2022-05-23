package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_ID;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_LEVEL;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_ROTATION;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_STAGE;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_RELIC;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = B_REWARD_TABLE)
public class BountyReward {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = B_REWARD_ID)
    private int id;
    @ColumnInfo(name = B_REWARD_MISSION)
    private String mission;
    @ColumnInfo(name = B_REWARD_RELIC)
    private String relic;
    @ColumnInfo(name = B_REWARD_LEVEL)
    private String level;
    @ColumnInfo(name = B_REWARD_STAGE)
    private String stage;
    @ColumnInfo(name = B_REWARD_ROTATION)
    private String rotation;
    @ColumnInfo(name = B_REWARD_DROP_CHANCE)
    private double dropChance;

    public BountyReward(String mission, String relic, String level, String stage, String rotation, double dropChance) {
        this.mission = mission;
        this.relic = relic;
        this.level = level;
        this.stage = stage;
        this.rotation = rotation;
        this.dropChance = dropChance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getMission() {
        return mission;
    }

    public String getRelic() {
        return relic;
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
}
