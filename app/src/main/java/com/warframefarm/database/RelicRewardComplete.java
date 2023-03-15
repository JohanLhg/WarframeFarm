package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RARITY;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;

import androidx.room.ColumnInfo;

public class RelicRewardComplete extends ComponentComplete {

    @ColumnInfo(name = R_REWARD_RELIC)
    private final String relic;
    @ColumnInfo(name = R_REWARD_RARITY)
    private final int rarity;

    public RelicRewardComplete(String relic, int rarity, String id, String prime, String type, String primeType, int needed, boolean vaulted, boolean owned) {
        super(id, prime, type, primeType, needed, vaulted, owned);
        this.relic = relic;
        this.rarity = rarity;
    }

    public String getRelic() {
        return relic;
    }

    public int getRarity() {
        return rarity;
    }
}
