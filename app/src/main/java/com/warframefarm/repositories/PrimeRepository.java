package com.warframefarm.repositories;

import static com.warframefarm.data.WarframeConstants.ARCHWING;
import static com.warframefarm.data.WarframeConstants.ARCH_GUN;
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
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.Mission;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.database.PrimeDao;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.List;

public class PrimeRepository {

    private final ComponentRepository componentRepository;
    private final MissionRepository missionRepository;
    private final RelicRepository relicRepository;

    private final PrimeDao primeDao;

    private final FirestoreHelper firestoreHelper;

    public PrimeRepository(Application application) {
        componentRepository = new ComponentRepository(application);
        missionRepository = new MissionRepository(application);
        relicRepository = new RelicRepository(application);

        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        primeDao = database.primeDao();

        firestoreHelper = FirestoreHelper.getInstance(application);
    }

    public void setPrimeOwned(String prime, boolean owned) {
        firestoreHelper.setPrimeOwned(prime, owned);
    }

    public void setPrimesOwned(List<String> primes, boolean owned) {
        firestoreHelper.setPrimesOwned(primes, owned);
    }

    public LiveData<PrimeComplete> getPrime(String prime) {
        return primeDao.getPrime(prime);
    }

    public LiveData<List<PrimeComplete>> getPrimes(String filter, String search, String order) {
        String queryString = "SELECT " + PRIME_TABLE + ".*, " + USER_PRIME_OWNED +
                " FROM " + PRIME_TABLE +
                " INNER JOIN " + USER_PRIME_TABLE + " ON " + USER_PRIME_NAME + " == " + PRIME_NAME;

        boolean hasCondition = false;
        if (!search.isEmpty()) {
            queryString += " WHERE " + PRIME_NAME + " LIKE \"" + search + "%\"";
            hasCondition = true;
        }

        if(!filter.isEmpty()) {
            if (hasCondition)
                queryString += " AND " + PRIME_TYPE + " == '" + filter + "'";
            else
                queryString += " WHERE " + PRIME_TYPE + " == '" + filter + "'";
        }

        String defaultOrder = PRIME_TYPE + " == '" + ARCH_GUN + "', " +
                PRIME_TYPE + " == '" + MELEE + "', " +
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
        return primeDao.getPrimesLiveData(query);
    }

    public List<PrimeComplete> getPrimesNotIn(List<String> primes, String filter, String search, String order) {
        String queryString = "SELECT * FROM " + PRIME_TABLE +
                " LEFT JOIN " + USER_PRIME_TABLE + " ON " + USER_PRIME_NAME + " == " + PRIME_NAME +
                " WHERE 1";

        if (!filter.equals(""))
            queryString += " AND " + PRIME_TYPE + " == '" + filter + "'";

        if (!search.equals(""))
            queryString += " AND " + PRIME_NAME + " LIKE '" + search + "%'";

        if (!primes.isEmpty()) {
            for (String name : primes)
                queryString += " AND " + PRIME_NAME + " != '" + name + "'";
        }

        if (!order.equals("")) {
            if (order.equals(PRIME_TYPE))
                queryString += " ORDER BY " +
                        PRIME_TYPE + " == '" + ARCH_GUN + "', " +
                        PRIME_TYPE + " == '" + MELEE + "', " +
                        PRIME_TYPE + " == '" + SECONDARY + "', " +
                        PRIME_TYPE + " == '" + PRIMARY + "', " +
                        PRIME_TYPE + " == '" + SENTINEL + "', " +
                        PRIME_TYPE + " == '" + PET + "', " +
                        PRIME_TYPE + " == '" + ARCHWING + "', " +
                        PRIME_TYPE + " == '" + WARFRAME + "', " +
                        PRIME_NAME;
            else
                queryString += " ORDER BY " + order + ", " +
                        PRIME_TYPE + " == '" + ARCH_GUN + "', " +
                        PRIME_TYPE + " == '" + MELEE + "', " +
                        PRIME_TYPE + " == '" + SECONDARY + "', " +
                        PRIME_TYPE + " == '" + PRIMARY + "', " +
                        PRIME_TYPE + " == '" + SENTINEL + "', " +
                        PRIME_TYPE + " == '" + PET + "', " +
                        PRIME_TYPE + " == '" + ARCHWING + "', " +
                        PRIME_TYPE + " == '" + WARFRAME + "', " +
                        PRIME_NAME;
        }

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return primeDao.getPrimes(query);
    }

    public LiveData<List<ComponentComplete>> getComponents(String name) {
        return componentRepository.getComponentsOfPrime(name);
    }

    public List<Mission> getMissions(String prime, String filter, String search) {
        return missionRepository.getMissionsForPrime(prime, filter, search);
    }

    public List<RelicComplete> getRelics(String prime, String filter, String search) {
        return relicRepository.getRelicsForPrime(prime, filter, search);
    }
}
