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
            "LEFT JOIN PART_TABLE ON part_id == r_reward_part " +
            "LEFT JOIN PRIME_TABLE ON prime_name == part_prime " +
            "LEFT JOIN USER_PART_TABLE ON user_part_id == part_id " +
            "WHERE r_reward_relic == :id " +
            "ORDER BY r_reward_rarity")
    List<RelicRewardComplete> getRewardsForRelic(String id);
}
