package com.warframefarm.database;

import static com.warframefarm.database.WarframeFarmDatabase.PART_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.PART_ID;
import static com.warframefarm.database.WarframeFarmDatabase.PART_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.PART_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RARITY;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_OWNED;

import androidx.room.ColumnInfo;

public class RelicRewardComplete extends PartComplete{

    @ColumnInfo(name = R_REWARD_RELIC)
    private String relic;
    @ColumnInfo(name = R_REWARD_RARITY)
    private int rarity;
    @ColumnInfo(name = PART_ID)
    private String id;
    @ColumnInfo(name = PART_PRIME)
    private String prime;
    @ColumnInfo(name = PART_COMPONENT)
    private String component;
    @ColumnInfo(name = PRIME_TYPE)
    private String type;
    @ColumnInfo(name = PART_NEEDED)
    private int needed;
    @ColumnInfo(name = PRIME_VAULTED)
    private boolean vaulted;
    @ColumnInfo(name = USER_PART_OWNED)
    private boolean owned;

    public RelicRewardComplete(String relic, int rarity, String id, String prime, String component, String type, int needed, boolean vaulted, boolean owned) {
        super(id, prime, component, type, needed, vaulted, owned);
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
