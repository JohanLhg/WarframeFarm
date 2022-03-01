package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_ID;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_TABLE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = USER_PART_TABLE)
public class UserPart {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = USER_PART_ID)
    private String part;
    @ColumnInfo(name = USER_PART_OWNED)
    private boolean owned;

    public UserPart(@NonNull String part, boolean owned) {
        this.part = part;
        this.owned = owned;
    }

    @NonNull
    public String getPart() {
        return part;
    }

    public boolean isOwned() {
        return owned;
    }
}
