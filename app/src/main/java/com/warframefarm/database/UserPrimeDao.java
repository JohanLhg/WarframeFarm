package com.warframefarm.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserPrimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserPrime userPrime);

    @Update
    int update(UserPrime userPrime);

    @Delete
    void delete(UserPrime userPrime);

    @Query("SELECT * FROM USER_PRIME_TABLE")
    List<UserPrime> getUserPrimes();

    @Query("UPDATE USER_PRIME_TABLE SET user_prime_owned = '0'")
    void resetPrimes();

    @Query("UPDATE USER_PRIME_TABLE" +
            " SET user_prime_owned = :owned" +
            " WHERE user_prime_name == :prime")
    void setOwned(String prime, boolean owned);

    @Query("UPDATE USER_PRIME_TABLE" +
            " SET user_prime_owned = :owned" +
            " WHERE user_prime_name IN (:prime)")
    void setOwned(List<String> prime, boolean owned);

    @Query("WITH BASE_TABLE AS (" +
            " SELECT *" +
                " FROM USER_PRIME_TABLE" +
                " LEFT JOIN PART_TABLE ON part_prime == user_prime_name" +
                " LEFT JOIN USER_PART_TABLE ON user_part_id == part_id" +
                " WHERE user_prime_name == :prime" +
            "), PRIME_CORRECT_TABLE AS (" +
                " SELECT user_prime_name AS prime, (sum(user_part_owned) == count()) == user_prime_owned AS prime_correct" +
                " FROM BASE_TABLE" +
                " GROUP BY user_prime_name" +
            ") SELECT user_prime_name, CASE" +
                " WHEN prime_correct THEN user_prime_owned" +
                " ELSE user_prime_owned == 0" +
            " END AS owned_after" +
            " FROM USER_PRIME_TABLE" +
            " LEFT JOIN PRIME_CORRECT_TABLE ON prime == user_prime_name" +
            " WHERE prime_correct == 0")
    PrimeCorrection getCorrection(String prime);

    @Query("WITH BASE_TABLE AS (" +
            " SELECT *" +
                " FROM USER_PRIME_TABLE" +
                " LEFT JOIN PART_TABLE ON part_prime == user_prime_name" +
                " LEFT JOIN USER_PART_TABLE ON user_part_id == part_id" +
                " WHERE user_prime_name IN (:prime)" +
            "), PRIME_CORRECT_TABLE AS (" +
                " SELECT user_prime_name AS prime, (sum(user_part_owned) == count()) == user_prime_owned AS prime_correct" +
                " FROM BASE_TABLE" +
                " GROUP BY user_prime_name" +
            ") SELECT user_prime_name, CASE" +
                " WHEN prime_correct THEN user_prime_owned" +
                " ELSE user_prime_owned == 0" +
            " END AS owned_after" +
            " FROM USER_PRIME_TABLE" +
            " LEFT JOIN PRIME_CORRECT_TABLE ON prime == user_prime_name" +
            " WHERE prime_correct == 0")
    List<PrimeCorrection> getCorrections(List<String> prime);
}
