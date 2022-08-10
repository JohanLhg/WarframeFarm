package com.warframefarm.database;

import android.content.Context;
import android.widget.ImageView;

public interface Item {
    String getId();
    String getName();
    String getFullName();
    boolean isOwned();
    void displayImage(Context context, ImageView view);
}
