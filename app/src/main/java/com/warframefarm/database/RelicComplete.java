package com.warframefarm.database;

import static com.warframefarm.data.WarframeLists.RelicEraImage;
import static com.warframefarm.data.WarframeLists.RelicEraRadiantImage;
import static com.warframefarm.data.WarframeLists.RelicRarityImage;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ERA;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RARITY;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.warframefarm.R;

import java.util.ArrayList;
import java.util.List;

public class RelicComplete {

    @ColumnInfo(name = RELIC_ID)
    private final String id;
    @ColumnInfo(name = RELIC_ERA)
    private final String era;
    @ColumnInfo(name = RELIC_NAME)
    private final String name;
    @ColumnInfo(name = RELIC_VAULTED)
    private final boolean vaulted;
    @ColumnInfo(name = R_REWARD_RARITY)
    private int rarityNeeded;

    @Ignore
    private int image =-2, imageRadiant = -2, imageRarity = -2;
    @Ignore
    private List<RelicRewardComplete> neededRewards = new ArrayList<>();

    public RelicComplete(String id, String era, String name, boolean vaulted, int rarityNeeded) {
        this.id = id;
        this.era = era;
        this.name = name;
        this.vaulted = vaulted;
        this.rarityNeeded = rarityNeeded;
    }

    public String getFullName() {
        return era + " " + name + " Relic";
    }

    public String getShortName() {
        return era + " " + name;
    }

    public String getId() {
        return id;
    }

    public String getEra() {
        return era;
    }

    public String getName() {
        return name;
    }

    public boolean isVaulted() {
        return vaulted;
    }

    public int getRarityNeeded() {
        return rarityNeeded;
    }

    public int getImage() {
        if (image == -2) {
            if (RelicEraImage.containsKey(era))
                image = RelicEraImage.get(era);
            else image = R.color.transparent;
        }
        return image;
    }

    public int getImageRadiant() {
        if (imageRadiant == -2) {
            if (RelicEraRadiantImage.containsKey(era))
                imageRadiant = RelicEraRadiantImage.get(era);
            else imageRadiant = R.color.transparent;
        }
        return imageRadiant;
    }

    public int getImageRarity() {
        if (imageRarity == -2) {
            if (rarityNeeded != 0 && RelicRarityImage.containsKey(rarityNeeded))
                imageRarity = RelicRarityImage.get(rarityNeeded);
            else imageRarity = -1;
        }
        return imageRarity;
    }

    public void addNeededReward(RelicRewardComplete reward) {
        neededRewards.add(reward);
        int rarity = reward.getRarity();
        if (rarityNeeded < rarity)
            rarityNeeded = rarity;
    }

    public List<RelicRewardComplete> getNeededRewards() {
        return neededRewards;
    }
}
