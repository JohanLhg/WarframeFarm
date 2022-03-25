package com.warframefarm.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;

@Dao
public interface RelicDao {

    @Insert(onConflict = REPLACE)
    void insert(Relic relic);

    @Update
    void update(Relic relic);

    @Delete
    void delete(Relic relic);

    @Query("DELETE FROM RELIC_TABLE")
    void clear();

    @Query("UPDATE RELIC_TABLE SET relic_vaulted = :vaulted WHERE relic_id == :id")
    void updateVaultState(String id, boolean vaulted);

    @Query("UPDATE RELIC_TABLE " +
            "SET relic_vaulted = CASE " +
            "WHEN relic_id IN (SELECT DISTINCT m_reward_relic FROM MISSION_REWARD_TABLE) THEN '0'" +
            "ELSE '1'" +
            "END")
    void setVaultStates();

    @Query("SELECT RELIC_TABLE.*, COALESCE(r_reward_rarity, 0) AS r_reward_rarity " +
            "FROM RELIC_TABLE " +
            "LEFT JOIN (" +
                "SELECT r_reward_relic, r_reward_rarity " +
                "FROM RELIC_REWARD_TABLE " +
                "LEFT JOIN USER_COMPONENT_TABLE ON user_component_id == r_reward_component " +
                "WHERE r_reward_relic == :id " +
                "AND user_component_owned == 0" +
            ") ON r_reward_relic == relic_id " +
            "WHERE relic_id == :id")
    RelicComplete getRelic(String id);

    @RawQuery(observedEntities = {Relic.class, RelicReward.class, UserComponent.class})
    LiveData<List<RelicComplete>> getRelics(SimpleSQLiteQuery query);

    @RawQuery(observedEntities = {Relic.class, RelicReward.class, UserComponent.class})
    Cursor getRelicsCursor(SimpleSQLiteQuery query);
}
