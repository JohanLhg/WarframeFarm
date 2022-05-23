package com.warframefarm.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;

@Dao
public interface BountyRewardDao {

    @Insert(onConflict = REPLACE)
    void insert(BountyReward bountyReward);

    @Update
    void update(BountyReward bountyReward);

    @Delete
    void delete(BountyReward bountyReward);

    @RawQuery(observedEntities = {BountyReward.class, RelicReward.class, Relic.class, UserComponent.class})
    List<BountyRewardComplete> getBountyRewards(SimpleSQLiteQuery query);
}
