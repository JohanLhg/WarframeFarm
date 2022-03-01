package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.MISSION_FACTION;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_OBJECTIVE;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_PLANET;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_NORMAL;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = MISSION_TABLE)
public class Mission {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = MISSION_NAME)
    private String name;
    @ColumnInfo(name = MISSION_PLANET)
    private String planet;
    @ColumnInfo(name = MISSION_OBJECTIVE)
    private String objective;
    @ColumnInfo(name = MISSION_FACTION)
    private String faction;
    @ColumnInfo(name = MISSION_TYPE, defaultValue =  "" + TYPE_NORMAL)
    private int type;

    public Mission(@NonNull String name, String planet, String objective, String faction, int type) {
        this.name = name;
        this.planet = planet;
        this.objective = objective;
        this.faction = faction;
        this.type = type;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getPlanet() {
        return planet;
    }

    public String getObjective() {
        return objective;
    }

    public String getFaction() {
        return faction;
    }

    public int getType() {
        return type;
    }
}
