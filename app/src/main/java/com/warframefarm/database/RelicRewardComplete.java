package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RARITY;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_OWNED;

import androidx.room.ColumnInfo;

public class RelicRewardComplete extends ComponentComplete {

    @ColumnInfo(name = R_REWARD_RELIC)
    private final String relic;
    @ColumnInfo(name = R_REWARD_RARITY)
    private final int rarity;
    @ColumnInfo(name = COMPONENT_ID)
    private String id;
    @ColumnInfo(name = COMPONENT_PRIME)
    private String prime;
    @ColumnInfo(name = COMPONENT_TYPE)
    private String type;
    @ColumnInfo(name = PRIME_TYPE)
    private String primeType;
    @ColumnInfo(name = COMPONENT_NEEDED)
    private int needed;
    @ColumnInfo(name = PRIME_VAULTED)
    private boolean vaulted;
    @ColumnInfo(name = USER_COMPONENT_OWNED)
    private boolean owned;

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
