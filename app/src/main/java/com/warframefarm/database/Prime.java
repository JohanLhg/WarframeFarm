package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = PRIME_TABLE)
public class Prime {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = PRIME_NAME)
    private final String name;
    @ColumnInfo(name = PRIME_TYPE)
    private final String type;
    @ColumnInfo(name = PRIME_VAULTED, defaultValue = "0")
    private boolean vaulted;

    public Prime(@NonNull String name, String type) {
        this.name = name;
        this.type = type;
    }

    public void setVaulted(boolean vaulted) {
        this.vaulted = vaulted;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isVaulted() {
        return vaulted;
    }
}
