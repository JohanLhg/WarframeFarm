package com.warframefarm.repositories;

import static com.warframefarm.data.WarframeConstants.ARCHWING;
import static com.warframefarm.data.WarframeConstants.ARCH_GUN;
import static com.warframefarm.data.WarframeConstants.MELEE;
import static com.warframefarm.data.WarframeConstants.PET;
import static com.warframefarm.data.WarframeConstants.PRIMARY;
import static com.warframefarm.data.WarframeConstants.SECONDARY;
import static com.warframefarm.data.WarframeConstants.SENTINEL;
import static com.warframefarm.data.WarframeConstants.WARFRAME;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.ITEM_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_TABLE;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.ComponentDao;
import com.warframefarm.database.Item;
import com.warframefarm.database.Mission;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.List;

public class ComponentRepository {

    private final MissionRepository missionRepository;
    private final RelicRepository relicRepository;

    private final ComponentDao componentDao;

    private final FirestoreHelper firestoreHelper;

    public ComponentRepository(Application application) {
        missionRepository = new MissionRepository(application);
        relicRepository = new RelicRepository(application);

        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        componentDao = database.componentDao();

        firestoreHelper = FirestoreHelper.getInstance(application);
    }

    public LiveData<ComponentComplete> getComponent(String id) {
        return componentDao.getComponent(id);
    }

    public LiveData<List<ComponentComplete>> getComponents(String filter, String search, String order) {
        String queryString = "SELECT " + COMPONENT_ID + ", " + COMPONENT_PRIME + ", " + COMPONENT_TYPE + ", " + COMPONENT_NEEDED +
                ", COALESCE(" + USER_COMPONENT_OWNED + ", 0) AS " + USER_COMPONENT_OWNED + ", " + PRIME_TYPE + ", " + PRIME_VAULTED +
                " FROM " + COMPONENT_TABLE +
                " LEFT JOIN " + USER_COMPONENT_TABLE + " ON " + USER_COMPONENT_ID + " == " + COMPONENT_ID +
                " LEFT JOIN " + PRIME_TABLE + " ON " + PRIME_NAME + " == " + COMPONENT_PRIME;

        boolean hasCondition = false;
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

        String defaultOrder = PRIME_TYPE + " == '" + ARCH_GUN + "', " +
                PRIME_TYPE + " == '" + MELEE + "', " +
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
        return componentDao.getComponentsLiveData(query);
    }

    public List<ComponentComplete> getComponentsNotIn(List<Item> components, String filter, String search, String order) {
        String queryString = "SELECT " + COMPONENT_ID + ", " + COMPONENT_PRIME + ", " + COMPONENT_TYPE + ", " + COMPONENT_NEEDED + ", " + PRIME_TYPE + ", " + PRIME_VAULTED + ", " + USER_COMPONENT_OWNED +
                " FROM " + COMPONENT_TABLE +
                " LEFT JOIN " + USER_COMPONENT_TABLE + " ON " + USER_COMPONENT_ID + " == " + COMPONENT_ID +
                " LEFT JOIN " + PRIME_TABLE + " ON " + PRIME_NAME + " == " + COMPONENT_PRIME +
                " WHERE 1";

        if (!filter.equals(""))
            queryString += " AND " + PRIME_TYPE + " == '" + filter + "'";

        if (!search.equals("")) {
            queryString += " AND (" + PRIME_NAME + " LIKE '" + search + "%'" +
                    " OR " + COMPONENT_ID + " LIKE '" + search + "%'" +
                    " OR " + COMPONENT_TYPE + " LIKE '" + search + "%')";
        }

        if (!components.isEmpty()) {
            for (Item component : components)
                queryString += " AND " + COMPONENT_ID + " NOT LIKE '" + component.getId() + "%'";
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
                        PRIME_NAME + ", " +
                        COMPONENT_TYPE;
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
                        PRIME_NAME + ", " +
                        COMPONENT_TYPE;
        }

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return componentDao.getComponents(query);
    }

    public LiveData<List<ComponentComplete>> getComponentsOfPrime(String name) {
        return componentDao.getComponentsOfPrime(name);
    }

    public LiveData<List<String>> getComponentIDs() {
        return componentDao.getComponentIDs();
    }

    public List<ComponentComplete> getNeededComponents() {
        return componentDao.getNeededComponents();
    }

    public void setComponentOwned(String component, String prime, boolean owned) {
        firestoreHelper.setComponentOwned(component, prime, owned);
    }

    public void setComponentsOwned(List<ComponentComplete> components) {
        firestoreHelper.setComponentsOwned(components);
    }

    public List<Mission> getMissions(String componentID, String filter, String search) {
        return missionRepository.getMissionsForComponent(componentID, filter, search);
    }

    public LiveData<List<RelicComplete>> getRelics(String componentID, String search) {
        return relicRepository.getRelicsForComponent(componentID, search);
    }
}
