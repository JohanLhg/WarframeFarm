package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.APP_API_TIMESTAMP;
import static com.warframefarm.database.WarframeFarmDatabase.APP_BUILD;
import static com.warframefarm.database.WarframeFarmDatabase.APP_ID;
import static com.warframefarm.database.WarframeFarmDatabase.APP_TABLE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = APP_TABLE)
public class App {

    @PrimaryKey
    @ColumnInfo(name = APP_ID)
    private final int id;
    @ColumnInfo(name = APP_BUILD)
    private final int build;
    @ColumnInfo(name = APP_API_TIMESTAMP)
    private final int apiTimestamp;

    public App(int id, int build, int apiTimestamp) {
        this.id = id;
        this.build = build;
        this.apiTimestamp = apiTimestamp;
    }

    public int getId() {
        return id;
    }

    public int getBuild() {
        return build;
    }

    public int getApiTimestamp() {
        return apiTimestamp;
    }
}
