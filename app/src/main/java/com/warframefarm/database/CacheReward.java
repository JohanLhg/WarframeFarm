package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_ID;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_ROTATION;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_TABLE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = C_REWARD_TABLE)
public class CacheReward {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = C_REWARD_ID)
    private int id;
    @ColumnInfo(name = C_REWARD_MISSION)
    private String mission;
    @ColumnInfo(name = C_REWARD_RELIC)
    private String relic;
    @ColumnInfo(name = C_REWARD_ROTATION)
    private String rotation;
    @ColumnInfo(name = C_REWARD_DROP_CHANCE)
    private double dropChance;

    public CacheReward(String mission, String relic, String rotation, double dropChance) {
        this.mission = mission;
        this.relic = relic;
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

    public String getRotation() {
        return rotation;
    }

    public double getDropChance() {
        return dropChance;
    }
}
