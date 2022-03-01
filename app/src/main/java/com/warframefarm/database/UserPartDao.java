package com.warframefarm.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserPartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserPart userPart);

    @Update
    int update(UserPart userPart);

    @Delete
    void delete(UserPart userPart);

    @Query("SELECT * FROM USER_PART_TABLE")
    List<UserPart> getUserParts();

    @Query("UPDATE USER_PART_TABLE SET user_part_owned = '0'")
    void resetParts();

    @Query("UPDATE USER_PART_TABLE" +
            " SET user_part_owned = :owned" +
            " WHERE user_part_id == :part")
    void setOwned(String part, boolean owned);

    @Query("WITH BASE_TABLE AS (" +
                " SELECT *" +
                " FROM USER_PRIME_TABLE" +
                " LEFT JOIN PART_TABLE ON part_prime == user_prime_name" +
                " LEFT JOIN USER_PART_TABLE ON user_part_id == part_id" +
                " WHERE user_prime_name == :prime" +
            "), PRIME_CORRECT_TABLE AS (" +
                " SELECT user_prime_name AS prime, (SUM(user_part_owned) == count()) == user_prime_owned as prime_correct" +
                " FROM BASE_TABLE" +
                " GROUP BY user_prime_name" +
            ") SELECT part_id, CASE" +
                " WHEN prime_correct THEN user_part_owned" +
                " ELSE user_prime_owned" +
            " END AS owned_after" +
            " FROM BASE_TABLE" +
            " LEFT JOIN PRIME_CORRECT_TABLE ON prime == user_prime_name" +
            " WHERE user_part_owned != owned_after")
    List<PartCorrection> getCorrections(String prime);

    @Query("WITH BASE_TABLE AS (" +
                " SELECT *" +
                " FROM USER_PRIME_TABLE" +
                " LEFT JOIN PART_TABLE ON part_prime == user_prime_name" +
                " LEFT JOIN USER_PART_TABLE ON user_part_id == part_id" +
                " WHERE user_prime_name IN (:prime)" +
            "), PRIME_CORRECT_TABLE AS (" +
                " SELECT user_prime_name AS prime, (SUM(user_part_owned) == count()) == user_prime_owned as prime_correct" +
                " FROM BASE_TABLE" +
                " GROUP BY user_prime_name" +
            ") SELECT part_id, CASE" +
                " WHEN prime_correct THEN user_part_owned" +
                " ELSE user_prime_owned" +
            " END AS owned_after" +
            " FROM BASE_TABLE" +
            " LEFT JOIN PRIME_CORRECT_TABLE ON prime == user_prime_name" +
            " WHERE user_part_owned != owned_after")
    List<PartCorrection> getCorrections(List<String> prime);
}

