package com.warframefarm.activities.details.mission;

import static com.warframefarm.data.WarframeConstants.AXI;
import static com.warframefarm.data.WarframeConstants.COMMON;
import static com.warframefarm.data.WarframeConstants.LITH;
import static com.warframefarm.data.WarframeConstants.MESO;
import static com.warframefarm.data.WarframeConstants.NEO;
import static com.warframefarm.data.WarframeConstants.RARE;
import static com.warframefarm.data.WarframeConstants.UNCOMMON;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_ID;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_LEVEL;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_ROTATION;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_STAGE;
import static com.warframefarm.database.WarframeFarmDatabase.B_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_ID;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_ROTATION;
import static com.warframefarm.database.WarframeFarmDatabase.C_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_ID;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_ROTATION;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ERA;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RARITY;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_TABLE;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.BountyRewardComplete;
import com.warframefarm.database.BountyRewardDao;
import com.warframefarm.database.CacheRewardComplete;
import com.warframefarm.database.CacheRewardDao;
import com.warframefarm.database.MissionDao;
import com.warframefarm.database.MissionRewardComplete;
import com.warframefarm.database.MissionRewardDao;
import com.warframefarm.database.MissionWithRewardTypes;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MissionRepository {

    private final MissionDao missionDao;
    private final MissionRewardDao missionRewardDao;
    private final BountyRewardDao bountyRewardDao;
    private final CacheRewardDao cacheRewardDao;

    private final Executor backgroundThread, mainThread;

    private final MutableLiveData<MissionWithRewardTypes> mission = new MutableLiveData<>();

    public static final int REWARDS = 0, BOUNTIES = 1, CACHES = 2;
    private final MutableLiveData<Integer> mode = new MutableLiveData<>(REWARDS);

    private String search = "";
    private final MutableLiveData<Boolean> filter = new MutableLiveData<>(false);

    private final MutableLiveData<List<MissionRewardComplete>> missionRewards = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<BountyRewardComplete>> bountyRewards = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<CacheRewardComplete>> cacheRewards = new MutableLiveData<>(new ArrayList<>());

    public MissionRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        missionDao = database.missionDao();
        missionRewardDao = database.missionRewardDao();
        bountyRewardDao = database.bountyRewardDao();
        cacheRewardDao = database.cacheRewardDao();

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();
    }

    public void setMission(String name) {
        backgroundThread.execute(() -> {
            MissionWithRewardTypes m = missionDao.getMission(name);
            int rewardType;
            if (m.hasRewards())
                rewardType = REWARDS;
            else if (m.hasBounties())
                rewardType = BOUNTIES;
            else if (m.hasCaches())
                rewardType = CACHES;
            else rewardType = REWARDS;
            mainThread.execute(() -> {
                mission.setValue(m);
                setMode(rewardType);
                updateRewards();
            });
        });
    }

    public LiveData<MissionWithRewardTypes> getMission() {
        return mission;
    }

    public void setMode(int mode) {
        if (this.mode.getValue() != mode) {
            this.mode.setValue(mode);
            updateRewards();
        }
    }

    public LiveData<Integer> getMode() {
        return mode;
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

    public LiveData<List<MissionRewardComplete>> getMissionRewards() {
        return missionRewards;
    }

    public LiveData<List<BountyRewardComplete>> getBountyRewards() {
        return bountyRewards;
    }

    public LiveData<List<CacheRewardComplete>> getCacheRewards() {
        return cacheRewards;
    }

    public void updateRewards() {
        switch (mode.getValue()) {
            case BOUNTIES: updateBountyRewards(); break;
            case CACHES: updateCacheRewards(); break;
            default: updateMissionRewards(); break;
        }
    }

    public void updateMissionRewards() {
        backgroundThread.execute(() -> {
            MissionWithRewardTypes m = mission.getValue();
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
                        " INNER JOIN " + COMPONENT_TABLE + " ON " + COMPONENT_ID + " == " + R_REWARD_COMPONENT +
                        " LEFT JOIN " + USER_COMPONENT_TABLE + " ON " + USER_COMPONENT_ID + " == " + COMPONENT_ID +
                        " WHERE " + M_REWARD_MISSION + " == \"" + m.getName() + "\"" +
                        " AND (" + USER_COMPONENT_OWNED + " == 0" +
                        " OR " + USER_COMPONENT_OWNED + " IS NULL)" +
                        " GROUP BY relic" +
                    ") ON relic == " + RELIC_ID +
                    " WHERE " + M_REWARD_MISSION + " == \"" + m.getName() + "\"";

            if (!search.equals("")) {
                queryString += " AND (" + R_REWARD_COMPONENT + " LIKE '" + search + "%'" +
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

            mainThread.execute(() -> missionRewards.setValue(rewardList));
        });
    }

    public void updateBountyRewards() {
        backgroundThread.execute(() -> {
            MissionWithRewardTypes m = mission.getValue();
            if (m == null)
                return;

            String neededRarity = "neededRarity";

            String queryString = "SELECT " + B_REWARD_ID + ", " + B_REWARD_MISSION + ", " +
                    B_REWARD_LEVEL + ", " + B_REWARD_STAGE + ", " + B_REWARD_ROTATION + ", " +
                    B_REWARD_DROP_CHANCE + ", " + RELIC_ID + ", " + RELIC_ERA + ", " + RELIC_NAME +
                    ", COALESCE(" + neededRarity + ", 0) AS " + R_REWARD_RARITY +
                    " FROM " + B_REWARD_TABLE +
                    " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + B_REWARD_RELIC +
                    " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                    " LEFT JOIN (" +
                        " SELECT " + RELIC_ID + " AS relic, MAX(" + R_REWARD_RARITY + ") AS " + neededRarity +
                        " FROM " + B_REWARD_TABLE +
                        " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + B_REWARD_RELIC +
                        " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                        " INNER JOIN " + COMPONENT_TABLE + " ON " + COMPONENT_ID + " == " + R_REWARD_COMPONENT +
                        " LEFT JOIN " + USER_COMPONENT_TABLE + " ON " + USER_COMPONENT_ID + " == " + COMPONENT_ID +
                        " WHERE " + B_REWARD_MISSION + " == \"" + m.getName() + "\"" +
                        " AND (" + USER_COMPONENT_OWNED + " == 0" +
                        " OR " + USER_COMPONENT_OWNED + " IS NULL)" +
                        " GROUP BY relic" +
                    ") ON relic == " + RELIC_ID +
                    " WHERE " + B_REWARD_MISSION + " == \"" + m.getName() + "\"";

            if (!search.equals("")) {
                queryString += " AND (" + R_REWARD_COMPONENT + " LIKE '" + search + "%'" +
                        " OR " + RELIC_ERA + " LIKE '" + search + "%'" +
                        " OR " + RELIC_NAME + " LIKE '" + search + "%'" +
                        ")";
            }

            if (filter.getValue()) queryString += " AND " + neededRarity + " > 0";

            queryString += " GROUP BY " + B_REWARD_RELIC + ", " + B_REWARD_ROTATION +
                    " ORDER BY " + B_REWARD_LEVEL + ", " +
                    B_REWARD_ROTATION + ", " +
                    B_REWARD_STAGE + " LIKE 'F%', " +
                    B_REWARD_STAGE + ", " +
                    neededRarity + " desc, " +
                    RELIC_ERA + " == '" + AXI + "', " +
                    RELIC_ERA + " == '" + NEO + "', " +
                    RELIC_ERA + " == '" + MESO + "', " +
                    RELIC_ERA + " == '" + LITH + "', " +
                    neededRarity + " == '" + RARE + "', " +
                    neededRarity + " == '" + UNCOMMON + "', " +
                    neededRarity + " == '" + COMMON + "'";

            System.out.println(queryString);
            SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
            List<BountyRewardComplete> rewardList = bountyRewardDao.getBountyRewards(query);

            mainThread.execute(() -> bountyRewards.setValue(rewardList));
        });
    }

    public void updateCacheRewards() {
        backgroundThread.execute(() -> {
            MissionWithRewardTypes m = mission.getValue();
            if (m == null)
                return;

            String neededRarity = "neededRarity";

            String queryString = "SELECT " + C_REWARD_ID + ", " + C_REWARD_MISSION + ", " + C_REWARD_ROTATION + ", " + C_REWARD_DROP_CHANCE +
                    ", " + RELIC_ID + ", " + RELIC_ERA + ", " + RELIC_NAME + ", COALESCE(" + neededRarity + ", 0) AS " + R_REWARD_RARITY +
                    " FROM " + C_REWARD_TABLE +
                    " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + C_REWARD_RELIC +
                    " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                    " LEFT JOIN (" +
                        " SELECT " + RELIC_ID + " AS relic, MAX(" + R_REWARD_RARITY + ") AS " + neededRarity +
                        " FROM " + C_REWARD_TABLE +
                        " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + C_REWARD_RELIC +
                        " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                        " INNER JOIN " + COMPONENT_TABLE + " ON " + COMPONENT_ID + " == " + R_REWARD_COMPONENT +
                        " LEFT JOIN " + USER_COMPONENT_TABLE + " ON " + USER_COMPONENT_ID + " == " + COMPONENT_ID +
                        " WHERE " + C_REWARD_MISSION + " == \"" + m.getName() + "\"" +
                        " AND (" + USER_COMPONENT_OWNED + " == 0" +
                        " OR " + USER_COMPONENT_OWNED + " IS NULL)" +
                        " GROUP BY relic" +
                    ") ON relic == " + RELIC_ID +
                    " WHERE " + C_REWARD_MISSION + " == \"" + m.getName() + "\"";

            if (!search.equals("")) {
                queryString += " AND (" + R_REWARD_COMPONENT + " LIKE '" + search + "%'" +
                        " OR " + RELIC_ERA + " LIKE '" + search + "%'" +
                        " OR " + RELIC_NAME + " LIKE '" + search + "%'" +
                        ")";
            }

            if (filter.getValue()) queryString += " AND " + neededRarity + " > 0";

            queryString += " GROUP BY " + C_REWARD_RELIC + ", " + C_REWARD_ROTATION +
                    " ORDER BY " + C_REWARD_ROTATION + ", " +
                    neededRarity + " desc, " +
                    RELIC_ERA + " == '" + AXI + "', " +
                    RELIC_ERA + " == '" + NEO + "', " +
                    RELIC_ERA + " == '" + MESO + "', " +
                    RELIC_ERA + " == '" + LITH + "', " +
                    neededRarity + " == '" + RARE + "', " +
                    neededRarity + " == '" + UNCOMMON + "', " +
                    neededRarity + " == '" + COMMON + "'";

            SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
            List<CacheRewardComplete> rewardList = cacheRewardDao.getCacheRewards(query);

            mainThread.execute(() -> cacheRewards.setValue(rewardList));
        });
    }
}
