package com.warframefarm.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;

@Dao
public interface CacheRewardDao {

    @Insert(onConflict = REPLACE)
    void insert(CacheReward cacheReward);

    @Update
    void update(CacheReward cacheReward);

    @Delete
    void delete(CacheReward cacheReward);

    @Query("DELETE FROM CACHE_REWARD_TABLE")
    void clear();

    @RawQuery(observedEntities = {CacheReward.class, RelicReward.class, Relic.class, UserComponent.class})
    List<CacheRewardComplete> getCacheRewards(SimpleSQLiteQuery query);
}
