package com.warframefarm.activities.list.primes;

import static com.warframefarm.data.WarframeConstants.ARCHWING;
import static com.warframefarm.data.WarframeConstants.MELEE;
import static com.warframefarm.data.WarframeConstants.PET;
import static com.warframefarm.data.WarframeConstants.PRIMARY;
import static com.warframefarm.data.WarframeConstants.SECONDARY;
import static com.warframefarm.data.WarframeConstants.SENTINEL;
import static com.warframefarm.data.WarframeConstants.WARFRAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PRIME_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PRIME_TABLE;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.AppExecutors;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.database.PrimeDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class PrimesRepository {

    private final PrimeDao primeDao;

    private final FirestoreHelper firestoreHelper;

    private final Executor backgroundThread, mainThread;

    private String search = "", order = USER_PRIME_OWNED;
    private final MutableLiveData<String> filter = new MutableLiveData<>("");
    private final List<String> orderValues = new ArrayList<>();

    private final MutableLiveData<Boolean> selectionCheck = new MutableLiveData<>(true);
    private final MutableLiveData<Integer> selectionNb = new MutableLiveData<>(0);

    private final MutableLiveData<LiveData<List<PrimeComplete>>> primes = new MutableLiveData<>();

    public PrimesRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        primeDao = database.primeDao();

        firestoreHelper = FirestoreHelper.getInstance(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        Collections.addAll(orderValues,
                USER_PRIME_OWNED,
                PRIME_VAULTED,
                PRIME_TYPE,
                PRIME_NAME
        );
    }

    public void setSearch(String search) {
        this.search = search;
        updatePrimes();
    }

    public void setOrder(int position) {
        this.order = orderValues.get(position);
        updatePrimes();
    }

    public LiveData<String> getFilter() {
        return filter;
    }

    public void setFilter(String newFilter) {
        if (filter.getValue() != null && filter.getValue().equals(newFilter))
            filter.setValue("");
        else
            filter.setValue(newFilter);
        updatePrimes();
    }

    public LiveData<Boolean> getSelectionCheck() {
        return selectionCheck;
    }

    public void setSelectionCheck(boolean check) {
        selectionCheck.setValue(check);
    }

    public LiveData<Integer> getSelectionNb() {
        return selectionNb;
    }

    public void setSelectionNb(int nb) {
        selectionNb.setValue(nb);
    }

    public LiveData<LiveData<List<PrimeComplete>>> getPrimes() {
        return primes;
    }

    public List<String> getPrimeNames() {
        List<String> primeNames = new ArrayList<>();
        List<PrimeComplete> primeList = primes.getValue().getValue();

        if (primeList == null)
            return primeNames;

        for (PrimeComplete prime : primeList)
            primeNames.add(prime.getName());

        return primeNames;
    }

    public void updatePrimes() {
        backgroundThread.execute(() -> {
            String queryString = "SELECT " + PRIME_TABLE + ".*, " + USER_PRIME_OWNED +
                    " FROM " + PRIME_TABLE +
                    " INNER JOIN " + USER_PRIME_TABLE + " ON " + USER_PRIME_NAME + " == " + PRIME_NAME;

            boolean hasCondition = false;
            if (!search.isEmpty()) {
                queryString += " WHERE " + PRIME_NAME + " LIKE \"" + search + "%\"";
                hasCondition = true;
            }

            String filter = this.filter.getValue();
            if(!filter.isEmpty()) {
                if (hasCondition)
                    queryString += " AND " + PRIME_TYPE + " == '" + filter + "'";
                else
                    queryString += " WHERE " + PRIME_TYPE + " == '" + filter + "'";
            }

            String defaultOrder = PRIME_TYPE + " == '" + MELEE + "', " +
                    PRIME_TYPE + " == '" + SECONDARY + "', " +
                    PRIME_TYPE + " == '" + PRIMARY + "', " +
                    PRIME_TYPE + " == '" + SENTINEL + "', " +
                    PRIME_TYPE + " == '" + PET + "', " +
                    PRIME_TYPE + " == '" + ARCHWING + "', " +
                    PRIME_TYPE + " == '" + WARFRAME + "', " +
                    PRIME_NAME;

            switch (order) {
                case PRIME_NAME:
                    queryString += " ORDER BY " + PRIME_NAME;
                    break;

                case PRIME_VAULTED:
                    queryString += " ORDER BY " + PRIME_VAULTED + ", " +
                            defaultOrder;
                    break;

                case USER_PRIME_OWNED:
                    queryString += " ORDER BY " + USER_PRIME_OWNED + ", " +
                            PRIME_VAULTED + ", " +
                            defaultOrder;
                    break;

                default:
                    queryString += " ORDER BY " + defaultOrder;
                    break;
            }

            queryString += ";";

            SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
            LiveData<List<PrimeComplete>> primeList = primeDao.getPrimesLiveData(query);
            mainThread.execute(() -> primes.setValue(primeList));
        });
    }

    public void switchPrimeOwned(PrimeComplete prime) {
        backgroundThread.execute(() -> {
            firestoreHelper.setPrimeOwned(prime.getName(), !prime.isOwned());
            updatePrimes();
        });
    }

    public void setSelectionOwned(List<String> primes) {
        backgroundThread.execute(() -> {
            firestoreHelper.setPrimesOwned(primes, selectionCheck.getValue());
            updatePrimes();
        });
    }
}
