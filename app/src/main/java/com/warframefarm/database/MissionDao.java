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

    //@Query("SELECT * FROM MISSION_TABLE WHERE mission_name == :name")
    //Mission getMission(String name);

    @Query("SELECT MISSION_TABLE.*, " +
            "CASE " +
                "WHEN (" +
                    "SELECT COUNT(m_reward_relic) " +
                    "FROM MISSION_REWARD_TABLE " +
                    "WHERE m_reward_mission == :name" +
                ") then 1 " +
                "ELSE 0 " +
            "END AS rewards, " +
            "CASE " +
                "WHEN (" +
                    "SELECT COUNT(b_reward_relic) " +
                    "FROM BOUNTY_REWARD_TABLE " +
                    "WHERE b_reward_mission == :name" +
                ") then 1 " +
                "ELSE 0 " +
            "END AS bounties, " +
            "CASE " +
                "WHEN (" +
                    "SELECT COUNT(c_reward_relic) " +
                    "FROM CACHE_REWARD_TABLE " +
                    "WHERE c_reward_mission == :name" +
                ") then 1 " +
                "ELSE 0 " +
            "END AS caches " +
            "FROM MISSION_TABLE " +
            "WHERE mission_name == :name")
    MissionWithRewardTypes getMission(String name);

    @RawQuery(observedEntities = {Mission.class, MissionReward.class, Relic.class, RelicReward.class})
    Cursor getMissions(SimpleSQLiteQuery query);

    @Query("SELECT DISTINCT mission_type FROM MISSION_TABLE WHERE mission_planet == :name")
    List<Integer> getPlanetMissionTypes(String name);
}
