package com.warframefarm.activities.details.relic;

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
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_TABLE;

import android.app.Application;
import android.database.Cursor;

import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.MissionDao;
import com.warframefarm.database.MissionReward;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.RelicDao;
import com.warframefarm.database.RelicRewardComplete;
import com.warframefarm.database.RelicRewardDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class RelicRepository {

    private final RelicDao relicDao;
    private final RelicRewardDao relicRewardDao;
    private final MissionDao missionDao;

    private final Executor backgroundThread, mainThread;

    private final MutableLiveData<RelicComplete> relic = new MutableLiveData<>();

    public static final int REWARD = 0, MISSION = 1;
    private final MutableLiveData<Integer> mode = new MutableLiveData<>(REWARD);
    private String search = "", filter = BEST_PLACES;
    private final List<String> filterValues = new ArrayList<>();

    private final MutableLiveData<List<RelicRewardComplete>> rewards = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<MissionComplete>> missions = new MutableLiveData<>(new ArrayList<>());

    public RelicRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        relicDao = database.relicDao();
        relicRewardDao = database.relicRewardDao();
        missionDao = database.missionDao();

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

    public void setRelic(String id) {
        backgroundThread.execute(() -> {
            RelicComplete r = relicDao.getRelic(id);
            mainThread.execute(() -> {
                relic.setValue(r);
                updateRewards();
            });
        });
    }

    public MutableLiveData<RelicComplete> getRelic() {
        return relic;
    }

    public void setMode(int newMode) {
        if (mode.getValue() != newMode) {
            mode.setValue(newMode);
            if (newMode == MISSION)
                updateMissions();
        }
    }

    public MutableLiveData<Integer> getMode() {
        return mode;
    }

    public void setSearch(String search) {
        this.search = search;
        if (mode.getValue() == MISSION)
            updateMissions();
    }

    public void setFilter(int filterPos) {
        filter = filterValues.get(filterPos);
        if (mode.getValue() == MISSION)
            updateMissions();
    }

    public MutableLiveData<List<RelicRewardComplete>> getRewards() {
        return rewards;
    }

    public MutableLiveData<List<MissionComplete>> getMissions() {
        return missions;
    }

    public void updateRewards() {
        backgroundThread.execute(() -> {
            RelicComplete r = relic.getValue();
            if (r == null)
                return;

            List<RelicRewardComplete> rewardList = relicRewardDao.getRewardsForRelic(r.getId());

            mainThread.execute(() -> rewards.setValue(rewardList));
        });
    }

    public void updateMissions() {
        backgroundThread.execute(() -> {
            RelicComplete r = relic.getValue();
            if (r == null)
                return;

            String queryString = "WITH SELECTED_REWARDS AS (" +
                        " SELECT " + M_REWARD_MISSION + ", " + M_REWARD_RELIC +
                        ", SUM(" + M_REWARD_DROP_CHANCE + ") AS " + M_REWARD_DROP_CHANCE + ", " + M_REWARD_ROTATION +
                        " FROM " + M_REWARD_TABLE +
                        " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                        " LEFT JOIN " + MISSION_TABLE + " ON " + MISSION_NAME + " == " + M_REWARD_MISSION +
                        " WHERE " + RELIC_ID + " == '" + r.getId() + "'" +
                        (search.equals("") ?
                            "" :
                            " AND (" + MISSION_NAME + " LIKE \"" + search + "%\"" +
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
}
