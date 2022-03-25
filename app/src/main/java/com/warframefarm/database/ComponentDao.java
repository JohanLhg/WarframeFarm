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
public interface ComponentDao {

    @Insert(onConflict = REPLACE)
    void insert(Component component);

    @Update
    void update(Component component);

    @Delete
    void delete(Component component);

    @Query("SELECT component_id FROM COMPONENT_TABLE")
    LiveData<List<String>> getComponentIDs();

    @Query("SELECT COMPONENT_TABLE.*, prime_type, prime_vaulted, user_component_owned " +
            "FROM COMPONENT_TABLE " +
            "LEFT JOIN PRIME_TABLE ON prime_name == component_prime " +
            "LEFT JOIN USER_COMPONENT_TABLE ON user_component_id == component_id " +
            "WHERE user_component_owned == 0 " +
            "ORDER BY prime_type == '" + MELEE + "', " +
            "prime_type == '" + SECONDARY + "', " +
            "prime_type == '" + PRIMARY + "', " +
            "prime_type == '" + SENTINEL + "', " +
            "prime_type == '" + PET + "', " +
            "prime_type == '" + ARCHWING + "', " +
            "prime_type == '" + WARFRAME + "', " +
            "component_id")
    List<ComponentComplete> getNeededComponents();

    @Query("SELECT COMPONENT_TABLE.*, prime_type, prime_vaulted, user_component_owned " +
            "FROM COMPONENT_TABLE " +
            "LEFT JOIN PRIME_TABLE ON prime_name == component_prime " +
            "LEFT JOIN USER_COMPONENT_TABLE ON user_component_id == component_id " +
            "WHERE component_prime == :prime " +
            "ORDER BY component_id")
    List<ComponentComplete> getComponentsOfPrime(String prime);

    @Query("SELECT COMPONENT_TABLE.*, prime_type, prime_vaulted, user_component_owned " +
            "FROM COMPONENT_TABLE " +
            "LEFT JOIN PRIME_TABLE ON prime_name == component_prime " +
            "LEFT JOIN USER_COMPONENT_TABLE ON user_component_id == component_id " +
            "WHERE component_prime == :prime " +
            "ORDER BY component_id")
    LiveData<List<ComponentComplete>> getComponentsOfPrimeLD(String prime);

    @Query("SELECT COMPONENT_TABLE.*, prime_type, prime_vaulted, user_component_owned " +
            "FROM COMPONENT_TABLE " +
            "LEFT JOIN PRIME_TABLE ON prime_name == component_prime " +
            "LEFT JOIN USER_COMPONENT_TABLE ON user_component_id == component_id " +
            "WHERE component_id == :id " +
            "ORDER BY component_id")
    LiveData<ComponentComplete> getComponent(String id);

    @RawQuery(observedEntities = {Component.class, UserComponent.class})
    LiveData<List<ComponentComplete>> getComponentsLiveData(SimpleSQLiteQuery query);

    @RawQuery(observedEntities = {Component.class, UserComponent.class})
    List<ComponentComplete> getComponents(SimpleSQLiteQuery query);
}
