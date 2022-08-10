package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ERA;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_VAULTED;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = RELIC_TABLE)
public class Relic {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = RELIC_ID)
    private final String id;
    @ColumnInfo(name = RELIC_ERA)
    private final String era;
    @ColumnInfo(name = RELIC_NAME)
    private final String name;
    @ColumnInfo(name = RELIC_VAULTED, defaultValue = "1")
    private boolean vaulted;

    public Relic(@NonNull String id, String era, String name) {
        this.id = id;
        this.era = era;
        this.name = name;
    }

    public void setVaulted(boolean vaulted) {
        this.vaulted = vaulted;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getEra() {
        return era;
    }

    public String getName() {
        return name;
    }

    public boolean isVaulted() {
        return vaulted;
    }
}
