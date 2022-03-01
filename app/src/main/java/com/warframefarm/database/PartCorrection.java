package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.PART_ID;

import androidx.room.ColumnInfo;

public class PartCorrection {

    @ColumnInfo(name = PART_ID)
    private final String id;
    @ColumnInfo(name = "owned_after")
    private final boolean owned;

    public PartCorrection(String id, boolean owned) {
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
