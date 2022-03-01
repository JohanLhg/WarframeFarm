package com.warframefarm.database;

import static com.warframefarm.data.WarframeLists.FactionImage;
import static com.warframefarm.data.WarframeLists.PlanetImage;
import static com.warframefarm.data.WarframeLists.PlanetSquareImage;
import static com.warframefarm.data.WarframeLists.PlanetTopImage;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_FACTION;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_TABLE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.warframefarm.R;

@Entity(tableName = PLANET_TABLE)
public class Planet {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = PLANET_NAME)
    private String name;
    @ColumnInfo(name = PLANET_FACTION)
    private String faction;

    @Ignore
    private int image = -2, squareImage = -2, topImage = -2, imageFaction = -2;

    public Planet(@NonNull String name, String faction) {
        this.name = name;
        this.faction = faction;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getFaction() {
        return faction;
    }

    public int getImage() {
        if (image == -2) {
            if (PlanetImage.containsKey(name))
                image = PlanetImage.get(name);
            else image = R.color.transparent;
        }
        return image;
    }

    public int getSquareImage() {
        if (squareImage == -2) {
            if (PlanetSquareImage.containsKey(name))
                squareImage = PlanetSquareImage.get(name);
            else squareImage = R.color.transparent;
        }
        return squareImage;
    }

    public int getTopImage() {
        if (topImage == -2) {
            if (PlanetTopImage.containsKey(name))
                topImage = PlanetTopImage.get(name);
            else topImage = R.color.transparent;
        }
        return topImage;
    }

    public int getImageFaction() {
        if (imageFaction == -2) {
            if (FactionImage.containsKey(faction))
                imageFaction = FactionImage.get(faction);
            else imageFaction = R.color.transparent;
        }
        return imageFaction;
    }
}
