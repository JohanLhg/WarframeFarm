package com.warframefarm.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RelicRewardDao {

    @Insert(onConflict = REPLACE)
    void insert(RelicReward relicReward);

    @Update
    void update(RelicReward relicReward);

    @Delete
    void delete(RelicReward relicReward);

    @Query("DELETE FROM RELIC_REWARD_TABLE")
    void clear();

    @Query("SELECT * " +
            "FROM RELIC_REWARD_TABLE " +
            "LEFT JOIN COMPONENT_TABLE ON component_id == r_reward_component " +
            "LEFT JOIN PRIME_TABLE ON prime_name == component_prime " +
            "LEFT JOIN USER_COMPONENT_TABLE ON user_component_id == component_id " +
            "WHERE r_reward_relic == :id " +
            "ORDER BY r_reward_rarity")
    List<RelicRewardComplete> getRewardsForRelic(String id);
}
