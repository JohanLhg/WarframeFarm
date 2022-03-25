package com.warframefarm.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserComponentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserComponent userComponent);

    @Update
    int update(UserComponent userComponent);

    @Delete
    void delete(UserComponent userComponent);

    @Query("SELECT * FROM USER_COMPONENT_TABLE")
    List<UserComponent> getUserComponents();

    @Query("UPDATE USER_COMPONENT_TABLE SET user_component_owned = '0'")
    void resetComponents();

    @Query("UPDATE USER_COMPONENT_TABLE" +
            " SET user_component_owned = :owned" +
            " WHERE user_component_id == :component")
    void setOwned(String component, boolean owned);

    @Query("WITH BASE_TABLE AS (" +
                " SELECT *" +
                " FROM USER_PRIME_TABLE" +
                " LEFT JOIN COMPONENT_TABLE ON component_prime == user_prime_name" +
                " LEFT JOIN USER_COMPONENT_TABLE ON user_component_id == component_id" +
                " WHERE user_prime_name == :prime" +
            "), PRIME_CORRECT_TABLE AS (" +
                " SELECT user_prime_name AS prime, (SUM(user_component_owned) == count()) == user_prime_owned as prime_correct" +
                " FROM BASE_TABLE" +
                " GROUP BY user_prime_name" +
            ") SELECT component_id, CASE" +
                " WHEN prime_correct THEN user_component_owned" +
                " ELSE user_prime_owned" +
            " END AS owned_after" +
            " FROM BASE_TABLE" +
            " LEFT JOIN PRIME_CORRECT_TABLE ON prime == user_prime_name" +
            " WHERE user_component_owned != owned_after")
    List<ComponentCorrection> getCorrections(String prime);

    @Query("WITH BASE_TABLE AS (" +
                " SELECT *" +
                " FROM USER_PRIME_TABLE" +
                " LEFT JOIN COMPONENT_TABLE ON component_prime == user_prime_name" +
                " LEFT JOIN USER_COMPONENT_TABLE ON user_component_id == component_id" +
                " WHERE user_prime_name IN (:prime)" +
            "), PRIME_CORRECT_TABLE AS (" +
                " SELECT user_prime_name AS prime, (SUM(user_component_owned) == count()) == user_prime_owned as prime_correct" +
                " FROM BASE_TABLE" +
                " GROUP BY user_prime_name" +
            ") SELECT component_id, CASE" +
                " WHEN prime_correct THEN user_component_owned" +
                " ELSE user_prime_owned" +
            " END AS owned_after" +
            " FROM BASE_TABLE" +
            " LEFT JOIN PRIME_CORRECT_TABLE ON prime == user_prime_name" +
            " WHERE user_component_owned != owned_after")
    List<ComponentCorrection> getCorrections(List<String> prime);
}

