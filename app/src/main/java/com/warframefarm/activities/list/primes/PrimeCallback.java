package com.warframefarm.activities.list.primes;

import android.widget.ImageView;
import android.widget.TextView;

public interface PrimeCallback {
    void onPrimeClick(String prime_name, ImageView background, ImageView type,
                      ImageView typeShadow, ImageView vault, ImageView vaultShadow,
                      ImageView imageOwned, ImageView shadowOwned, TextView name);
}
