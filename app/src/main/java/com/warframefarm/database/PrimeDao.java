package com.warframefarm.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface PrimeDao {

    @Insert(onConflict = REPLACE)
    void insert(Prime prime);

    @Update
    void update(Prime prime);

    @Delete
    void delete(Prime prime);

    @Query("SELECT PRIME_TABLE.*, user_prime_owned " +
            "FROM PRIME_TABLE " +
            "LEFT JOIN USER_PRIME_TABLE ON user_prime_name == prime_name " +
            "WHERE prime_name == :prime")
    LiveData<PrimeComplete> getPrime(String prime);

    @RawQuery(observedEntities = {Prime.class, UserPrime.class})
    LiveData<List<PrimeComplete>> getPrimesLiveData(SupportSQLiteQuery query);

    @RawQuery(observedEntities = {Prime.class, UserPrime.class})
    List<PrimeComplete> getPrimes(SupportSQLiteQuery query);

    @Query("SELECT * FROM prime_table")
    LiveData<List<Prime>> getPrimes();

    @Query("SELECT prime_name FROM prime_table")
    List<String> getPrimeNames();

    @Query("UPDATE PRIME_TABLE " +
            "SET prime_vaulted = CASE " +
                "WHEN prime_name IN (" +
                    "SELECT DISTINCT component_prime " +
                    "FROM COMPONENT_TABLE " +
                    "WHERE component_id IN (" +
                        "SELECT DISTINCT r_reward_component " +
                        "FROM RELIC_REWARD_TABLE " +
                        "INNER JOIN MISSION_REWARD_TABLE ON m_reward_relic == r_reward_relic" +
                    ")" +
                ") THEN '0'" +
                "ELSE '1'" +
            "END")
    void setVaultStates();
}
