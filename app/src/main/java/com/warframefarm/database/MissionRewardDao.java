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
public interface MissionRewardDao {

    @Insert(onConflict = REPLACE)
    void insert(MissionReward missionReward);

    @Update
    void update(MissionReward missionReward);

    @Delete
    void delete(MissionReward missionReward);

    @RawQuery(observedEntities = {MissionReward.class, RelicReward.class, Relic.class, UserComponent.class})
    List<MissionRewardComplete> getMissionRewards(SimpleSQLiteQuery query);
}
