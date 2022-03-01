package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.PART_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.PART_ID;
import static com.warframefarm.database.WarframeFarmDatabase.PART_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.PART_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.PART_TABLE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = PART_TABLE)
public class Part {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = PART_ID)
    private String id;
    @ColumnInfo(name = PART_PRIME)
    private String prime;
    @ColumnInfo(name = PART_COMPONENT)
    private String component;
    @ColumnInfo(name = PART_NEEDED)
    private int needed;

    public Part(@NonNull String id, String prime, String component, int needed) {
        this.id = id;
        this.prime = prime;
        this.component = component;
        this.needed = needed;
    }

    public void setNeeded(int needed) {
        this.needed = needed;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getPrime() {
        return prime;
    }

    public String getComponent() {
        return component;
    }

    public int getNeeded() {
        return needed;
    }
}
