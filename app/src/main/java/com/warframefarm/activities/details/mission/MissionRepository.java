package com.warframefarm.activities.details.mission;

import static com.warframefarm.data.WarframeConstants.AXI;
import static com.warframefarm.data.WarframeConstants.COMMON;
import static com.warframefarm.data.WarframeConstants.LITH;
import static com.warframefarm.data.WarframeConstants.MESO;
import static com.warframefarm.data.WarframeConstants.NEO;
import static com.warframefarm.data.WarframeConstants.RARE;
import static com.warframefarm.data.WarframeConstants.UNCOMMON;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_ID;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_ROTATION;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PART_ID;
import static com.warframefarm.database.WarframeFarmDatabase.PART_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ERA;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_PART;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RARITY;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_ID;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_TABLE;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.MissionDao;
import com.warframefarm.database.MissionRewardComplete;
import com.warframefarm.database.MissionRewardDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MissionRepository {

    private final MissionDao missionDao;
    private final MissionRewardDao missionRewardDao;

    private final Executor backgroundThread, mainThread;

    private final MutableLiveData<MissionComplete> mission = new MutableLiveData<>();

    private String search = "";
    private final MutableLiveData<Boolean> filter = new MutableLiveData<>(false);

    private final MutableLiveData<List<MissionRewardComplete>> rewards = new MutableLiveData<>(new ArrayList<>());

    public MissionRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        missionDao = database.missionDao();
        missionRewardDao = database.missionRewardDao();

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();
    }

    public void setMission(String name) {
        backgroundThread.execute(() -> {
            MissionComplete m = missionDao.getMission(name);
            mainThread.execute(() -> {
                mission.setValue(m);
                updateRewards();
            });
        });
    }

    public LiveData<MissionComplete> getMission() {
        return mission;
    }

    public void setSearch(String search) {
        this.search = search;
        updateRewards();
    }

    public void switchFilter() {
        filter.setValue(!filter.getValue());
        updateRewards();
    }

    public LiveData<Boolean> getFilter() {
        return filter;
    }

    public MutableLiveData<List<MissionRewardComplete>> getRewards() {
        return rewards;
    }

    public void updateRewards() {
        backgroundThread.execute(() -> {
            MissionComplete m = mission.getValue();
            if (m == null)
                return;

            String neededRarity = "neededRarity";

            String queryString = "SELECT " + M_REWARD_ID + ", " + M_REWARD_MISSION + ", " + M_REWARD_ROTATION + ", " + M_REWARD_DROP_CHANCE +
                    ", " + RELIC_ID + ", " + RELIC_ERA + ", " + RELIC_NAME + ", COALESCE(" + neededRarity + ", 0) AS " + R_REWARD_RARITY +
                    " FROM " + M_REWARD_TABLE +
                    " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                    " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                    " LEFT JOIN (" +
                    " SELECT " + RELIC_ID + " AS relic, MAX(" + R_REWARD_RARITY + ") AS " + neededRarity +
                    " FROM " + M_REWARD_TABLE +
                    " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                    " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                    " INNER JOIN " + PART_TABLE + " ON " + PART_ID + " == " + R_REWARD_PART +
                    " LEFT JOIN " + USER_PART_TABLE + " ON " + USER_PART_ID + " == " + PART_ID +
                    " WHERE " + M_REWARD_MISSION + " == \"" + m.getName() + "\"" +
                    " AND (" + USER_PART_OWNED + " == 0" +
                    " OR " + USER_PART_OWNED + " IS NULL)" +
                    " GROUP BY relic" +
                    ") ON relic == " + RELIC_ID +
                    " WHERE " + M_REWARD_MISSION + " == \"" + m.getName() + "\"";

            if (!search.equals("")) {
                queryString += " AND (" + R_REWARD_PART + " LIKE '" + search + "%'" +
                        " OR " + RELIC_ERA + " LIKE '" + search + "%'" +
                        " OR " + RELIC_NAME + " LIKE '" + search + "%'" +
                        ")";
            }

            if (filter.getValue()) queryString += " AND " + neededRarity + " > 0";

            queryString += " GROUP BY " + M_REWARD_RELIC + ", " + M_REWARD_ROTATION +
                    " ORDER BY " + M_REWARD_ROTATION + ", " +
                    neededRarity + " desc, " +
                    RELIC_ERA + " == '" + AXI + "', " +
                    RELIC_ERA + " == '" + NEO + "', " +
                    RELIC_ERA + " == '" + MESO + "', " +
                    RELIC_ERA + " == '" + LITH + "', " +
                    neededRarity + " == '" + RARE + "', " +
                    neededRarity + " == '" + UNCOMMON + "', " +
                    neededRarity + " == '" + COMMON + "'";

            SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
            List<MissionRewardComplete> rewardList = missionRewardDao.getMissionRewards(query);

            mainThread.execute(() -> rewards.setValue(rewardList));
        });
    }
}
