package com.warframefarm.activities.list.components;

import static com.warframefarm.data.WarframeConstants.ARCHWING;
import static com.warframefarm.data.WarframeConstants.MELEE;
import static com.warframefarm.data.WarframeConstants.PET;
import static com.warframefarm.data.WarframeConstants.PRIMARY;
import static com.warframefarm.data.WarframeConstants.SECONDARY;
import static com.warframefarm.data.WarframeConstants.SENTINEL;
import static com.warframefarm.data.WarframeConstants.WARFRAME;
import static com.warframefarm.database.WarframeFarmDatabase.ITEM_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_TABLE;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.AppExecutors;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.ComponentDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class ComponentsRepository {

    private static ComponentsRepository instance;
    private final ComponentDao componentDao;

    private final FirestoreHelper firestoreHelper;

    private final Executor backgroundThread, mainThread;

    private String search = "";
    private String order = ITEM_NEEDED;
    private final MutableLiveData<String> filter = new MutableLiveData<>("");
    private final List<String> sortOptionValues = new ArrayList<>();

    private final HashMap<String, Boolean> componentsBeforeChanges = new HashMap<>();
    private final MutableLiveData<List<ComponentComplete>> componentsAfterChanges = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<LiveData<List<ComponentComplete>>> components = new MutableLiveData<>();

    public static ComponentsRepository getInstance(Application application) {
        if (instance == null)
            instance = new ComponentsRepository(application);
        return instance;
    }

    private ComponentsRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        componentDao = database.componentDao();

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
        updateComponents();
    }

    public void setOrder(int position) {
        order = sortOptionValues.get(position);
        updateComponents();
    }

    public void setFilter(String newFilter) {
        if (filter.getValue() != null && filter.getValue().equals(newFilter))
            filter.setValue("");
        else
            filter.setValue(newFilter);
        updateComponents();
    }

    public LiveData<String> getFilter() {
        return filter;
    }

    public void modifyComponent(ComponentComplete component) {
        List<ComponentComplete> componentsAfterChanges = this.componentsAfterChanges.getValue();
        String id = component.getId();
        component.switchOwned();

        if (componentsAfterChanges == null)
            componentsAfterChanges = new ArrayList<>();

        if (componentsBeforeChanges.containsKey(id)) {
            componentsAfterChanges.remove(component);
            if (component.isOwned() == componentsBeforeChanges.get(id))
                componentsBeforeChanges.remove(id);
            else componentsAfterChanges.add(component);
        }
        else {
            componentsBeforeChanges.put(id, !component.isOwned());
            componentsAfterChanges.add(component);
        }

        this.componentsAfterChanges.setValue(componentsAfterChanges);
    }

    public void clearChanges() {
        componentsBeforeChanges.clear();
        componentsAfterChanges.setValue(new ArrayList<>());
    }

    public void applyChanges() {
        backgroundThread.execute(() -> {
            firestoreHelper.setComponentsOwned(componentsAfterChanges.getValue());
            mainThread.execute(() -> {
                clearChanges();
                updateComponents();
            });
        });
    }

    public LiveData<List<ComponentComplete>> getComponentsAfterChanges() {
        return componentsAfterChanges;
    }

    public LiveData<LiveData<List<ComponentComplete>>> getComponents() {
        return components;
    }

    public void updateComponents() {
        backgroundThread.execute(() -> {
            String queryString = "SELECT " + COMPONENT_ID + ", " + COMPONENT_PRIME + ", " + COMPONENT_TYPE + ", " + COMPONENT_NEEDED +
                    ", COALESCE(" + USER_COMPONENT_OWNED + ", 0) AS " + USER_COMPONENT_OWNED + ", " + PRIME_TYPE + ", " + PRIME_VAULTED +
                    " FROM " + COMPONENT_TABLE +
                    " LEFT JOIN " + USER_COMPONENT_TABLE + " ON " + USER_COMPONENT_ID + " == " + COMPONENT_ID +
                    " LEFT JOIN " + PRIME_TABLE + " ON " + PRIME_NAME + " == " + COMPONENT_PRIME;

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
                    COMPONENT_TYPE;

            switch (order) {
                case "": break;

                case PRIME_TYPE:
                    queryString += " ORDER BY " + defaultOrder;
                    break;

                case ITEM_NEEDED:
                    queryString += " ORDER BY " +
                            USER_COMPONENT_OWNED + ", " +
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
            LiveData<List<ComponentComplete>> componentList = componentDao.getComponentsLiveData(query);

            mainThread.execute(() -> components.setValue(componentList));
        });
    }
}
