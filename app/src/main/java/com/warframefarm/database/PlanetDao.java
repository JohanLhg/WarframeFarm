package com.warframefarm.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface PlanetDao {

    @Insert(onConflict = REPLACE)
    void insert(Planet planet);

    @Update
    void update(Planet planet);

    @Delete
    void delete(Planet planet);

    @Query("SELECT * FROM PLANET_TABLE WHERE planet_name == :name")
    Planet getPlanet(String name);

    @Query("SELECT planet_name FROM PLANET_TABLE")
    List<String> getPlanetNames();

    @RawQuery(observedEntities = {Planet.class, Mission.class, MissionReward.class, RelicReward.class})
    List<Planet> getPlanets(SupportSQLiteQuery query);
}
