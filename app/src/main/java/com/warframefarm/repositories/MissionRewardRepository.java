package com.warframefarm.repositories;

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

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.database.BountyRewardComplete;
import com.warframefarm.database.BountyRewardDao;
import com.warframefarm.database.CacheRewardComplete;
import com.warframefarm.database.CacheRewardDao;
import com.warframefarm.database.MissionRewardComplete;
import com.warframefarm.database.MissionRewardDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.List;

public class MissionRewardRepository {

    private final MissionRewardDao missionRewardDao;
    private final CacheRewardDao cacheRewardDao;
    private final BountyRewardDao bountyRewardDao;

    public MissionRewardRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        missionRewardDao = database.missionRewardDao();
        cacheRewardDao = database.cacheRewardDao();
        bountyRewardDao = database.bountyRewardDao();
    }

    public List<MissionRewardComplete> getMissionRewards(String name, boolean filter, String search) {
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
                    " WHERE " + M_REWARD_MISSION + " == \"" + name + "\"" +
                    " AND (" + USER_COMPONENT_OWNED + " == 0" +
                    " OR " + USER_COMPONENT_OWNED + " IS NULL)" +
                    " GROUP BY relic" +
                ") ON relic == " + RELIC_ID +
                " WHERE " + M_REWARD_MISSION + " == \"" + name + "\"";

        if (!search.equals("")) {
            queryString += " AND (" + R_REWARD_COMPONENT + " LIKE '" + search + "%'" +
                        " OR " + RELIC_ERA + " LIKE '" + search + "%'" +
                        " OR " + RELIC_NAME + " LIKE '" + search + "%'" +
                    ")";
        }

        if (filter) queryString += " AND " + neededRarity + " > 0";

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
        return missionRewardDao.getMissionRewards(query);
    }

    public List<CacheRewardComplete> getCacheRewards(String mission, boolean filter, String search) {
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
                    " WHERE " + C_REWARD_MISSION + " == \"" + mission + "\"" +
                    " AND (" + USER_COMPONENT_OWNED + " == 0" +
                    " OR " + USER_COMPONENT_OWNED + " IS NULL)" +
                    " GROUP BY relic" +
                ") ON relic == " + RELIC_ID +
                " WHERE " + C_REWARD_MISSION + " == \"" + mission + "\"";

        if (!search.equals("")) {
            queryString += " AND (" + R_REWARD_COMPONENT + " LIKE '" + search + "%'" +
                        " OR " + RELIC_ERA + " LIKE '" + search + "%'" +
                        " OR " + RELIC_NAME + " LIKE '" + search + "%'" +
                    ")";
        }

        if (filter) queryString += " AND " + neededRarity + " > 0";

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
        return cacheRewardDao.getCacheRewards(query);
    }

    public List<BountyRewardComplete> getBountyRewards(String mission, boolean filter, String search) {
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
                    " WHERE " + B_REWARD_MISSION + " == \"" + mission + "\"" +
                    " AND (" + USER_COMPONENT_OWNED + " == 0" +
                    " OR " + USER_COMPONENT_OWNED + " IS NULL)" +
                    " GROUP BY relic" +
                ") ON relic == " + RELIC_ID +
                " WHERE " + B_REWARD_MISSION + " == \"" + mission + "\"";

        if (!search.equals("")) {
            queryString += " AND (" + R_REWARD_COMPONENT + " LIKE '" + search + "%'" +
                        " OR " + RELIC_ERA + " LIKE '" + search + "%'" +
                        " OR " + RELIC_NAME + " LIKE '" + search + "%'" +
                    ")";
        }

        if (filter) queryString += " AND " + neededRarity + " > 0";

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

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return bountyRewardDao.getBountyRewards(query);
    }
}
