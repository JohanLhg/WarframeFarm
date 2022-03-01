package com.warframefarm.activities.farm;

import static com.warframefarm.data.WarframeConstants.ARCHWING;
import static com.warframefarm.data.WarframeConstants.AXI;
import static com.warframefarm.data.WarframeConstants.LITH;
import static com.warframefarm.data.WarframeConstants.MELEE;
import static com.warframefarm.data.WarframeConstants.MESO;
import static com.warframefarm.data.WarframeConstants.NEO;
import static com.warframefarm.data.WarframeConstants.PET;
import static com.warframefarm.data.WarframeConstants.PRIMARY;
import static com.warframefarm.data.WarframeConstants.SECONDARY;
import static com.warframefarm.data.WarframeConstants.SENTINEL;
import static com.warframefarm.data.WarframeConstants.WARFRAME;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_FACTION;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_OBJECTIVE;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_PLANET;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_ROTATION;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PART_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.PART_ID;
import static com.warframefarm.database.WarframeFarmDatabase.PART_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.PART_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.PART_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ERA;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_PART;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RARITY;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_ID;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PRIME_TABLE;

import android.app.Application;
import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.MissionDao;
import com.warframefarm.database.MissionReward;
import com.warframefarm.database.PartComplete;
import com.warframefarm.database.PartDao;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.database.PrimeDao;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.RelicDao;
import com.warframefarm.database.RelicRewardComplete;
import com.warframefarm.database.WarframeFarmDatabase;
import com.warframefarm.database.Part;
import com.warframefarm.database.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class FarmRepository {

    private static FarmRepository instance;

    private final PrimeDao primeDao;
    private final PartDao partDao;
    private final RelicDao relicDao;
    private final MissionDao missionDao;

    private final Executor backgroundThread, mainThread;

    //region Fragment
    public static final int RELIC = 0, MISSION = 1;
    private final MutableLiveData<Integer> mode = new MutableLiveData<>(RELIC);
    private final MutableLiveData<Boolean> relicFilter = new MutableLiveData<>(false),
            missionFilter = new MutableLiveData<>(false);

    private final MutableLiveData<List<Item>> items = new MutableLiveData<>(new ArrayList<>());
    private final List<String> itemNames = new ArrayList<>();
    private final MutableLiveData<List<RelicComplete>> relics = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<MissionComplete>> missions = new MutableLiveData<>(new ArrayList<>());
    //endregion

    //Dialogs
    private final MutableLiveData<String> dialogFilter = new MutableLiveData<>("");
    private String dialogSearch = "", dialogOrder = PRIME_VAULTED;
    private final MutableLiveData<List<PrimeComplete>> remainingPrimes = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<PartComplete>> remainingParts = new MutableLiveData<>(new ArrayList<>());

    private FarmRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        primeDao = database.primeDao();
        partDao = database.partDao();
        relicDao = database.relicDao();
        missionDao = database.missionDao();

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        addAllNeededItems();
    }

    public static FarmRepository getInstance(Application application) {
        if (instance == null)
            instance = new FarmRepository(application);
        return instance;
    }

    public LiveData<Integer> getMode() {
        return mode;
    }

    public LiveData<Boolean> getRelicFilter() {
        return relicFilter;
    }

    public LiveData<Boolean> getMissionFilter() {
        return missionFilter;
    }

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public LiveData<List<RelicComplete>> getRelics() {
        return relics;
    }

    public LiveData<List<MissionComplete>> getMissions() {
        return missions;
    }

    public LiveData<List<PrimeComplete>> getRemainingPrimes() {
        return remainingPrimes;
    }

    public LiveData<List<PartComplete>> getRemainingParts() {
        return remainingParts;
    }

    public LiveData<String> getDialogFilter() {
        return dialogFilter;
    }

    public void setDialogSearch(String search) {
        dialogSearch = search;
        updateRemainingPrimes();
        updateRemainingParts();
    }

    public void setDialogFilter(String filter) {
        if (dialogFilter.getValue().equals(filter))
            dialogFilter.setValue("");
        else
            dialogFilter.setValue(filter);
        updateRemainingPrimes();
        updateRemainingParts();
    }

    public void setDialogOrder(String order) {
        dialogOrder = order;
        updateRemainingPrimes();
        updateRemainingParts();
    }

    public void setMode(int mode) {
        this.mode.setValue(mode);
        updateResults();
    }

    public void checkFilter() {
        if (mode.getValue() == RELIC) {
            relicFilter.setValue(!relicFilter.getValue());
            updateRelics();
        }
        else if (mode.getValue() == MISSION) {
            missionFilter.setValue(!missionFilter.getValue());
            updateMissions();
        }
    }

    public void clearItems() {
        items.setValue(new ArrayList<>());
        itemNames.clear();
        updateResults();
    }

    public void addAllNeededItems() {
        backgroundThread.execute(() -> {
            List<PartComplete> neededItems = getNeededParts();

            if (neededItems.isEmpty()) return;

            String itemName;
            for (Item item : neededItems) {
                itemName = item.getId();

                if (itemNames.contains(itemName) || itemName.equals("") ||
                        (item instanceof Part && itemNames.contains(((Part) item).getPrime())))
                    continue;

                itemNames.add(itemName);
                List<Item> newList = items.getValue();
                newList.add(item);
                mainThread.execute(() -> items.setValue(newList));
            }
            updateResults();
        });
    }

    public void addPrimes(List<String> primeNames, List<PrimeComplete> primes) {
        itemNames.addAll(primeNames);
        List<Item> newList = items.getValue();
        newList.addAll(primes);
        items.setValue(newList);

        updateResults();
    }

    public void addParts(List<String> partNames, List<PartComplete> parts) {
        itemNames.addAll(partNames);
        List<Item> newList = items.getValue();
        newList.addAll(parts);
        items.setValue(newList);

        updateResults();
    }

    public void removeItem(Item item) {
        itemNames.remove(item.getId());
        List<Item> newList = items.getValue();
        newList.remove(item);
        items.setValue(newList);

        updateResults();
    }

    public List<String> getSelectedPrimeNames() {
        List<String> names = new ArrayList<>();
        for (Item item : items.getValue()) {
            if (item instanceof PrimeComplete)
                names.add(item.getId());
        }
        return names;
    }

    public void updateResults() {
        if (mode.getValue() == RELIC)
            updateRelics();
        else if (mode.getValue() == MISSION)
            updateMissions();
    }

    public void updateRelics() {
        backgroundThread.execute(() -> {
            List<RelicComplete> relicList = getRelicsForItems(itemNames, relicFilter.getValue());
            mainThread.execute(() -> relics.setValue(relicList));
        });
    }

    public void updateMissions() {
        backgroundThread.execute(() -> {
            List<MissionComplete> missionList = getMissionsForItems(itemNames, missionFilter.getValue());
            mainThread.execute(() -> missions.setValue(missionList));
        });
    }

    public void updateRemainingPrimes() {
        backgroundThread.execute(() -> {
            List<PrimeComplete> primes = getRemainingPrimesFromDB();

            mainThread.execute(() -> remainingPrimes.setValue(primes));
        });
    }

    public void updateRemainingParts() {
        backgroundThread.execute(() -> {
            List<PartComplete> parts = getRemainingPartsFromDB();

            mainThread.execute(() -> remainingParts.setValue(parts));
        });
    }

    public List<PartComplete> getNeededParts() {
        return partDao.getNeededParts();
    }

    public List<RelicComplete> getRelicsForItems(List<String> items, boolean showVaulted) {
        List<RelicComplete> relics = new ArrayList<>();
        if (items.isEmpty())
            return relics;

        String queryString = "SELECT *" +
                " FROM " + RELIC_TABLE +
                " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                " LEFT JOIN " + PART_TABLE + " ON " + PART_ID + " == " + R_REWARD_PART +
                " LEFT JOIN " + PRIME_TABLE + " ON " + PRIME_NAME + " == " + PART_PRIME +
                " WHERE ";

        int size = items.size();
        String item = items.get(0);
        if (size == 1) {
            queryString += R_REWARD_PART + " LIKE '" + item + "%'";
        }
        else {
            queryString += "(";
            for (int i = 0; i < size; i++) {
                item = items.get(i);
                if (i != 0) queryString += " OR ";
                queryString += R_REWARD_PART + " LIKE '" + item + "%'";
            }
            queryString += ")";
        }

        if (!showVaulted) queryString += " AND " + RELIC_VAULTED + " == 0";

        queryString += " ORDER BY " + RELIC_VAULTED + ", " +
                RELIC_ERA + " == '" + AXI + "', " +
                RELIC_ERA + " == '" + NEO + "', " +
                RELIC_ERA + " == '" + MESO + "', " +
                RELIC_ERA + " == '" + LITH + "', " +
                RELIC_NAME + ";";

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        Cursor cursor = relicDao.getRelicsCursor(query);
        if (cursor != null) {
            RelicComplete relic = null;
            String prev_id = "", id, era, name;
            boolean vaulted;

            String part_id, prime, component, type;
            int needed, rarity;

            int col_id = cursor.getColumnIndex(RELIC_ID),
                    col_era = cursor.getColumnIndex(RELIC_ERA),
                    col_name = cursor.getColumnIndex(RELIC_NAME),
                    col_vaulted = cursor.getColumnIndex(RELIC_VAULTED),
                    col_part_id = cursor.getColumnIndex(PART_ID),
                    col_prime = cursor.getColumnIndex(PART_PRIME),
                    col_component = cursor.getColumnIndex(PART_COMPONENT),
                    col_type = cursor.getColumnIndex(PRIME_TYPE),
                    col_needed = cursor.getColumnIndex(PART_NEEDED),
                    col_rarity = cursor.getColumnIndex(R_REWARD_RARITY);

            while (cursor.moveToNext()) {
                id = cursor.getString(col_id);
                if (!id.equals(prev_id)) {
                    era = cursor.getString(col_era);
                    name = cursor.getString(col_name);
                    vaulted = cursor.getInt(col_vaulted) > 0;

                    if (relic != null)
                        relics.add(relic);

                    relic = new RelicComplete(id, era, name, vaulted, 0);
                    prev_id = id;
                }

                part_id = cursor.getString(col_part_id);
                prime = cursor.getString(col_prime);
                component = cursor.getString(col_component);
                type = cursor.getString(col_type);
                needed = cursor.getInt(col_needed);
                rarity = cursor.getInt(col_rarity);

                relic.addNeededReward(
                        new RelicRewardComplete(id, rarity, part_id, prime, component,
                                type, needed, false, false)
                );
            }
            if (relic != null)
                relics.add(relic);

            cursor.close();
        }
        return relics;
    }

    public List<MissionComplete> getMissionsForItems(List<String> items, boolean bestPlaces) {
        if (items.isEmpty())
            return new ArrayList<>();

        String whereClause = "";
        int size = items.size();
        String item = items.get(0);
        if (size == 1) {
            whereClause += " WHERE " + R_REWARD_PART + " LIKE '" + item + "%'";
        }
        else {
            whereClause += " WHERE (";
            for (int i = 0; i < size; i++) {
                item = items.get(i);
                if (i != 0) whereClause += " OR ";
                whereClause += R_REWARD_PART + " LIKE '" + item + "%'";
            }
            whereClause += ")";
        }

        String queryString = "WITH SELECTED_REWARDS AS (" +
                    " SELECT " + M_REWARD_MISSION + ", " + M_REWARD_RELIC +
                    ", SUM(" + M_REWARD_DROP_CHANCE + ") AS " + M_REWARD_DROP_CHANCE + ", " + M_REWARD_ROTATION +
                    " FROM " + M_REWARD_TABLE +
                    " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                    " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                    whereClause +
                    " AND " + RELIC_VAULTED + " == 0" +
                    " GROUP BY " + M_REWARD_MISSION + ", " + M_REWARD_ROTATION +
                ") ";

        if (bestPlaces) {
            queryString += "SELECT " + MISSION_PLANET + ", " + MISSION_NAME + ", " + MISSION_OBJECTIVE + ", " + MISSION_FACTION + ", " +
                    MISSION_TYPE + ", " + M_REWARD_RELIC + ", dropChance, " + M_REWARD_ROTATION + ", " + M_REWARD_DROP_CHANCE +
                    " FROM " + MISSION_TABLE +
                    " LEFT JOIN (" +
                        " SELECT mission, MAX(dropChance) AS dropChance" +
                        " FROM (" +
                            " SELECT " + MISSION_NAME + " AS mission, " + MISSION_OBJECTIVE + " AS objective, " +
                            MISSION_TYPE + " AS type, SUM(dropChances) AS dropChance" +
                            " FROM " + MISSION_TABLE +
                            " LEFT JOIN (" +
                                " SELECT " + M_REWARD_MISSION + " AS mission, " +
                                " CASE " + M_REWARD_ROTATION +
                                    " WHEN 'Z' THEN " + M_REWARD_DROP_CHANCE +
                                    " WHEN 'A' THEN " + M_REWARD_DROP_CHANCE + "/2" +
                                    " WHEN 'B' THEN " + M_REWARD_DROP_CHANCE + "/4" +
                                    " WHEN 'C' THEN " + M_REWARD_DROP_CHANCE + "/4" +
                                    " ELSE 0" +
                                " END AS dropChances" +
                                " FROM SELECTED_REWARDS" +
                            ") ON mission == " + MISSION_NAME +
                            " WHERE dropChances != 0" +
                            " GROUP BY " + MISSION_NAME +
                            " ORDER BY dropChance DESC, " + MISSION_PLANET +
                        ") GROUP BY objective, type" +
                    ") ON mission == " + MISSION_NAME +
                    " JOIN SELECTED_REWARDS ON " + M_REWARD_MISSION + "==" + MISSION_NAME +
                    " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                    " WHERE dropChance != 0" +
                    " ORDER BY dropChance DESC, " +
                    MISSION_NAME + ", " +
                    M_REWARD_ROTATION;
        }
        else {
            queryString += "SELECT " + MISSION_PLANET + ", " + MISSION_NAME + ", " + MISSION_OBJECTIVE + ", " + MISSION_FACTION + ", " +
                    MISSION_TYPE + ", " + M_REWARD_RELIC + ", dropChance, " + M_REWARD_ROTATION + ", " + M_REWARD_DROP_CHANCE +
                    " FROM " + MISSION_TABLE +
                    " LEFT JOIN (" +
                        " SELECT " + MISSION_NAME + " AS mission, SUM(dropChances) AS dropChance" +
                        " FROM " + MISSION_TABLE +
                        " LEFT JOIN (" +
                            " SELECT " + M_REWARD_MISSION + " AS mission, " +
                            " CASE " + M_REWARD_ROTATION +
                                " WHEN 'Z' THEN " + M_REWARD_DROP_CHANCE +
                                " WHEN 'A' THEN " + M_REWARD_DROP_CHANCE + "/2" +
                                " WHEN 'B' THEN " + M_REWARD_DROP_CHANCE + "/4" +
                                " WHEN 'C' THEN " + M_REWARD_DROP_CHANCE + "/4" +
                                " ELSE 0" +
                            " END AS dropChances" +
                            " FROM SELECTED_REWARDS" +
                        ") ON mission == " + MISSION_NAME +
                        " WHERE dropChances != 0" +
                        " GROUP BY " + MISSION_NAME +
                        " ORDER BY dropChance DESC, " + MISSION_PLANET +
                    ") ON mission == " + MISSION_NAME +
                    " JOIN SELECTED_REWARDS ON " + M_REWARD_MISSION + "==" + MISSION_NAME +
                    " LEFT JOIN " + PLANET_TABLE + " ON " + PLANET_NAME + " == " + MISSION_PLANET +
                    " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                    " WHERE dropChance != 0" +
                    " ORDER BY dropChance DESC, " +
                    MISSION_NAME + ", " +
                    M_REWARD_ROTATION;
        }

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        Cursor cursor = missionDao.getMissions(query);
        List<MissionComplete> missions = new ArrayList<>();
        if (cursor != null) {
            MissionComplete mission = null;
            String mission_planet, mission_name, mission_objective, mission_faction;
            int mission_type;

            MissionReward reward;
            String reward_relic, reward_rotation;
            double reward_rarity;

            int col_planet = cursor.getColumnIndex(MISSION_PLANET),
                    col_mission = cursor.getColumnIndex(MISSION_NAME),
                    col_objective = cursor.getColumnIndex(MISSION_OBJECTIVE),
                    col_faction = cursor.getColumnIndex(MISSION_FACTION),
                    col_type = cursor.getColumnIndex(MISSION_TYPE),
                    col_relic = cursor.getColumnIndex(M_REWARD_RELIC),
                    col_rotation = cursor.getColumnIndex(M_REWARD_ROTATION),
                    col_rarity = cursor.getColumnIndex(M_REWARD_DROP_CHANCE);

            while (cursor.moveToNext()) {
                mission_name = cursor.getString(col_mission);
                if (mission == null || !mission.getName().equals(mission_name)) {
                    mission_planet = cursor.getString(col_planet);
                    mission_objective = cursor.getString(col_objective);
                    mission_faction = cursor.getString(col_faction);
                    mission_type = cursor.getInt(col_type);
                    mission = new MissionComplete(mission_name, mission_planet, mission_objective, mission_faction, mission_type);
                    missions.add(mission);
                }

                reward_relic = cursor.getString(col_relic);
                reward_rotation = cursor.getString(col_rotation);
                reward_rarity = cursor.getDouble(col_rarity);
                reward = new MissionReward(mission_name, reward_relic, reward_rotation, reward_rarity);
                mission.addMissionReward(reward);
            }
            cursor.close();
        }
        return missions;
    }

    public List<PrimeComplete> getRemainingPrimesFromDB() {
        List<String> alreadyAddedPrimes = getSelectedPrimeNames();
        String filter = dialogFilter.getValue();

        String queryString = "SELECT * FROM " + PRIME_TABLE +
                " LEFT JOIN " + USER_PRIME_TABLE + " ON " + USER_PRIME_NAME + " == " + PRIME_NAME +
                " WHERE 1";

        if (!filter.equals(""))
            queryString += " AND " + PRIME_TYPE + " == '" + filter + "'";

        if (!dialogSearch.equals(""))
            queryString += " AND " + PRIME_NAME + " LIKE '" + dialogSearch + "%'";

        if (!alreadyAddedPrimes.isEmpty()) {
            for (String name : alreadyAddedPrimes)
                queryString += " AND " + PRIME_NAME + " != '" + name + "'";
        }

        if (!dialogOrder.equals("")) {
            if (dialogOrder.equals(PRIME_TYPE))
                queryString += " ORDER BY " +
                        PRIME_TYPE + " == '" + MELEE + "', " +
                        PRIME_TYPE + " == '" + SECONDARY + "', " +
                        PRIME_TYPE + " == '" + PRIMARY + "', " +
                        PRIME_TYPE + " == '" + SENTINEL + "', " +
                        PRIME_TYPE + " == '" + PET + "', " +
                        PRIME_TYPE + " == '" + ARCHWING + "', " +
                        PRIME_TYPE + " == '" + WARFRAME + "', " +
                        PRIME_NAME;
            else
                queryString += " ORDER BY " + dialogOrder + ", " +
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

    public List<PartComplete> getRemainingPartsFromDB() {
        List<Item> alreadyAddedItems = items.getValue();
        String filter = dialogFilter.getValue();

        String queryString = "SELECT " + PART_ID + ", " + PART_PRIME + ", " + PART_COMPONENT + ", " + PART_NEEDED + ", " + PRIME_TYPE + ", " + PRIME_VAULTED + ", " + USER_PART_OWNED +
                " FROM " + PART_TABLE +
                " LEFT JOIN " + USER_PART_TABLE + " ON " + USER_PART_ID + " == " + PART_ID +
                " LEFT JOIN " + PRIME_TABLE + " ON " + PRIME_NAME + " == " + PART_PRIME +
                " WHERE 1";

        if (!filter.equals(""))
            queryString += " AND " + PRIME_TYPE + " == '" + filter + "'";

        if (!dialogSearch.equals("")) {
            queryString += " AND (" + PRIME_NAME + " LIKE '" + dialogSearch + "%'" +
                    " OR " + PART_ID + " LIKE '" + dialogSearch + "%'" +
                    " OR " + PART_COMPONENT + " LIKE '" + dialogSearch + "%')";
        }

        if (!alreadyAddedItems.isEmpty()) {
            for (Item item : alreadyAddedItems)
                queryString += " AND " + PART_ID + " NOT LIKE '" + item.getId() + "%'";
        }

        if (!dialogOrder.equals("")) {
            if (dialogOrder.equals(PRIME_TYPE))
                queryString += " ORDER BY " +
                        PRIME_TYPE + " == '" + MELEE + "', " +
                        PRIME_TYPE + " == '" + SECONDARY + "', " +
                        PRIME_TYPE + " == '" + PRIMARY + "', " +
                        PRIME_TYPE + " == '" + SENTINEL + "', " +
                        PRIME_TYPE + " == '" + PET + "', " +
                        PRIME_TYPE + " == '" + ARCHWING + "', " +
                        PRIME_TYPE + " == '" + WARFRAME + "', " +
                        PRIME_NAME + ", " +
                        PART_COMPONENT;
            else
                queryString += " ORDER BY " + dialogOrder + ", " +
                        PRIME_TYPE + " == '" + MELEE + "', " +
                        PRIME_TYPE + " == '" + SECONDARY + "', " +
                        PRIME_TYPE + " == '" + PRIMARY + "', " +
                        PRIME_TYPE + " == '" + SENTINEL + "', " +
                        PRIME_TYPE + " == '" + PET + "', " +
                        PRIME_TYPE + " == '" + ARCHWING + "', " +
                        PRIME_TYPE + " == '" + WARFRAME + "', " +
                        PRIME_NAME + ", " +
                        PART_COMPONENT;
        }

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return partDao.getParts(query);
    }
}
