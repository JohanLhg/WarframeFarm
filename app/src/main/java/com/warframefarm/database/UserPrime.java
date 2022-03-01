package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.USER_PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PRIME_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PRIME_TABLE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = USER_PRIME_TABLE)
public class UserPrime {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = USER_PRIME_NAME)
    private String prime;
    @ColumnInfo(name = USER_PRIME_OWNED)
    private boolean owned;

    public UserPrime(@NonNull String prime, boolean owned) {
        this.prime = prime;
        this.owned = owned;
    }

    @NonNull
    public String getPrime() {
        return prime;
    }

    public boolean isOwned() {
        return owned;
    }
}
