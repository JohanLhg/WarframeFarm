package com.warframefarm.repositories;

import static com.warframefarm.data.WarframeConstants.AXI;
import static com.warframefarm.data.WarframeConstants.LITH;
import static com.warframefarm.data.WarframeConstants.MESO;
import static com.warframefarm.data.WarframeConstants.NEO;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_PRIME;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.FORMA;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ERA;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_ID;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RARITY;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_TABLE;

import android.app.Application;
import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.database.Mission;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.RelicDao;
import com.warframefarm.database.RelicRewardComplete;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.List;

public class RelicRepository {

    private final RelicRewardRepository relicRewardRepository;
    private final MissionRepository missionRepository;

    private final RelicDao relicDao;

    public RelicRepository(Application application) {
        relicRewardRepository = new RelicRewardRepository(application);
        missionRepository = new MissionRepository(application);

        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        relicDao = database.relicDao();
    }

    public RelicComplete getRelic(String relicID) {
        return relicDao.getRelic(relicID);
    }

    public LiveData<List<RelicComplete>> getRelics(String filter, String search, String order) {
        String queryString = "SELECT " + RELIC_TABLE + ".*, COALESCE(" + R_REWARD_RARITY + ", 0) AS " + R_REWARD_RARITY +
                " FROM " + RELIC_TABLE +
                " LEFT JOIN (" +
                    " SELECT " + R_REWARD_RELIC + ", MAX(" + R_REWARD_RARITY + ", 0) AS " + R_REWARD_RARITY +
                    " FROM " + R_REWARD_TABLE +
                    " LEFT JOIN " + USER_COMPONENT_TABLE + " ON " + USER_COMPONENT_ID + " == " + R_REWARD_COMPONENT +
                    " WHERE " + USER_COMPONENT_OWNED + " == 0" +
                    " OR (" + R_REWARD_COMPONENT + " != '" + FORMA + "' AND " + USER_COMPONENT_OWNED + " IS NULL)" +
                    " GROUP BY " + R_REWARD_RELIC +
                ") ON " + R_REWARD_RELIC + " == " + RELIC_ID;

        boolean hasCondition = false;
        if (!search.equals("")) {
            queryString += " WHERE (" + RELIC_NAME + " LIKE '" + search + "%'" +
                        " OR " + RELIC_ID + " IN (" +
                        " SELECT " + R_REWARD_RELIC +
                        " FROM " + R_REWARD_TABLE +
                        " WHERE " + R_REWARD_COMPONENT + " LIKE '" + search + "%'" +
                    "))";
            hasCondition = true;
        }

        if (!filter.equals("")) {
            if (hasCondition)
                queryString += " AND " + RELIC_ERA + " == '" + filter + "'";
            else
                queryString += " WHERE " + RELIC_ERA + " == '" + filter + "'";
        }

        switch (order) {
            case RELIC_ERA:
                queryString += " ORDER BY " + RELIC_ERA + " == '" + AXI + "', " +
                        RELIC_ERA + " == '" + NEO + "', " +
                        RELIC_ERA + " == '" + MESO + "', " +
                        RELIC_ERA + " == '" + LITH + "', " +
                        RELIC_NAME;
                break;

            case RELIC_VAULTED:
                queryString += " ORDER BY " + RELIC_VAULTED + ", " +
                        RELIC_ERA + " == '" + AXI + "', " +
                        RELIC_ERA + " == '" + NEO + "', " +
                        RELIC_ERA + " == '" + MESO + "', " +
                        RELIC_ERA + " == '" + LITH + "', " +
                        RELIC_NAME;
                break;

            case RELIC_NEEDED:
                queryString += " ORDER BY " + R_REWARD_RARITY + " == 0 DESC, " +
                        RELIC_VAULTED + " , " +
                        RELIC_ERA + " == '" + AXI + "', " +
                        RELIC_ERA + " == '" + NEO + "', " +
                        RELIC_ERA + " == '" + MESO + "', " +
                        RELIC_ERA + " == '" + LITH + "', " +
                        R_REWARD_RARITY + ", " +
                        RELIC_NAME;
        }

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return relicDao.getRelics(query);
    }

    public List<RelicComplete> getRelicsForPrime(String prime, String filter, String search) {
        String queryString = "SELECT *" +
                " FROM " + RELIC_TABLE +
                " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                " LEFT JOIN " + COMPONENT_TABLE + " ON " + COMPONENT_ID + " == " + R_REWARD_COMPONENT +
                " LEFT JOIN " + PRIME_TABLE + " ON " + PRIME_NAME + " == " + COMPONENT_PRIME +
                " WHERE " + R_REWARD_COMPONENT + " LIKE '" + prime + "%'";

        if (!search.equals(""))
            queryString += " AND (" + R_REWARD_COMPONENT + " LIKE '" + prime + " " + search + "%'" +
                    " OR " + RELIC_ERA + " LIKE '" + search + "%'" +
                    " OR " + RELIC_NAME + " LIKE '" + search + "%')";

        if (filter.equals(COMPONENT_ID)) {
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
        List<RelicComplete> relics = new ArrayList<>();
        if (cursor != null) {
            RelicComplete relic = null;
            String prev_id = "", id, era, name;
            boolean vaulted;

            String component_id, prime_name, component, type;
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
                if (!id.equals(prev_id) || relic == null) {
                    era = cursor.getString(col_era);
                    name = cursor.getString(col_name);
                    vaulted = cursor.getInt(col_vaulted) > 0;

                    if (relic != null)
                        relics.add(relic);

                    relic = new RelicComplete(id, era, name, vaulted, 0);
                    prev_id = id;
                }

                component_id = cursor.getString(col_component_id);
                prime_name = cursor.getString(col_prime);
                component = cursor.getString(col_component);
                type = cursor.getString(col_type);
                needed = cursor.getInt(col_needed);
                rarity = cursor.getInt(col_rarity);

                relic.addNeededReward(
                        new RelicRewardComplete(id, rarity, component_id, prime_name, component,
                                type, needed, false, false)
                );
            }
            if (relic != null)
                relics.add(relic);

            cursor.close();
        }
        return relics;
    }

    public LiveData<List<RelicComplete>> getRelicsForComponent(String componentID, String search) {
        String queryString = "SELECT *" +
                " FROM " + RELIC_TABLE +
                " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                " LEFT JOIN " + COMPONENT_TABLE + " ON " + COMPONENT_ID + " == " + R_REWARD_COMPONENT +
                " LEFT JOIN " + PRIME_TABLE + " ON " + PRIME_NAME + " == " + COMPONENT_PRIME +
                " WHERE " + R_REWARD_COMPONENT + " == '" + componentID + "'";

        if (!search.equals(""))
            queryString += " AND (" + RELIC_ERA + " LIKE '" + search + "%'" +
                    " OR " + RELIC_NAME + " LIKE '" + search + "%')";

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return relicDao.getRelics(query);
    }

    public List<RelicComplete> getRelicsForItems(List<String> items, boolean showVaulted) {
        List<RelicComplete> relics = new ArrayList<>();
        if (items.isEmpty())
            return relics;

        String queryString = "SELECT *" +
                " FROM " + RELIC_TABLE +
                " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                " LEFT JOIN " + COMPONENT_TABLE + " ON " + COMPONENT_ID + " == " + R_REWARD_COMPONENT +
                " LEFT JOIN " + PRIME_TABLE + " ON " + PRIME_NAME + " == " + COMPONENT_PRIME +
                " WHERE ";

        int size = items.size();
        String item = items.get(0);
        if (size == 1) {
            queryString += R_REWARD_COMPONENT + " LIKE '" + item + "%'";
        }
        else {
            queryString += "(";
            for (int i = 0; i < size; i++) {
                item = items.get(i);
                if (i != 0) queryString += " OR ";
                queryString += R_REWARD_COMPONENT + " LIKE '" + item + "%'";
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
                        relics.add(relic);

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
                relics.add(relic);

            cursor.close();
        }
        return relics;
    }

    public List<RelicRewardComplete> getRelicRewards(String relicID) {
        return relicRewardRepository.getRelicRewards(relicID);
    }

    public List<Mission> getMissions(String relicID, String filter, String search) {
        return missionRepository.getMissionsForRelic(relicID, filter, search);
    }
}
