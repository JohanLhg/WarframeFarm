package com.warframefarm.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;

@Dao
public interface MissionDao {

    @Insert(onConflict = REPLACE)
    void insert(Mission mission);

    @Update
    void update(Mission mission);

    @Delete
    void delete(Mission mission);

    @Query("SELECT mission_name FROM MISSION_TABLE WHERE mission_planet == :planet")
    List<String> getPlanetMissions(String planet);

    @Query("SELECT * FROM MISSION_TABLE WHERE mission_name == :name")
    MissionComplete getMission(String name);

    @RawQuery(observedEntities = {Mission.class, MissionReward.class, Relic.class, RelicReward.class})
    Cursor getMissions(SimpleSQLiteQuery query);
}
