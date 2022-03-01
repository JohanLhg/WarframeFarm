package com.warframefarm.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SettingDao {

    @Insert(onConflict = REPLACE)
    void insert(Setting setting);

    @Update
    void update(Setting setting);

    @Delete
    void delete(Setting setting);

    @Query("SELECT * FROM SETTINGS_TABLE WHERE 1")
    LiveData<Setting> getSettings();

    @Query("UPDATE SETTINGS_TABLE SET settings_load_limit = :loadLimit WHERE 1")
    void setLoadLimit(int loadLimit);

    @Query("UPDATE SETTINGS_TABLE SET settings_limited = :limited WHERE 1")
    void setLimited(boolean limited);
}
