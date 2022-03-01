package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_ID;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_ROTATION;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_TABLE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.DecimalFormat;

@Entity(tableName = M_REWARD_TABLE)
public class MissionReward {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = M_REWARD_ID)
    private int id;
    @ColumnInfo(name = M_REWARD_MISSION)
    private String mission;
    @ColumnInfo(name = M_REWARD_RELIC)
    private String relic;
    @ColumnInfo(name = M_REWARD_ROTATION)
    private String rotation;
    @ColumnInfo(name = M_REWARD_DROP_CHANCE)
    private double dropChance;

    public MissionReward(String mission, String relic, String rotation, double dropChance) {
        this.mission = mission;
        this.relic = relic;
        this.rotation = rotation;
        if (dropChance > 99)
            this.dropChance = 100;
        else this.dropChance = dropChance;
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

    public String getFormattedDropChance() {
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        return numberFormat.format(dropChance) + "%";
    }

    public String getFormattedRotationDropChance() {
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        return rotation + ": " + numberFormat.format(dropChance) + "%";
    }
}
