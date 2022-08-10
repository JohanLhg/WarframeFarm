package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TYPE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = COMPONENT_TABLE)
public class Component {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = COMPONENT_ID)
    private final String id;
    @ColumnInfo(name = COMPONENT_PRIME)
    private final String prime;
    @ColumnInfo(name = COMPONENT_TYPE)
    private final String type;
    @ColumnInfo(name = COMPONENT_NEEDED)
    private int needed;

    public Component(@NonNull String id, String prime, String type, int needed) {
        this.id = id;
        this.prime = prime;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public int getNeeded() {
        return needed;
    }
}
