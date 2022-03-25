package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;

import androidx.room.ColumnInfo;

public class ComponentCorrection {

    @ColumnInfo(name = COMPONENT_ID)
    private final String id;
    @ColumnInfo(name = "owned_after")
    private final boolean owned;

    public ComponentCorrection(String id, boolean owned) {
        this.id = id;
        this.owned = owned;
    }

    public String getId() {
        return id;
    }

    public boolean isOwned() {
        return owned;
    }
}
