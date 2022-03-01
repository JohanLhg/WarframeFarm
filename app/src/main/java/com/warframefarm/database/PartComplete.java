package com.warframefarm.database;

import static com.warframefarm.data.WarframeLists.PrimeImage;
import static com.warframefarm.data.WarframeLists.getImagePart;
import static com.warframefarm.data.WarframeLists.isPartBP;
import static com.warframefarm.database.WarframeFarmDatabase.PART_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.PART_ID;
import static com.warframefarm.database.WarframeFarmDatabase.PART_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.PART_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_OWNED;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.warframefarm.R;

import java.util.Objects;

public class PartComplete implements Item {

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

    @Ignore
    private int image = -2, imagePrime = -2;
    @Ignore
    private boolean blueprint = false;

    public PartComplete(String id, String prime, String component, String type, int needed, boolean vaulted, boolean owned) {
        this.id = id;
        this.prime = prime;
        this.component = component;
        this.type = type;
        this.needed = needed;
        this.vaulted = vaulted;
        this.owned = owned;

        if (id == null || id.equals(""))
            return;
        blueprint = isPartBP(component, type);
    }

    public String getFullName() {
        if (needed > 1)
            return needed + "x " + prime + " " + component;
        else
            return prime + " " + component;
    }

    public String getId() {
        return id;
    }

    public String getPrime() {
        return prime;
    }

    public String getComponent() {
        return component;
    }

    public int getNeeded() {
        return needed;
    }

    public String getType() {
        return type;
    }

    public boolean isVaulted() {
        return vaulted;
    }

    public void switchOwned() {
        owned = !owned;
    }

    public boolean isOwned() {
        return owned;
    }

    public int getImage() {
        if (image == -2) {
            if (id == null || id.equals(""))
                image = R.color.transparent;
            else image = getImagePart(component, prime, type);
        }
        return image;
    }

    public int getImagePrime() {
        System.out.println(prime + " " + component + " Image ID: " + imagePrime);
        if (imagePrime == -2) {
            if (PrimeImage.containsKey(prime)) {
                imagePrime = PrimeImage.get(prime);
            }
            else imagePrime = R.color.transparent;
        }
        return imagePrime;
    }

    public boolean isBlueprint() {
        return blueprint;
    }

    @NonNull
    @Override
    public String toString() {
        return prime + "  " + component + (owned? " is owned" : " is not owned");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartComplete that = (PartComplete) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    public PartComplete clone() {
        return new PartComplete(id, prime, component, type, needed, vaulted, owned);
    }
}
