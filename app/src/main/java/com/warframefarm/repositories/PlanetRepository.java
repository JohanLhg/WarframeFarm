package com.warframefarm.repositories;

import static com.warframefarm.database.WarframeFarmDatabase.MISSION_FACTION;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_OBJECTIVE;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_PLANET;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_MISSION;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.M_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_FACTION;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_COMPONENT;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_TABLE;

import android.app.Application;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.database.Mission;
import com.warframefarm.database.Planet;
import com.warframefarm.database.PlanetDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.List;

public class PlanetRepository {

    private final MissionRepository missionRepository;

    private final PlanetDao planetDao;

    public PlanetRepository(Application application) {
        missionRepository = new MissionRepository(application);

        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        planetDao = database.planetDao();
    }

    public Planet getPlanet(String name) {
        return planetDao.getPlanet(name);
    }

    public List<Planet> getPlanets(String search) {
        String queryString;
        if (search.isEmpty()) {
            queryString = "SELECT " + PLANET_NAME + ", " + PLANET_FACTION +
                    " FROM " + PLANET_TABLE +
                    " ORDER BY " + PLANET_NAME;
        }
        else {
            queryString = "SELECT DISTINCT " + PLANET_NAME + ", " + PLANET_FACTION +
                    " FROM " + PLANET_TABLE +
                    " LEFT JOIN " + MISSION_TABLE + " ON " + MISSION_PLANET + " == " + PLANET_NAME +
                    " LEFT JOIN " + M_REWARD_TABLE + " ON " + M_REWARD_MISSION + " == " + MISSION_NAME +
                    " LEFT JOIN " + R_REWARD_TABLE + " ON " + R_REWARD_RELIC + " == " + M_REWARD_RELIC +
                    " WHERE " + PLANET_NAME + " LIKE \"" + search + "%\"" +
                    " OR " + MISSION_NAME + " LIKE \"" + search + "%\"" +
                    " OR " + MISSION_OBJECTIVE + " LIKE \"" + search + "%\"" +
                    " OR " + MISSION_FACTION + " LIKE \"" + search + "%\"" +
                    " OR " + M_REWARD_RELIC + " LIKE \"" + search + "%\"" +
                    " OR " + R_REWARD_COMPONENT + " LIKE \"" + search + "%\"" +
                    " ORDER BY " + PLANET_NAME;
        }

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return planetDao.getPlanets(query);
    }

    public List<Mission> getMissions(String planet, boolean relicFilter, int typeFilter, String search) {
        return missionRepository.getMissionsForPlanet(planet, relicFilter, typeFilter, search);
    }

    public List<Integer> getMissionTypes(String name) {
        return missionRepository.getMissionTypes(name);
    }
}
