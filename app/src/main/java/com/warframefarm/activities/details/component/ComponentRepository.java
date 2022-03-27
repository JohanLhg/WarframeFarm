package com.warframefarm.activities.details.component;

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
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ERA;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_TABLE;

import android.app.Application;
import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.AppExecutors;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.MissionDao;
import com.warframefarm.database.MissionReward;
import com.warframefarm.database.ComponentDao;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.RelicDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class ComponentRepository {

    private final ComponentDao componentDao;
    private final RelicDao relicDao;
    private final MissionDao missionDao;

    private final FirestoreHelper firestoreHelper;

    private final Executor backgroundThread, mainThread;

    private LiveData<ComponentComplete> component = new MutableLiveData<>();

    public static final int RELIC = 0, MISSION = 1;
    private final List<String> filterValues = new ArrayList<>();
    private final MutableLiveData<Integer> mode = new MutableLiveData<>(RELIC);
    private String search = "", filter = BEST_PLACES;

    private final MutableLiveData<LiveData<List<RelicComplete>>> relics = new MutableLiveData<>();
    private final MutableLiveData<List<MissionComplete>> missions = new MutableLiveData<>(new ArrayList<>());

    public ComponentRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        componentDao = database.componentDao();
        relicDao = database.relicDao();
        missionDao = database.missionDao();

        firestoreHelper = FirestoreHelper.getInstance(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        Collections.addAll(filterValues,
                BEST_PLACES,
                DROP_CHANCE,
                MISSION_OBJECTIVE,
                PLANET_NAME
        );
    }

    public LiveData<ComponentComplete> getComponent() {
        return component;
    }

    public void setComponent(String id) {
        component = componentDao.getComponent(id);
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
        filter = filterValues.get(filterPos);
        if (mode.getValue() == MISSION)
            updateMissions();
    }

    public LiveData<LiveData<List<RelicComplete>>> getRelics() {
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

    public void updateRelics() {
        backgroundThread.execute(() -> {
            ComponentComplete p = component.getValue();
            if (p == null)
                return;

            String queryString = "SELECT *" +
                    " FROM " + RELIC_TABLE +
                    " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                    " LEFT JOIN " + COMPONENT_TABLE + " ON " + COMPONENT_ID + " == " + R_REWARD_COMPONENT +
                    " LEFT JOIN " + PRIME_TABLE + " ON " + PRIME_NAME + " == " + COMPONENT_PRIME +
                    " WHERE " + R_REWARD_COMPONENT + " == '" + p.getId() + "'";

            if (!search.equals(""))
                queryString += " AND (" + RELIC_ERA + " LIKE '" + search + "%'" +
                        " OR " + RELIC_NAME + " LIKE '" + search + "%')";

            SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
            LiveData<List<RelicComplete>> relicList = relicDao.getRelics(query);

            mainThread.execute(() -> relics.setValue(relicList));
        });
    }

    public void updateMissions() {
        backgroundThread.execute(() -> {
            ComponentComplete p = component.getValue();
            if (p == null)
                return;

            String queryString = "WITH SELECTED_REWARDS AS (" +
                        " SELECT " + M_REWARD_MISSION + ", " + M_REWARD_RELIC +
                        ", SUM(" + M_REWARD_DROP_CHANCE + ") AS " + M_REWARD_DROP_CHANCE + ", " + M_REWARD_ROTATION +
                        " FROM " + M_REWARD_TABLE +
                        " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                        " LEFT JOIN " + MISSION_TABLE + " ON " + MISSION_NAME + " == " + M_REWARD_MISSION +
                        " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                        " WHERE " + R_REWARD_COMPONENT + " LIKE '" + p.getId() + "%'" +
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

            if (filter.equals(BEST_PLACES)) {
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

                if (filter.equals(MISSION_OBJECTIVE) || filter.equals(PLANET_NAME))
                    queryString += filter + ", dropChance DESC, " + MISSION_NAME + ", " + M_REWARD_ROTATION;
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

    public void switchOwned() {
        backgroundThread.execute(() -> {
            ComponentComplete component = this.component.getValue();
            if (component == null)
                return;

            firestoreHelper.setComponentOwned(component.getId(), component.getPrime(), !component.isOwned());
        });
    }
}
