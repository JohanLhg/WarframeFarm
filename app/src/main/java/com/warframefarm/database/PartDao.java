package com.warframefarm.database;

import static androidx.room.OnConflictStrategy.REPLACE;
import static com.warframefarm.data.WarframeConstants.ARCHWING;
import static com.warframefarm.data.WarframeConstants.MELEE;
import static com.warframefarm.data.WarframeConstants.PET;
import static com.warframefarm.data.WarframeConstants.PRIMARY;
import static com.warframefarm.data.WarframeConstants.SECONDARY;
import static com.warframefarm.data.WarframeConstants.SENTINEL;
import static com.warframefarm.data.WarframeConstants.WARFRAME;

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
public interface PartDao {

    @Insert(onConflict = REPLACE)
    void insert(Part part);

    @Update
    void update(Part part);

    @Delete
    void delete(Part part);

    @Query("SELECT part_id FROM PART_TABLE")
    LiveData<List<String>> getPartIDs();

    @Query("SELECT PART_TABLE.*, prime_type, prime_vaulted, user_part_owned " +
            "FROM PART_TABLE " +
            "LEFT JOIN PRIME_TABLE ON prime_name == part_prime " +
            "LEFT JOIN USER_PART_TABLE ON user_part_id == part_id " +
            "WHERE user_part_owned == 0 " +
            "ORDER BY prime_type == '" + MELEE + "', " +
            "prime_type == '" + SECONDARY + "', " +
            "prime_type == '" + PRIMARY + "', " +
            "prime_type == '" + SENTINEL + "', " +
            "prime_type == '" + PET + "', " +
            "prime_type == '" + ARCHWING + "', " +
            "prime_type == '" + WARFRAME + "', " +
            "part_id")
    List<PartComplete> getNeededParts();

    @Query("SELECT PART_TABLE.*, prime_type, prime_vaulted, user_part_owned " +
            "FROM PART_TABLE " +
            "LEFT JOIN PRIME_TABLE ON prime_name == part_prime " +
            "LEFT JOIN USER_PART_TABLE ON user_part_id == part_id " +
            "WHERE part_prime == :prime " +
            "ORDER BY part_id")
    List<PartComplete> getPartsOfPrime(String prime);

    @Query("SELECT PART_TABLE.*, prime_type, prime_vaulted, user_part_owned " +
            "FROM PART_TABLE " +
            "LEFT JOIN PRIME_TABLE ON prime_name == part_prime " +
            "LEFT JOIN USER_PART_TABLE ON user_part_id == part_id " +
            "WHERE part_prime == :prime " +
            "ORDER BY part_id")
    LiveData<List<PartComplete>> getPartsOfPrimeLD(String prime);

    @Query("SELECT PART_TABLE.*, prime_type, prime_vaulted, user_part_owned " +
            "FROM PART_TABLE " +
            "LEFT JOIN PRIME_TABLE ON prime_name == part_prime " +
            "LEFT JOIN USER_PART_TABLE ON user_part_id == part_id " +
            "WHERE part_id == :id " +
            "ORDER BY part_id")
    LiveData<PartComplete> getPart(String id);

    @RawQuery(observedEntities = {Part.class, UserPart.class})
    LiveData<List<PartComplete>> getPartsLiveData(SimpleSQLiteQuery query);

    @RawQuery(observedEntities = {Part.class, UserPart.class})
    List<PartComplete> getParts(SimpleSQLiteQuery query);
}
