package com.warframefarm.repositories;

import static com.warframefarm.database.WarframeFarmDatabase.BEST_PLACES;
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
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ERA;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_ARCHWING;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_EMPYREAN;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_NORMAL;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_COMPONENT_TABLE;

import android.app.Application;
import android.database.Cursor;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.database.Mission;
import com.warframefarm.database.MissionDao;
import com.warframefarm.database.MissionReward;
import com.warframefarm.database.MissionWithRewardTypes;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.List;

public class MissionRepository {

    private final MissionDao missionDao;

    public MissionRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        missionDao = database.missionDao();
    }

    public MissionWithRewardTypes getMission(String name) {
        return missionDao.getMission(name);
    }

    public List<Mission> getMissionsForPrime(String prime, String filter, String search) {
        String queryString = "WITH SELECTED_REWARDS AS (" +
                    " SELECT " + M_REWARD_MISSION + ", " + M_REWARD_RELIC +
                    ", SUM(" + M_REWARD_DROP_CHANCE + ") AS " + M_REWARD_DROP_CHANCE + ", " + M_REWARD_ROTATION +
                    " FROM " + M_REWARD_TABLE +
                    " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                    " LEFT JOIN " + MISSION_TABLE + " ON " + MISSION_NAME + " == " + M_REWARD_MISSION +
                    " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                    " WHERE " + R_REWARD_COMPONENT + " LIKE '" + prime + "%'" +
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
        List<Mission> missions = new ArrayList<>();
        if (cursor != null) {
            Mission mission = null;
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
                    mission = new Mission(mission_name, mission_planet, mission_objective, mission_faction, mission_type);
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

    public List<Mission> getMissionsForComponent(String componentID, String filter, String search) {
        String queryString = "WITH SELECTED_REWARDS AS (" +
                    " SELECT " + M_REWARD_MISSION + ", " + M_REWARD_RELIC +
                    ", SUM(" + M_REWARD_DROP_CHANCE + ") AS " + M_REWARD_DROP_CHANCE + ", " + M_REWARD_ROTATION +
                    " FROM " + M_REWARD_TABLE +
                    " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                    " LEFT JOIN " + MISSION_TABLE + " ON " + MISSION_NAME + " == " + M_REWARD_MISSION +
                    " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + RELIC_ID +
                    " WHERE " + R_REWARD_COMPONENT + " LIKE '" + componentID + "%'" +
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
        List<Mission> missionList = new ArrayList<>();
        if (cursor != null) {
            Mission mission = null;
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
                    mission = new Mission(mission_name, mission_planet, mission_objective, mission_faction, mission_type);
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

        return missionList;
    }

    public List<Mission> getMissionsForPlanet(String planet, boolean relicFilter, int typeFilter, String search) {
        String NEEDED_MISSION_REWARDS = "NEEDED_MISSION_REWARDS";

        String queryString = "WITH " + NEEDED_MISSION_REWARDS + " AS (" +
                    " SELECT DISTINCT " + M_REWARD_TABLE + ".*" +
                    " FROM " + MISSION_TABLE +
                    " LEFT JOIN " + M_REWARD_TABLE + " ON " + M_REWARD_MISSION + " == " + MISSION_NAME +
                    " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + M_REWARD_RELIC +
                    " LEFT JOIN " + USER_COMPONENT_TABLE + " ON " + USER_COMPONENT_ID + " == " + R_REWARD_COMPONENT +
                    " WHERE " + MISSION_PLANET + " == '" + planet + "'" +
                    " AND " + USER_COMPONENT_OWNED + " == 0" +
                ")" +
                " SELECT " + MISSION_TABLE + ".*, COALESCE(" + M_REWARD_RELIC + ", '') AS " + M_REWARD_RELIC +
                ", COALESCE(" + M_REWARD_ROTATION + ", '') AS " + M_REWARD_ROTATION +
                ", COALESCE(SUM(" + M_REWARD_DROP_CHANCE + "), 0) AS " + M_REWARD_DROP_CHANCE +
                " FROM " + MISSION_TABLE +
                " LEFT JOIN " + NEEDED_MISSION_REWARDS + " ON " + M_REWARD_MISSION + " == " + MISSION_NAME +
                " WHERE " + MISSION_PLANET + " == '" + planet + "'" +
                (typeFilter == -1 ?
                        "" :
                        " AND " + MISSION_TYPE + " == " + typeFilter
                ) +
                (search.isEmpty() ?
                        "" :
                        " AND (" + MISSION_NAME + " LIKE \"" + search + "%\"" +
                                " OR " + MISSION_OBJECTIVE + " LIKE \"" + search + "%\"" +
                                " OR " + MISSION_TYPE + " == (CASE" +
                                " WHEN 'Normal' LIKE \"" + search + "%\" THEN " + TYPE_NORMAL +
                                " WHEN 'Archwing' LIKE \"" + search + "%\" THEN " + TYPE_ARCHWING +
                                " WHEN 'Empyrean' LIKE \"" + search + "%\"" +
                                " OR 'Railjack' LIKE \"" + search + "%\" THEN " + TYPE_EMPYREAN +
                                " END)" +
                                " OR " + MISSION_FACTION + " LIKE \"" + search + "%\")"
                ) +
                (relicFilter ? " AND " + M_REWARD_RELIC + " != ''" : "") +
                " GROUP BY " + MISSION_NAME + ", " + M_REWARD_ROTATION +
                " ORDER BY " + M_REWARD_DROP_CHANCE + " == 0 DESC, " + MISSION_TYPE + ", " + MISSION_NAME + ", " + M_REWARD_ROTATION;

        List<Mission> missions = new ArrayList<>();

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        Cursor cursor = missionDao.getMissions(query);
        if (cursor != null) {
            Mission mission = null;
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
                    mission = new Mission(mission_name, mission_planet, mission_objective, mission_faction, mission_type);
                    missions.add(mission);
                }

                reward_relic = cursor.getString(col_relic);
                if (reward_relic.equals(""))
                    continue;
                reward_rotation = cursor.getString(col_rotation);
                reward_rarity = cursor.getDouble(col_rarity);
                reward = new MissionReward(mission_name, reward_relic, reward_rotation, reward_rarity);
                mission.addMissionReward(reward);
            }
            cursor.close();
        }
        return missions;
    }

    public List<Mission> getMissionsForRelic(String relicID, String filter, String search) {
        String queryString = "WITH SELECTED_REWARDS AS (" +
                    " SELECT " + M_REWARD_MISSION + ", " + M_REWARD_RELIC +
                    ", SUM(" + M_REWARD_DROP_CHANCE + ") AS " + M_REWARD_DROP_CHANCE + ", " + M_REWARD_ROTATION +
                    " FROM " + M_REWARD_TABLE +
                    " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                    " LEFT JOIN " + MISSION_TABLE + " ON " + MISSION_NAME + " == " + M_REWARD_MISSION +
                    " WHERE " + RELIC_ID + " == '" + relicID + "'" +
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
        List<Mission> missionList = new ArrayList<>();
        if (cursor != null) {
            Mission mission = null;
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
                    mission = new Mission(mission_name, mission_planet, mission_objective, mission_faction, mission_type);
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

        return missionList;
    }

    public List<Mission> getMissionsForItems(List<String> items, boolean bestPlaces) {
        if (items.isEmpty())
            return new ArrayList<>();

        String whereClause = "";
        int size = items.size();
        String item = items.get(0);
        if (size == 1) {
            whereClause += " WHERE " + R_REWARD_COMPONENT + " LIKE '" + item + "%'";
        }
        else {
            whereClause += " WHERE (";
            for (int i = 0; i < size; i++) {
                item = items.get(i);
                if (i != 0) whereClause += " OR ";
                whereClause += R_REWARD_COMPONENT + " LIKE '" + item + "%'";
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

        if (bestPlaces) {
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
                    " JOIN SELECTED_REWARDS ON " + M_REWARD_MISSION + " == " + MISSION_NAME +
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
                    " LEFT JOIN MISSION_DROP_CHANCE ON mission == " + MISSION_NAME +
                    " JOIN SELECTED_REWARDS ON " + M_REWARD_MISSION + " == " + MISSION_NAME +
                    " LEFT JOIN " + PLANET_TABLE + " ON " + PLANET_NAME + " == " + MISSION_PLANET +
                    " LEFT JOIN " + RELIC_TABLE + " ON " + RELIC_ID + " == " + M_REWARD_RELIC +
                    " WHERE dropChance != 0" +
                    " ORDER BY dropChance DESC, " +
                    MISSION_NAME + ", " +
                    M_REWARD_ROTATION;
        }

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        Cursor cursor = missionDao.getMissions(query);
        List<Mission> missions = new ArrayList<>();
        if (cursor != null) {
            Mission mission = null;
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
                    mission = new Mission(mission_name, mission_planet, mission_objective, mission_faction, mission_type);
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

    public List<Integer> getMissionTypes(String name) {
        return missionDao.getPlanetMissionTypes(name);
    }
}
