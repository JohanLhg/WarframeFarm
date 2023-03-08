package com.warframefarm.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AppDao {

    @Insert(onConflict = REPLACE)
    void insert(App app);

    @Update
    void update(App app);

    @Delete
    void delete(App app);

    @Query("UPDATE APP_TABLE " +
            "SET app_build = :build")
    void updateBuild(int build);

    @Query("UPDATE APP_TABLE " +
            "SET app_api_timestamp = :apiTimestamp")
    void updateApiTimestamp(int apiTimestamp);

    @Query("SELECT app_build " +
            "FROM APP_TABLE " +
            "WHERE app_id == 0")
    int getCurrentVersion();

    @Query("SELECT app_api_timestamp " +
            "FROM APP_TABLE " +
            "WHERE app_id == 0")
    int getApiTimestamp();
}
