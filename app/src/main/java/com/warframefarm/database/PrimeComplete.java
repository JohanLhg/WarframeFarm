package com.warframefarm.database;

import static com.warframefarm.data.WarframeLists.PrimeTypeImage;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PRIME_OWNED;

import android.content.Context;
import android.widget.ImageView;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.warframefarm.R;
import com.warframefarm.data.FirestoreHelper;

import java.util.Objects;

public class PrimeComplete implements Item {

    @ColumnInfo(name = PRIME_NAME)
    private final String name;
    @ColumnInfo(name = PRIME_TYPE)
    private final String type;
    @ColumnInfo(name = PRIME_VAULTED)
    private final boolean vaulted;
    @ColumnInfo(name = USER_PRIME_OWNED)
    private final boolean owned;

    @Ignore
    private int imageType = -2;

    public PrimeComplete(String name, String type, boolean vaulted, boolean owned) {
        this.name = name;
        this.type = type;
        this.vaulted = vaulted;
        this.owned = owned;
    }

    @Override
    public String getId() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return name + " Prime";
    }

    public String getType() {
        return type;
    }

    public boolean isVaulted() {
        return vaulted;
    }

    public boolean isOwned() {
        return owned;
    }

    public void displayImage(Context context, ImageView view) {
        FirestoreHelper.loadPrimeImage(name, context, view);
    }

    public int getImageType() {
        if (imageType == -2) {
            if (PrimeTypeImage.containsKey(type))
                imageType = PrimeTypeImage.get(type);
            else imageType = R.color.transparent;
        }
        return imageType;
    }

    @Override
    public String toString() {
        return "{" + name + " " + type +
                ", vaulted=" + vaulted +
                ", owned=" + owned +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimeComplete that = (PrimeComplete) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
