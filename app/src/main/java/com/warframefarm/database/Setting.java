package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.SETTINGS_ID;
import static com.warframefarm.database.WarframeFarmDatabase.SETTINGS_LIMITED;
import static com.warframefarm.database.WarframeFarmDatabase.SETTINGS_LOAD_LIMIT;
import static com.warframefarm.database.WarframeFarmDatabase.SETTINGS_TABLE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = SETTINGS_TABLE)
public class Setting {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SETTINGS_ID)
    private int id;
    @ColumnInfo(name = SETTINGS_LOAD_LIMIT)
    private int loadLimit;
    @ColumnInfo(name = SETTINGS_LIMITED)
    private boolean limited;

    public Setting(int loadLimit, boolean limited) {
        this.loadLimit = loadLimit;
        this.limited = limited;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getLoadLimit() {
        return loadLimit;
    }

    public boolean isLimited() {
        return limited;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "id=" + id +
                ", loadLimit=" + loadLimit +
                ", limited=" + limited +
                '}';
    }
}
