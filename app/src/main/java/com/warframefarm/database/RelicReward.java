package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_ID;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RARITY;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_TABLE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = R_REWARD_TABLE)
public class RelicReward {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = R_REWARD_ID)
    private int id;
    @ColumnInfo(name = R_REWARD_RELIC)
    private String relic;
    @ColumnInfo(name = R_REWARD_COMPONENT)
    private String component;
    @ColumnInfo(name = R_REWARD_RARITY)
    private int rarity;

    public RelicReward(String relic, String component, int rarity) {
        this.relic = relic;
        this.component = component;
        this.rarity = rarity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getRelic() {
        return relic;
    }

    public String getComponent() {
        return component;
    }

    public int getRarity() {
        return rarity;
    }
}
