package com.warframefarm.activities.details.prime;

import static com.warframefarm.data.WarframeConstants.AXI;
import static com.warframefarm.data.WarframeConstants.LITH;
import static com.warframefarm.data.WarframeConstants.MESO;
import static com.warframefarm.data.WarframeConstants.NEO;
import static com.warframefarm.database.WarframeFarmDatabase.BEST_PLACES;
import static com.warframefarm.database.WarframeFarmDatabase.DROP_CHANCE;
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
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ERA;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RARITY;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_TABLE;

import android.app.Application;
import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.AppExecutors;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.MissionDao;
import com.warframefarm.database.MissionReward;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.ComponentDao;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.database.PrimeDao;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.RelicDao;
import com.warframefarm.database.RelicRewardComplete;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class PrimeRepository {

    private final PrimeDao primeDao;
    private final ComponentDao componentDao;
    private final RelicDao relicDao;
    private final MissionDao missionDao;

    private final FirestoreHelper firestoreHelper;

    private final Executor backgroundThread, mainThread;

    private LiveData<PrimeComplete> prime = new MutableLiveData<>();

    public static final int COMPONENT = 0, RELIC = 1, MISSION = 2;
    private final List<String> relicFilterValues = new ArrayList<>(), missionFilterValues = new ArrayList<>();
    private final MutableLiveData<Integer> mode = new MutableLiveData<>(COMPONENT);
    private String search = "", relicFilter = COMPONENT_ID, missionFilter = BEST_PLACES;

    private LiveData<List<ComponentComplete>> components = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<RelicComplete>> relics = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<MissionComplete>> missions = new MutableLiveData<>(new ArrayList<>());

    public PrimeRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        primeDao = database.primeDao();
        componentDao = database.componentDao();
        relicDao = database.relicDao();
        missionDao = database.missionDao();

        firestoreHelper = FirestoreHelper.getInstance(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        Collections.addAll(relicFilterValues,
                COMPONENT_ID,
                RELIC_ID
        );

        Collections.addAll(missionFilterValues,
                BEST_PLACES,
                DROP_CHANCE,
                MISSION_OBJECTIVE,
                PLANET_NAME
        );
    }

    public LiveData<PrimeComplete> getPrime() {
        return prime;
    }

    public void setPrime(String primeName) {
        prime = primeDao.getPrime(primeName);
        components = componentDao.getComponentsOfPrimeLD(primeName);
    }

    public LiveData<Integer> getMode() {
        return mode;
    }

    public void setMode(int newMode) {
        if (mode.getValue() != newMode) {
            mode.setValue(newMode);
            updateResults();
        }
    }

    public void setSearch(String search) {
        this.search = search;
        updateResults();
    }

    public void setFilter(int filterPos) {
        int m = mode.getValue();
        if (m == RELIC)
            setRelicFilter(filterPos);
        else if (m == MISSION)
            setMissionFilter(filterPos);
    }

    public void setRelicFilter(int filterPos) {
        relicFilter = relicFilterValues.get(filterPos);
        updateRelics();
    }

    public int getRelicFilterPos() {
        int pos = relicFilterValues.indexOf(relicFilter);
        return pos == -1 ? 0 : pos;
    }

    public void setMissionFilter(int filterPos) {
        missionFilter = missionFilterValues.get(filterPos);
        updateMissions();
    }

    public int getMissionFilterPos() {
        int pos = missionFilterValues.indexOf(missionFilter);
        return pos == -1 ? 0 : pos;
    }

    public LiveData<List<ComponentComplete>> getComponents() {
        return components;
    }

    public LiveData<List<RelicComplete>> getRelics() {
        return relics;
    }

    public LiveData<List<MissionComplete>> getMissions() {
        return missions;
    }

    public void updateResults() {
        int m = mode.getValue();
        if (m == RELIC)
            updateRelics();
        else if (m == MISSION)
            updateMissions();
    }

    public void updateComponents() {
        /**
        backgroundThread.execute(() -> {
            PrimeComplete p = prime.getValue();
            if (p == null)
                return;

            List<PartComplete> partList = partDao.getPartsOfPrime(p.getName());

            mainThread.execute(() -> parts.setValue(partList));
        });*/
        PrimeComplete p = prime.getValue();
        if (p == null)
            return;

        components = componentDao.getComponentsOfPrimeLD(p.getName());
    }

    public void updateRelics() {
        backgroundThread.execute(() -> {
            PrimeComplete p = prime.getValue();
            if (p == null)
                return;

            List<RelicComplete> relicList = new ArrayList<>();

            String queryString = "SELECT *" +
                    " FROM " + RELIC_TABLE +
                    " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                    " LEFT JOIN " + COMPONENT_TABLE + " ON " + COMPONENT_ID + " == " + R_REWARD_COMPONENT +
                    " LEFT JOIN " + PRIME_TABLE + " ON " + PRIME_NAME + " == " + COMPONENT_PRIME +
                    " WHERE " + R_REWARD_COMPONENT + " LIKE '" + p.getName() + "%'";

            if (!search.equals(""))
                queryString += " AND (" + R_REWARD_COMPONENT + " LIKE '" + p.getName() + " " + search + "%'" +
                        " OR " + RELIC_ERA + " LIKE '" + search + "%'" +
                        " OR " + RELIC_NAME + " LIKE '" + search + "%')";

            if (relicFilter.equals(COMPONENT_ID)) {
                queryString += " ORDER BY " + RELIC_VAULTED + ", " +
                        R_REWARD_COMPONENT + ", " +
                        RELIC_ERA + " == '" + AXI + "', " +
                        RELIC_ERA + " == '" + NEO + "', " +
                        RELIC_ERA + " == '" + MESO + "', " +
                        RELIC_ERA + " == '" + LITH + "', " +
                        R_REWARD_RARITY + ", " +
                        RELIC_NAME;
            }
            else {
                queryString += " ORDER BY " + RELIC_VAULTED + ", " +
                        RELIC_ERA + " == '" + AXI + "', " +
                        RELIC_ERA + " == '" + NEO + "', " +
                        RELIC_ERA + " == '" + MESO + "', " +
                        RELIC_ERA + " == '" + LITH + "', " +
                        R_REWARD_RARITY + ", " +
                        RELIC_NAME;
            }

            SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
            Cursor cursor = relicDao.getRelicsCursor(query);
            if (cursor != null) {
                RelicComplete relic = null;
                String prev_id = "", id, era, name;
                boolean vaulted;

                String component_id, prime, component, type;
                int needed, rarity;

                int col_id = cursor.getColumnIndex(RELIC_ID),
                        col_era = cursor.getColumnIndex(RELIC_ERA),
                        col_name = cursor.getColumnIndex(RELIC_NAME),
                        col_vaulted = cursor.getColumnIndex(RELIC_VAULTED),
                        col_component_id = cursor.getColumnIndex(COMPONENT_ID),
                        col_prime = cursor.getColumnIndex(COMPONENT_PRIME),
                        col_component = cursor.getColumnIndex(COMPONENT_TYPE),
                        col_type = cursor.getColumnIndex(PRIME_TYPE),
                        col_needed = cursor.getColumnIndex(COMPONENT_NEEDED),
                        col_rarity = cursor.getColumnIndex(R_REWARD_RARITY);

                while (cursor.moveToNext()) {
                    id = cursor.getString(col_id);
                    if (!id.equals(prev_id)) {
                        era = cursor.getString(col_era);
                        name = cursor.getString(col_name);
                        vaulted = cursor.getInt(col_vaulted) > 0;

                        if (relic != null)
                            relicList.add(relic);

                        relic = new RelicComplete(id, era, name, vaulted, 0);
                        prev_id = id;
                    }

                    component_id = cursor.getString(col_component_id);
                    prime = cursor.getString(col_prime);
                    component = cursor.getString(col_component);
                    type = cursor.getString(col_type);
                    needed = cursor.getInt(col_needed);
                    rarity = cursor.getInt(col_rarity);

                    relic.addNeededReward(
                            new RelicRewardComplete(id, rarity, component_id, prime, component,
                                    type, needed, false, false)
                    );
                }
                if (relic != null)
                    relicList.add(relic);

                cursor.close();
            }

            mainThread.execute(() -> relics.setValue(relicList));
        });
    }

    public void updateMissions() {
        backgroundThread.execute(() -> {
            PrimeComplete p = prime.getValue();
            if (p == null)
                return;

            String queryString = "WITH SELECTED_REWARDS AS (" +
                        " SELECT " + M_REWARD_MISSION + ", " + M_REWARD_RELIC +
                        ", SUM(" + M_REWARD_DROP_CHANCE + ") AS " + M_REWARD_DROP_CHANCE + ", " + M_REWARD_ROTATION +
                        " FROM " + M_REWARD_TABLE +
                        " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                        " LEFT JOIN " + MISSION_TABLE + " ON " + MISSION_NAME + " == " + M_REWARD_MISSION +
                        " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                        " WHERE " + R_REWARD_COMPONENT + " LIKE '" + p.getName() + "%'" +
                        " AND " + RELIC_VAULTED + " == 0" +
                        (search.isEmpty() ?
                            "" :
                            " AND (" + RELIC_ERA + " LIKE \"" + search + "%\"" +
                                " OR " + RELIC_NAME + " LIKE \"" + search + "%\"" +
                                " OR " + MISSION_NAME + " LIKE \"" + search + "%\"" +
                                " OR " + MISSION_PLANET + " LIKE \"" + search + "%\"" +
                                " OR " + MISSION_FACTION + " LIKE \"" + search + "%\"" +
                                " OR " + MISSION_OBJECTIVE + " LIKE \"" + search + "%\"" +
                            ")"
                        ) +
                        " GROUP BY " + M_REWARD_MISSION + ", " + M_REWARD_ROTATION +
                    "), MISSION_DROP_CHANCE AS (" +
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
                    ")";

            if (missionFilter.equals(BEST_PLACES)) {
                queryString += "SELECT " + MISSION_PLANET + ", " + MISSION_NAME + ", " + MISSION_OBJECTIVE + ", " + MISSION_FACTION + ", " +
                        MISSION_TYPE + ", " + M_REWARD_RELIC + ", dropChance, " + M_REWARD_ROTATION + ", " + M_REWARD_DROP_CHANCE +
                        " FROM " + MISSION_TABLE +
                        " LEFT JOIN (" +
                            " SELECT mission, dropChance" +
                            " FROM MISSION_DROP_CHANCE" +
                            " JOIN (" +
                                " SELECT objective AS obj, MAX(dropChance) AS max" +
                                " FROM MISSION_DROP_CHANCE" +
                                " GROUP BY obj" +
                            ") AS MISSION_MAX ON dropCHANCE == max AND objective == obj" +
                        ") ON mission == " + MISSION_NAME +
                        " JOIN SELECTED_REWARDS ON " + M_REWARD_MISSION + "==" + MISSION_NAME +
                        " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                        " WHERE dropChance != 0" +
                        " ORDER BY dropChance DESC, " + MISSION_NAME + ", " + M_REWARD_ROTATION;
            }
            else {
                queryString += "SELECT " + MISSION_PLANET + ", " + MISSION_NAME + ", " + MISSION_OBJECTIVE + ", " + MISSION_FACTION + ", " +
                        MISSION_TYPE + ", " + M_REWARD_RELIC + ", dropChance, " + M_REWARD_ROTATION + ", " + M_REWARD_DROP_CHANCE +
                        " FROM " + MISSION_TABLE +
                        " LEFT JOIN MISSION_DROP_CHANCE ON mission == " + MISSION_NAME +
                        " JOIN SELECTED_REWARDS ON " + M_REWARD_MISSION + "==" + MISSION_NAME +
                        " LEFT JOIN " + PLANET_TABLE + " ON " + PLANET_NAME + " == " + MISSION_PLANET +
                        " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                        " WHERE dropChance != 0" +
                        " ORDER BY ";

                if (missionFilter.equals(MISSION_OBJECTIVE) || missionFilter.equals(PLANET_NAME))
                    queryString += missionFilter + ", dropChance DESC, " + MISSION_NAME + ", " + M_REWARD_ROTATION;
                else queryString += "dropChance DESC, " + MISSION_NAME + ", " + M_REWARD_ROTATION;
            }

            SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
            Cursor cursor = missionDao.getMissions(query);
            List<MissionComplete> missionList = new ArrayList<>();
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
                        missionList.add(mission);
                    }

                    reward_relic = cursor.getString(col_relic);
                    reward_rotation = cursor.getString(col_rotation);
                    reward_rarity = cursor.getDouble(col_rarity);
                    reward = new MissionReward(mission_name, reward_relic, reward_rotation, reward_rarity);
                    mission.addMissionReward(reward);
                }
                cursor.close();
            }

            mainThread.execute(() -> missions.setValue(missionList));
        });
    }

    public void switchPrimeOwned() {
        backgroundThread.execute(() -> {
            PrimeComplete prime = this.prime.getValue();
            if (prime == null)
                return;
            firestoreHelper.setPrimeOwned(prime.getName(), !prime.isOwned());
        });
    }

    public void switchComponentOwned(ComponentComplete component) {
        backgroundThread.execute(() -> firestoreHelper.setComponentOwned(component.getId(), component.getPrime(), !component.isOwned()));
    }
}
