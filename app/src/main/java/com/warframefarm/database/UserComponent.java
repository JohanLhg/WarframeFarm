package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_TABLE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = USER_COMPONENT_TABLE)
public class UserComponent {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = USER_COMPONENT_ID)
    private final String component;
    @ColumnInfo(name = USER_COMPONENT_OWNED)
    private final boolean owned;

    public UserComponent(@NonNull String component, boolean owned) {
        this.component = component;
        this.owned = owned;
    }

    @NonNull
    public String getComponent() {
        return component;
    }

    public boolean isOwned() {
        return owned;
    }
}
