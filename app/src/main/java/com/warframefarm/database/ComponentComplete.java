package com.warframefarm.database;

import static com.warframefarm.data.WarframeConstants.BLUEPRINT;
import static com.warframefarm.data.WarframeLists.getImageComponent;
import static com.warframefarm.data.WarframeLists.isComponentBP;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_OWNED;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.warframefarm.R;
import com.warframefarm.data.FirestoreHelper;

import java.util.Objects;

public class ComponentComplete implements Item {

    @ColumnInfo(name = COMPONENT_ID)
    private final String id;
    @ColumnInfo(name = COMPONENT_PRIME)
    private final String prime;
    @ColumnInfo(name = COMPONENT_TYPE)
    private final String type;
    @ColumnInfo(name = PRIME_TYPE)
    private final String primeType;
    @ColumnInfo(name = COMPONENT_NEEDED)
    private final int needed;
    @ColumnInfo(name = PRIME_VAULTED)
    private final boolean vaulted;
    @ColumnInfo(name = USER_COMPONENT_OWNED)
    private boolean owned;

    @Ignore
    private int image = -2;
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

    public String getName() {
        return id;
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

    public void displayPrimeImage(Context context, ImageView view) {
        FirestoreHelper.loadPrimeImage(prime, context, view);
    }

    public void displayImage(Context context, ImageView view) {
        view.setBackgroundResource(blueprint ? R.drawable.blueprint_bg : R.color.transparent);

        if (type.equals(BLUEPRINT))
            FirestoreHelper.loadPrimeImage(prime, context, view);
        else
            view.setImageResource(getImage());
    }

    public int getImage() {
        if (image == -2) {
            if (id == null || id.equals(""))
                image = R.color.transparent;
            else image = getImageComponent(type, primeType);
        }
        return image;
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
