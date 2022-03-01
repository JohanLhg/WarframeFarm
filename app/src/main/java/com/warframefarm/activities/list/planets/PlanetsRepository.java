package com.warframefarm.activities.list.planets;

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
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_PART;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_TABLE;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.Planet;
import com.warframefarm.database.PlanetDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class PlanetsRepository {

    private final PlanetDao planetDao;

    private final Executor backgroundThread, mainThread;

    public String search = "";

    private final MutableLiveData<List<Planet>> planets = new MutableLiveData<>(new ArrayList<>());

    public PlanetsRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        planetDao = database.planetDao();

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();
    }

    public void setSearch(String search) {
        this.search = search;
        updatePlanets();
    }

    public LiveData<List<Planet>> getPlanets() {
        return planets;
    }

    public void updatePlanets() {
        backgroundThread.execute(() -> {
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
                        " OR " + R_REWARD_PART + " LIKE \"" + search + "%\"" +
                        " ORDER BY " + PLANET_NAME;
            }

            SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
            List<Planet> planetList = planetDao.getPlanets(query);
            mainThread.execute(() -> planets.setValue(planetList));
        });
    }
}
