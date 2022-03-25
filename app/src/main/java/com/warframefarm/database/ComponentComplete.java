package com.warframefarm.database;

import static com.warframefarm.data.WarframeLists.PrimeImage;
import static com.warframefarm.data.WarframeLists.getImageComponent;
import static com.warframefarm.data.WarframeLists.isComponentBP;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_OWNED;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.warframefarm.R;

import java.util.Objects;

public class ComponentComplete implements Item {

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

    @Ignore
    private int image = -2, imagePrime = -2;
    @Ignore
    private boolean blueprint = false;

    public ComponentComplete(String id, String prime, String type, String primeType, int needed, boolean vaulted, boolean owned) {
        this.id = id;
        this.prime = prime;
        this.type = type;
        this.primeType = primeType;
        this.needed = needed;
        this.vaulted = vaulted;
        this.owned = owned;

        if (id == null || id.equals(""))
            return;
        blueprint = isComponentBP(type, primeType);
    }

    public String getFullName() {
        if (needed > 1)
            return needed + "x " + prime + " " + type;
        else
            return prime + " " + type;
    }

    public String getId() {
        return id;
    }

    public String getPrime() {
        return prime;
    }

    public String getType() {
        return type;
    }

    public int getNeeded() {
        return needed;
    }

    public String getPrimeType() {
        return primeType;
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
            else image = getImageComponent(type, prime, primeType);
        }
        return image;
    }

    public int getImagePrime() {
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
        return prime + "  " + type + (owned? " is owned" : " is not owned");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentComplete that = (ComponentComplete) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    public ComponentComplete clone() {
        return new ComponentComplete(id, prime, type, primeType, needed, vaulted, owned);
    }
}
