package com.warframefarm.activities.inventory;

import static com.warframefarm.data.WarframeConstants.ARCHWING;
import static com.warframefarm.data.WarframeConstants.MELEE;
import static com.warframefarm.data.WarframeConstants.PET;
import static com.warframefarm.data.WarframeConstants.PRIMARY;
import static com.warframefarm.data.WarframeConstants.SECONDARY;
import static com.warframefarm.data.WarframeConstants.SENTINEL;
import static com.warframefarm.data.WarframeConstants.WARFRAME;
import static com.warframefarm.database.WarframeFarmDatabase.ITEM_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.PART_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.PART_ID;
import static com.warframefarm.database.WarframeFarmDatabase.PART_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.PART_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.PART_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_ID;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_TABLE;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.AppExecutors;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.PartComplete;
import com.warframefarm.database.PartDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class InventoryRepository {

    private static InventoryRepository instance;
    private final PartDao partDao;

    private final FirestoreHelper firestoreHelper;

    private final Executor backgroundThread, mainThread;

    private String search = "";
    private String order = ITEM_NEEDED;
    private final MutableLiveData<String> filter = new MutableLiveData<>("");
    private final List<String> sortOptionValues = new ArrayList<>();

    private final HashMap<String, Boolean> partsBeforeChanges = new HashMap<>();
    private final MutableLiveData<List<PartComplete>> partsAfterChanges = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<LiveData<List<PartComplete>>> parts = new MutableLiveData<>();

    public static InventoryRepository getInstance(Application application) {
        if (instance == null)
            instance = new InventoryRepository(application);
        return instance;
    }

    private InventoryRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        partDao = database.partDao();

        firestoreHelper = FirestoreHelper.getInstance(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        Collections.addAll(sortOptionValues,
                ITEM_NEEDED,
                PRIME_VAULTED,
                PRIME_TYPE,
                PRIME_NAME
        );
    }

    public void setSearch(String search) {
        this.search = search;
        updateParts();
    }

    public void setOrder(int position) {
        order = sortOptionValues.get(position);
        updateParts();
    }

    public void setFilter(String newFilter) {
        if (filter.getValue() != null && filter.getValue().equals(newFilter))
            filter.setValue("");
        else
            filter.setValue(newFilter);
        updateParts();
    }

    public LiveData<String> getFilter() {
        return filter;
    }

    public void modifyPart(PartComplete part) {
        List<PartComplete> partsAfterChanges = this.partsAfterChanges.getValue();
        String id = part.getId();
        part.switchOwned();

        if (partsAfterChanges == null)
            partsAfterChanges = new ArrayList<>();

        if (partsBeforeChanges.containsKey(id)) {
            partsAfterChanges.remove(part);
            if (part.isOwned() == partsBeforeChanges.get(id))
                partsBeforeChanges.remove(id);
            else partsAfterChanges.add(part);
        }
        else {
            partsBeforeChanges.put(id, !part.isOwned());
            partsAfterChanges.add(part);
        }

        this.partsAfterChanges.setValue(partsAfterChanges);
    }

    public void clearChanges() {
        partsBeforeChanges.clear();
        partsAfterChanges.setValue(new ArrayList<>());
    }

    public void applyChanges() {
        backgroundThread.execute(() -> {
            firestoreHelper.setPartsOwned(partsAfterChanges.getValue());
            mainThread.execute(() -> {
                clearChanges();
                updateParts();
            });
        });
    }

    public LiveData<List<PartComplete>> getPartsAfterChanges() {
        return partsAfterChanges;
    }

    public LiveData<LiveData<List<PartComplete>>> getParts() {
        return parts;
    }

    public void updateParts() {
        backgroundThread.execute(() -> {
            String queryString = "SELECT " + PART_ID + ", " + PART_PRIME + ", " + PART_COMPONENT + ", " + PART_NEEDED +
                    ", COALESCE(" + USER_PART_OWNED + ", 0) AS " + USER_PART_OWNED + ", " + PRIME_TYPE + ", " + PRIME_VAULTED +
                    " FROM " + PART_TABLE +
                    " LEFT JOIN " + USER_PART_TABLE + " ON " + USER_PART_ID + " == " + PART_ID +
                    " LEFT JOIN " + PRIME_TABLE + " ON " + PRIME_NAME + " == " + PART_PRIME;

            boolean hasCondition = false;
            String filter = this.filter.getValue();
            if (!filter.isEmpty()) {
                hasCondition = true;
                queryString += " WHERE " + PRIME_TYPE + " == '" + filter + "'";
            }

            if (!search.isEmpty()) {
                if (hasCondition)
                    queryString += " AND " + PRIME_NAME + " LIKE '" + search + "%'";
                else
                    queryString += " WHERE " + PRIME_NAME + " LIKE '" + search + "%'";
            }

            String defaultOrder = PRIME_TYPE + " == '" + MELEE + "', " +
                    PRIME_TYPE + " == '" + SECONDARY + "', " +
                    PRIME_TYPE + " == '" + PRIMARY + "', " +
                    PRIME_TYPE + " == '" + SENTINEL + "', " +
                    PRIME_TYPE + " == '" + PET + "', " +
                    PRIME_TYPE + " == '" + ARCHWING + "', " +
                    PRIME_TYPE + " == '" + WARFRAME + "', " +
                    PRIME_NAME + ", " +
                    PART_COMPONENT;

            switch (order) {
                case "": break;

                case PRIME_TYPE:
                    queryString += " ORDER BY " + defaultOrder;
                    break;

                case ITEM_NEEDED:
                    queryString += " ORDER BY " +
                            USER_PART_OWNED + ", " +
                            PRIME_VAULTED + ", " +
                            defaultOrder;
                    break;

                default:
                    queryString += " ORDER BY " + order + ", " +
                            defaultOrder;
                    break;
            }

            queryString += ";";

            SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
            LiveData<List<PartComplete>> partList = partDao.getPartsLiveData(query);

            mainThread.execute(() -> parts.setValue(partList));
        });
    }
}
