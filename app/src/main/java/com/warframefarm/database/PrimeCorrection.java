package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.USER_PRIME_NAME;

import androidx.room.ColumnInfo;

public class PrimeCorrection {

    @ColumnInfo(name = USER_PRIME_NAME)
    private final String id;
    @ColumnInfo(name = "owned_after")
    private final boolean owned;

    public PrimeCorrection(String id, boolean owned) {
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
